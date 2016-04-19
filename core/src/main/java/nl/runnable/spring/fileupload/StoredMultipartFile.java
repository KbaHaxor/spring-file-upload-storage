package nl.runnable.spring.fileupload;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * Represents {@link MultipartFile}s stored in {@link nl.runnable.spring.fileupload.MultipartFileStorage}.
 *
 * @author Laurens Fridael
 */
public interface StoredMultipartFile extends MultipartFile {

  /**
   * Obtains the file's ID.
   *
   * @return
   */
  @NotNull
  String getId();

  /**
   * Obtains the file's context. The context is used to bind files to, for example, a user session.
   * <p>The context is used by {@link SessionMultipartFileStorage}.</p>
   *
   * @return
   */
  @Nullable
  String getContext();

  /**
   * Obtains the file's metadata.
   * <p>
   * Metadata is application-specific data, for example a JSON or XML fragment, that can be saved along with the
   * file itself.
   * </p>
   *
   * @return
   * @see MultipartFileStorage#save(MultipartFile, int, String, String)
   * @see MultipartFileStorage#setMetadata(String, String)
   * @see SessionMultipartFileStorage#save(MultipartFile, int, String)
   * @see SessionMultipartFileStorage#setMetadata(String, String)
   */
  @Nullable
  String getMetadata();

  /**
   * Obtains the file's creation date/time.
   *
   * @return
   */
  @NotNull
  Date getCreatedAt();

  /**
   * Obtains the file's expiration date/time.
   *
   * @return
   */
  @NotNull
  Date getExpiresAt();

}
