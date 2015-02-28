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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JDBC-based {@link nl.runnable.spring.fileupload.MultipartFileStorage} implementation.
 *
 * @author Laurens Fridael
 */
public class JdbcMultipartFileStorage implements MultipartFileStorage, InitializingBean {

  private static final int DEFAULT_TTL = 1800; // 30 minutes

  private final Logger logger = LoggerFactory.getLogger(getClass());

  /* Dependencies */

  private JdbcOperations jdbc;

  private final LobHandler lobHandler = new DefaultLobHandler();

  private IdGenerator idGenerator = new UuidGenerator();

  /* Configuration */

  @Value("${spring-file-upload-storage.database.init:true}")
  private boolean initDatabaseAutomatically = true;

  /* Main operations */

  @NotNull
  @Override
  public String save(@NotNull final MultipartFile file, int timeToLiveInSeconds, @Nullable final String username) {
    Assert.notNull(file, "File cannot be null.");
    if (file.getSize() > Integer.MAX_VALUE) {
      throw new IllegalArgumentException(String.format("Cannot store files larger than %d bytes.", Integer.MAX_VALUE));
    }
    Assert.isTrue(timeToLiveInSeconds > 0, "Time to live must be greater than 0.");

    final String id = idGenerator.generateId();
    final Date createdAt = new Date();
    final Date expiresAt = new Date(createdAt.getTime() + timeToLiveInSeconds * 1000);
    logger.info("Saving multipart file '{}'. File expires at: {} ", id, expiresAt);
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
          ps.setString(pos++, username);
          ps.setTimestamp(pos++, new Timestamp(createdAt.getTime()));
          ps.setTimestamp(pos++, new Timestamp(expiresAt.getTime()));
          Assert.state(pos - 1 == SqlConstants.COLUMN_COUNT, "Unexpected column count.");
        } catch (final IOException e) {
          throw new RuntimeException(e);
        }
      }
    });
    return id;
  }

  @Override
  @Nullable
  public StoredMultipartFile find(@NotNull String id) {
    Assert.hasText(id, "File ID cannot be empty.");

    final List<JdbcMultipartFile> files = jdbc.query(SqlConstants.SELECT_BY_ID, new JdbcMultiPartFileResultExtractor
        (), id);
    if (!files.isEmpty()) {
      return files.get(0);
    }
    return null;
  }

  @Override
  @Nullable
  public Date extendTimeToLive(@NotNull String id, int timeToLiveInSeconds) {
    Assert.hasText(id, "File ID cannot be empty.");
    Assert.isTrue(timeToLiveInSeconds > 0, "Time to live must be greater than 0.");

    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public boolean delete(@NotNull String id) {
    Assert.hasText(id, "File ID cannot be empty.");

    final int count = jdbc.update(SqlConstants.DELETE_BY_ID, id);
    return count == 1;
  }

  /* Utility */

  private final class JdbcMultiPartFileResultExtractor implements ResultSetExtractor<List<JdbcMultipartFile>> {
    @Override
    public List<JdbcMultipartFile> extractData(final ResultSet rs) throws SQLException, DataAccessException {
      final List<JdbcMultipartFile> files = new ArrayList<JdbcMultipartFile>();
      while (rs.next()) {
        final JdbcMultipartFile file = new JdbcMultipartFile(jdbc);
        file.setId(rs.getString("id"));
        file.setName(rs.getString("name"));
        file.setOriginalFilename(rs.getString("original_filename"));
        file.setContentType(rs.getString("content_type"));
        file.setSize(rs.getInt("size"));
        file.setUsername(rs.getString("username"));
        file.setCreatedAt(rs.getTimestamp("created_at"));
        file.setExpiresAt(rs.getTimestamp("expires_at"));
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
    Resource resource = new ClassPathResource("META-INF/spring-file-upload-storage/create-schema.sql");
    String sql = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream()));
    logger.info("Creating database using schema:{}", sql);
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
