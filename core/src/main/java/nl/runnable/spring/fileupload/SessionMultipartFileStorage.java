package nl.runnable.spring.fileupload;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Defines storage operations for {@link org.springframework.web.multipart.MultipartFile}s that are bound to a session.
 *
 * @author Laurens Fridael
 */
public interface SessionMultipartFileStorage {

  /**
   * Saves the file in the current session.
   *
   * @param file                The file.
   * @param timeToLiveInSeconds The time to live.
   * @return The ID of the stored  file.
   * @see nl.runnable.spring.fileupload.MultipartFileStorage#save(org.springframework.web.multipart.MultipartFile,
   * int, String)
   */
  @NotNull
  String save(@NotNull MultipartFile file, int timeToLiveInSeconds);

  /**
   * Saves the file in the current session using a predefined ID.
   *
   * @param file                The file.
   * @param id                  The ID.
   * @param timeToLiveInSeconds The time to live.
   */
  void save(@NotNull MultipartFile file, @NotNull String id, int timeToLiveInSeconds);

  /**
   * Finds a file in the current session.
   *
   * @param id The file's ID.
   * @return The matching file or {@literal null} if no match was found in the current session.
   */
  @Nullable
  StoredMultipartFile find(@NotNull String id);

  /**
   * Obtains all files in the current session.
   *
   * @return The available files or an empty list if no files are available.
   */
  @NotNull
  List<StoredMultipartFile> findAll();

  /**
   * Deletes the given file from the current session.
   *
   * @param id The file's ID
   * @return The number of files deleted, either 0 or 1.
   */
  int delete(@NotNull String id);

  /**
   * Deletes all file in the current session.
   *
   * @return The number of files deleted.
   */
  int deleteAll();

  /**
   * Sets the time-to-live of the files in the current session.
   *
   * @param timeToLiveInSeconds The time-to-live.
   */
  void setTimeToLive(int timeToLiveInSeconds);

}
