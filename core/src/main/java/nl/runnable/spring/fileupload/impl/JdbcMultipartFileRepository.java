package nl.runnable.spring.fileupload.impl;

import nl.runnable.spring.fileupload.MultipartFileRepository;
import nl.runnable.spring.fileupload.StoredMultipartFile;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

/**
 * JDBC-based {@link nl.runnable.spring.fileupload.MultipartFileRepository} implementation.
 *
 * @author Laurens Fridael
 */
public class JdbcMultipartFileRepository implements MultipartFileRepository {

  @Override
  public StoredMultipartFile save(@NotNull MultipartFile file) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public StoredMultipartFile find(@NotNull String id) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

}
