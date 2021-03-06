package nl.runnable.spring.fileupload.impl;

import nl.runnable.spring.fileupload.IdGenerator;
import nl.runnable.spring.fileupload.MultipartFileStorage;
import nl.runnable.spring.fileupload.StoredMultipartFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JDBC-based {@link nl.runnable.spring.fileupload.MultipartFileStorage} implementation.
 *
 * @author Laurens Fridael
 */
public class JdbcMultipartFileStorage implements MultipartFileStorage, InitializingBean {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  /* Dependencies */

  private JdbcOperations jdbc;

  private final LobHandler lobHandler = new DefaultLobHandler();

  private IdGenerator idGenerator = new UuidGenerator();

  /* Configuration */

  @Value("${spring-file-upload-storage.database.autoInit:true}")
  private boolean initDatabaseAutomatically = true;

  /* Main operations */

  @NotNull
  @Override
  public String save(@NotNull final MultipartFile file, int timeToLiveInSeconds, @Nullable final String context,
                     @Nullable String metadata) {
    final String id = idGenerator.generateId();
    save(file, id, timeToLiveInSeconds, context, metadata);
    return id;
  }

  @Override
  public void save(@NotNull final MultipartFile file, @NotNull final String id, int timeToLiveInSeconds,
                   final @Nullable String context, final @Nullable String metadata) {
    Assert.notNull(file, "File cannot be null.");
    if (file.getSize() > Integer.MAX_VALUE) {
      throw new IllegalArgumentException(String.format("Cannot store files larger than %d bytes.", Integer.MAX_VALUE));
    }
    Assert.hasText(id, "ID cannot be empty.");
    Assert.isTrue(timeToLiveInSeconds >= 0, "Time to live must be greater than or equal to 0.");
    Assert.isTrue(context == null || context.length() <= 255, "Context cannot be longer than 255 characters");
    Assert.isTrue(metadata == null || metadata.length() <= 255, "Metadata cannot be longer than 255 characters");

    final Date createdAt = new Date();
    final Date expiresAt = new Date(createdAt.getTime() + timeToLiveInSeconds * 1000);
    logger.debug("Saving multipart file '{}'. Expires at: {} ", id, expiresAt);
    jdbc.execute(SqlConstants.INSERT_INTO, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {

      @Override
      protected void setValues(final PreparedStatement ps, final LobCreator lobCreator)
          throws SQLException, DataAccessException {
        try {
          int pos = 1;
          ps.setString(pos++, id);
          ps.setString(pos++, file.getName());
          ps.setString(pos++, file.getOriginalFilename());
          ps.setString(pos++, file.getContentType());
          ps.setInt(pos++, (int) file.getSize());
          lobCreator.setBlobAsBinaryStream(ps, pos++, file.getInputStream(), (int) file.getSize());
          ps.setString(pos++, context);
          ps.setString(pos++, metadata);
          ps.setLong(pos++, createdAt.getTime());
          ps.setLong(pos++, expiresAt.getTime());
          Assert.state(pos - 1 == SqlConstants.COLUMN_COUNT, "Unexpected column count.");
        } catch (final IOException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }

  @Override
  @Nullable
  public StoredMultipartFile find(@NotNull String id) {
    Assert.hasText(id, "File ID cannot be empty.");

    final List<StoredMultipartFile> files = jdbc.query(SqlConstants.SELECT_BY_ID,
        new JdbcMultiPartFileResultExtractor(), id);
    if (!files.isEmpty()) {
      return files.get(0);
    }
    return null;
  }

  @NotNull
  @Override
  public List<StoredMultipartFile> findByContext(@NotNull String context) {
    Assert.hasText(context, "Context cannot be empty.");

    return jdbc.query(SqlConstants.SELECT_BY_CONTEXT, new JdbcMultiPartFileResultExtractor(), context);
  }

  @Override
  @Nullable
  public Date setTimeToLive(@NotNull String id, int timeToLiveInSeconds) {
    Assert.hasText(id, "File ID cannot be empty.");
    Assert.isTrue(timeToLiveInSeconds >= 0, "Time to live must be greater than or equal to 0.");

    Date now = new Date();
    Date expiresAt = new Date(now.getTime() + timeToLiveInSeconds * 1000);
    int count = jdbc.update(SqlConstants.UPDATE_EXPIRES_AT, expiresAt.getTime(), id);
    if (count > 0) {
      logger.debug("Set expiration of {} file(s) to {}.", count, expiresAt);
    }
    return count == 1 ? expiresAt : null;
  }

  @Override
  public int setMetadata(@NotNull String id, @Nullable String metadata) {
    Assert.hasText(id, "File ID cannot be empty.");

    int count = jdbc.update(SqlConstants.UPDATE_METADATA, metadata, id);
    if (count > 0) {
      logger.debug("Set metadata of {} file(s) to '{}'.", count, metadata);
    }
    return count;
  }

  @Override
  public int delete(@NotNull String id) {
    Assert.hasText(id, "File ID cannot be empty.");

    int count = jdbc.update(SqlConstants.DELETE_BY_ID, id);
    if (count == 1) {
      logger.debug("Deleted file '{}'.", id);
    }
    return count;
  }

  @Override
  public int deleteByContext(@NotNull String context) {
    Assert.hasText(context, "Context cannot be empty.");

    int count = jdbc.update(SqlConstants.DELETE_BY_CONTEXT, context);
    if (count > 1) {
      logger.debug("Deleted {} files with context '{}'.", count, context);
    }
    return count;
  }

  @Override
  public int deleteExpired() {
    Date now = new Date();
    int count = jdbc.update(SqlConstants.DELETE_EXPIRED, now.getTime());
    if (count > 0) {
      logger.debug("Deleted {} expired files.", count);
    }
    return count;
  }

  @Override
  public int deleteAll() {
    int count = jdbc.update(SqlConstants.DELETE_ALL);
    if (count > 0) {
      logger.debug("Deleted {} files.", count);
    }
    return count;
  }

  @Override
  public int count() {
    return jdbc.queryForObject(SqlConstants.COUNT, Integer.class);
  }

  /* Utility */

  private final class JdbcMultiPartFileResultExtractor implements ResultSetExtractor<List<StoredMultipartFile>> {
    @Override
    public List<StoredMultipartFile> extractData(final ResultSet rs) throws SQLException, DataAccessException {
      final List<StoredMultipartFile> files = new ArrayList<StoredMultipartFile>();
      while (rs.next()) {
        final JdbcMultipartFile file = new JdbcMultipartFile(jdbc);
        file.setId(rs.getString("id"));
        file.setName(rs.getString("name"));
        file.setOriginalFilename(rs.getString("original_filename"));
        file.setContentType(rs.getString("content_type"));
        file.setSize(rs.getInt("size"));
        file.setContext(rs.getString("context"));
        file.setMetadata(rs.getString("metadata"));
        file.setCreatedAt(new Date(rs.getLong("created_at")));
        file.setExpiresAt(new Date(rs.getLong("expires_at")));
        files.add(file);
      }
      return files;
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (isInitDatabaseAutomatically()) {
      initDatabase();
    }
  }

  private void initDatabase() throws IOException {
    if (isTableAvailable()) {
      return;
    }
    Resource resource = new ClassPathResource("META-INF/spring-file-upload-storage/schema.sql");
    String sql = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream()));
    logger.info("Creating database using schema:\n{}", sql);
    jdbc.execute(sql);
  }


  private boolean isTableAvailable() {
    try {
      jdbc.queryForObject(SqlConstants.SELECT_COUNT, Integer.class);
      return true;
    } catch (DataAccessException e) {
      return false;
    }
  }

  /* Dependencies */

  @Autowired
  @Qualifier("spring-file-upload-storage")
  public void setDataSource(@NotNull DataSource dataSource) {
    Assert.notNull(dataSource);
    jdbc = new JdbcTemplate(dataSource);
  }

  public void setIdGenerator(@NotNull IdGenerator idGenerator) {
    Assert.notNull(idGenerator);
    this.idGenerator = idGenerator;
  }

  /* Configuration */

  public void setInitDatabaseAutomatically(boolean initDatabaseAutomatically) {
    this.initDatabaseAutomatically = initDatabaseAutomatically;
  }

  protected boolean isInitDatabaseAutomatically() {
    return initDatabaseAutomatically;
  }
}
