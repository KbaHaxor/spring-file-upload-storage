package nl.runnable.spring.fileupload.impl;

import nl.runnable.spring.fileupload.StoredMultipartFile;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author Laurens Fridael
 */
class JdbcMultipartFile implements StoredMultipartFile {

  private final JdbcOperations jdbc;

  private String id;

  private String name;

  private String originalFilename;

  private String contentType;

  private long size;

  private String username;

  private Date createdAt;

  private Date expiresAt;

  JdbcMultipartFile(@NotNull JdbcOperations jdbc) {
    Assert.notNull(jdbc);
    this.jdbc = jdbc;
  }

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setOriginalFilename(final String originalFilename) {
    this.originalFilename = originalFilename;
  }

  @Override
  public String getOriginalFilename() {
    return originalFilename;
  }

  public void setContentType(final String contentType) {
    this.contentType = contentType;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public boolean isEmpty() {
    return getSize() == 0;
  }

  public void setSize(final long size) {
    Assert.isTrue(size <= Integer.MAX_VALUE);
    this.size = size;
  }

  @Override
  public long getSize() {
    return size;
  }

  @Override
  public byte[] getBytes() throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream((int) getSize());
    FileCopyUtils.copy(getInputStream(), buffer);
    return buffer.toByteArray();
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return jdbc.query(SqlConstants.SELECT_DATA_BY_ID, new ResultSetExtractor<InputStream>() {

      @Override
      public InputStream extractData(final ResultSet rs) throws SQLException, DataAccessException {
        InputStream data = null;
        if (rs.next()) {
          data = rs.getBinaryStream(1);
        }
        return data;
      }
    }, id);
  }

  @Override
  public void transferTo(final File dest) throws IOException, IllegalStateException {
    FileCopyUtils.copy(getInputStream(), new FileOutputStream(dest));
  }


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(Date expiresAt) {
    this.expiresAt = expiresAt;
  }
}
