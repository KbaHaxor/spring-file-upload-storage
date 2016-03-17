package nl.runnable.spring.fileupload.impl;

import nl.runnable.spring.fileupload.MultipartFileStorage;
import nl.runnable.spring.fileupload.SessionMultipartFileStorage;
import nl.runnable.spring.fileupload.StoredMultipartFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Abstract base class for {@link nl.runnable.spring.fileupload.SessionMultipartFileStorage} implementations.
 * <p>
 * This implementation is a wrapper around {@link nl.runnable.spring.fileupload.MultipartFileStorage}, associating
 * files with the session using the session identifier. I.e. the implementation does not store the files in the session
 * itself. The management and lifecycle of the session is therefore independent of that of the file storage.
 * </p>
 * <p>
 * This implementation assumes that sessions are kept alive using a timeout mechanism. I.e. the file's time-to-live
 * should be extended.
 * </p>
 * <p>
 * Subclasses must provide an implementation of {@link #getSessionId}.
 * </p>
 *
 * @author Laurens Fridael
 */
public abstract class AbstractSessionMultipartFileStorage implements SessionMultipartFileStorage {

  private final MultipartFileStorage storage;

  protected AbstractSessionMultipartFileStorage(@NotNull MultipartFileStorage storage) {
    Assert.notNull(storage);

    this.storage = storage;
  }

  @NotNull
  @Override
  public String save(@NotNull MultipartFile file, int timeToLiveInSeconds) {
    Assert.notNull(file, "File cannot be null.");
    Assert.isTrue(timeToLiveInSeconds > 0, "Time to live must be greater than or equal to 0.");

    return save(file, timeToLiveInSeconds, null);
  }

  @NotNull
  @Override
  public String save(@NotNull MultipartFile file, int timeToLiveInSeconds, @Nullable String metadata) {
    return storage.save(file, timeToLiveInSeconds, getSessionId(), metadata);
  }

  @Override
  public void save(@NotNull MultipartFile file, @NotNull String id, int timeToLiveInSeconds) {
    save(file, id, timeToLiveInSeconds, null);
  }

  @Override
  public void save(@NotNull MultipartFile file, @NotNull String id, int timeToLiveInSeconds,
                   @Nullable String metadata) {
    storage.save(file, id, timeToLiveInSeconds, getSessionId(), metadata);
  }


  @Nullable
  @Override
  public StoredMultipartFile find(@NotNull String id) {
    Assert.hasText(id, "ID cannot be empty.");

    StoredMultipartFile file = storage.find(id);
    if (isBoundToSession(file)) {
      return file;
    } else {
      return null;
    }
  }

  @NotNull
  @Override
  public List<StoredMultipartFile> findAll() {
    return storage.findByContext(getSessionId());
  }

  @Override
  public int delete(@NotNull String id) {
    Assert.hasText(id, "ID cannot be empty.");

    StoredMultipartFile file = find(id);
    if (isBoundToSession(file)) {
      return storage.delete(file.getId());
    }
    return 0;
  }

  @Override
  public int deleteAll() {
    return storage.deleteByContext(getSessionId());
  }

  @Override
  public void setTimeToLive(int timeToLiveInSeconds) {
    for (StoredMultipartFile file : findAll()) {
      storage.setTimeToLive(file.getId(), timeToLiveInSeconds);
    }
  }

  @Override
  public int setMetadata(@NotNull String id, @Nullable String metadata) {
    StoredMultipartFile file = find(id);
    if (isBoundToSession(file)) {
      return storage.setMetadata(id, metadata);
    }
    return 0;
  }

  /**
   * Tests if the given file is bound to the current session.
   *
   * @param file
   * @return {@code true} if the file is not {@code null} and has a context that is equal to the session ID.
   * @see StoredMultipartFile#getContext()
   * @see #getSessionId()
   */
  private boolean isBoundToSession(@Nullable StoredMultipartFile file) {
    return file != null && getSessionId().equals(file.getContext());
  }

}
