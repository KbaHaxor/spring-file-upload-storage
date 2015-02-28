package nl.runnable.spring.fileupload;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

/**
 * Value Object that represents a multipart file stored in a {@link MultipartFileRepository}.
 *
 * @author Laurens Fridael
 */
public class StoredMultipartFile {

  private final String id;

  private final MultipartFile file;

  public StoredMultipartFile(@NotNull MultipartFile file, @NotNull String id) {
    Assert.notNull(file, "MultipartFile cannot be null.");
    Assert.hasText(id, "ID cannot be empty.");
    this.file = file;
    this.id = id;
  }

  /**
   * Obtains the ID.
   *
   * @return The ID
   */
  public String getId() {
    return id;
  }

  /**
   * Obtains the MultipartFile.
   *
   * @return The MultipartFile.
   */
  public MultipartFile getFile() {
    return file;
  }
}
