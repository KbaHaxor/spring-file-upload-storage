package nl.runnable.spring.fileupload;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>Defines data access operations for {@link MultipartFile}s.</p>
 *
 * @author Laurens Fridael
 */
public interface MultipartFileRepository {

  /**
   * Saves a multipart file to the repository.
   *
   * @param file The multipart file.
   * @return The StoredMultipartFile.
   */
  StoredMultipartFile save(@NotNull MultipartFile file);

  /**
   * Obtains a StoredMultipartFile by ID.
   *
   * @param id The ID
   * @return The matching file or {@literal null} if no match was found.
   */
  StoredMultipartFile find(@NotNull String id);
}
