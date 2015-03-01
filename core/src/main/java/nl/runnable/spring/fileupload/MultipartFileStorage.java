package nl.runnable.spring.fileupload;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * <p>Defines data access operations for {@link MultipartFile}s.</p>
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
   * @param username            The user name, can be {@literal null}. The username can later be retrieved using
   *                            {@link StoredMultipartFile#getUsername()}.
   * @return The ID under which the file is stored.
   */
  @NotNull
  String save(@NotNull MultipartFile file, int timeToLiveInSeconds, @Nullable String username);

  /**
   * Obtains a file.
   *
   * @param id The file's ID
   * @return The matching file or {@literal null} if no match was found.
   */
  @Nullable
  StoredMultipartFile find(@NotNull String id);

  /**
   * Sets a file's time to live.
   *
   * @param id                  The file ID.
   * @param timeToLiveInSeconds The time to live from now.
   * @return The new date/time at which the file is due to expire or {@literal null} if no matching file was found.
   */
  @Nullable
  Date setTimeToLive(@NotNull String id, int timeToLiveInSeconds);

  /**
   * Deletes a file.
   *
   * @param id The file's ID.
   * @return {@literal true} if the file was found and deleted, {@literal false} if not.
   */
  boolean delete(@NotNull String id);

  /**
   * Deletes expired files.
   *
   * @return The number of files deleted.
   */
  int deleteExpired();

}
