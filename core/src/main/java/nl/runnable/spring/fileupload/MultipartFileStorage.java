package nl.runnable.spring.fileupload;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * <p>Defines storage operations for {@link MultipartFile}s.</p>
 *
 * @author Laurens Fridael
 */
public interface MultipartFileStorage {

  public static final int TTL_30_MINUTES = 30 * 60;

  public static final int TTL_1_HOUR = 60 * 60;

  /**
   * Saves a multipart file.
   *
   * @param file                The multipart file.
   * @param timeToLiveInSeconds The time to keep the file in storage. After this time expires, the file becomes
   *                            eligible for cleanup.
   * @param context             Optional context value to associate with this file. The context can, for example, be the
   *                            username or the HTTP session ID. The purpose of the context is to make application code
   *                            responsible for managing client state. The context can be retrieved using
   *                            {@link StoredMultipartFile#getContext()}.
   * @param metadata            Optional metadata
   * @return The ID of the stored file.
   */
  @NotNull
  String save(@NotNull MultipartFile file, int timeToLiveInSeconds, @Nullable String context,
              @Nullable String metadata);

  /**
   * Saves multipart file using a predefined ID.
   *
   * @param file                The multipart file.
   * @param id                  The ID>
   * @param timeToLiveInSeconds The time to keep the file in storage. After this time expires, the file becomes
   *                            eligible for cleanup.
   * @param context             Optional context value to associate with this file. The context can, for example, be the
   *                            username or the HTTP session ID. The purpose of the context is to make application code
   *                            responsible for managing client state. The context can be retrieved using
   *                            {@link StoredMultipartFile#getContext()}.
   * @param metadata            Optional metadata
   */
  void save(@NotNull MultipartFile file, @NotNull String id, int timeToLiveInSeconds, @Nullable String context,
            @Nullable String metadata);

  /**
   * Obtains a file.
   *
   * @param id The file's ID
   * @return The matching file or {@code null} if no match was found.
   */
  @Nullable
  StoredMultipartFile find(@NotNull String id);

  /**
   * Obtains files matching a given context.
   *
   * @param context The context to filter against.
   * @return The matching files, sorted by the time they were created.
   */
  @NotNull
  List<StoredMultipartFile> findByContext(@NotNull String context);

  /**
   * Sets a file's time to live.
   *
   * @param id                  The file ID.
   * @param timeToLiveInSeconds The time to live from now.
   * @return The new date/time at which the file is due to expire or {@code null} if no matching file was found.
   */
  @Nullable
  Date setTimeToLive(@NotNull String id, int timeToLiveInSeconds);

  /**
   * Sets a file's metadata.
   *
   * @param id       The file ID.
   * @param metadata The metadata.
   * @return the number of files updated, either 0 or 1.
   */
  int setMetadata(@NotNull String id, @Nullable String metadata);

  /**
   * Deletes a file.
   *
   * @param id The file's ID.
   * @return The number of files deleted, either 0 or 1.
   */
  int delete(@NotNull String id);

  /**
   * Deletes files matching a given context.
   *
   * @param context The context to filter against.
   * @return The number of files deleted.
   */
  int deleteByContext(@NotNull String context);

  /**
   * Deletes expired files.
   *
   * @return The number of files deleted.
   */
  int deleteExpired();

  /**
   * Deletes all files.
   *
   * @return The number of files deleted.
   */
  int deleteAll();

  /**
   * Obtains the number of files stored.
   *
   * @return The number of files.
   */
  int count();

}
