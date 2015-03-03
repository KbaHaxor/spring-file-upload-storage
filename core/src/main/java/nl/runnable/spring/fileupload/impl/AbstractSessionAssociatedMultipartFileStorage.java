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
public abstract class AbstractSessionAssociatedMultipartFileStorage implements SessionMultipartFileStorage {

  private final MultipartFileStorage storage;

  protected AbstractSessionAssociatedMultipartFileStorage(@NotNull MultipartFileStorage storage) {
    Assert.notNull(storage);

    this.storage = storage;
  }

  @NotNull
  @Override
  public String save(@NotNull MultipartFile file, int timeToLiveInSeconds) {
    Assert.notNull(file, "File cannot be null.");
    Assert.isTrue(timeToLiveInSeconds > 0, "Time to live must be greater than or equal to 0.");

    return storage.save(file, timeToLiveInSeconds, getSessionId());
  }

  @Nullable
  @Override
  public StoredMultipartFile find(@NotNull String id) {
    Assert.hasText(id, "ID cannot be empty.");

    StoredMultipartFile file = storage.find(id);
    if (file != null && isBoundToSession(file)) {
      return file;
    }
    return file;
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
    if (file != null && isBoundToSession(file)) {
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
    for (StoredMultipartFile file: findAll()) {
      storage.setTimeToLive(file.getId(), timeToLiveInSeconds);
    }
  }

  private boolean isBoundToSession(@NotNull StoredMultipartFile file) {
    return getSessionId().equals(file.getContext());
  }

  /**
   * Obtains the ID of the current session. Implementations should create a new session if necessary.
   *
   * @return The current session ID.
   */
  @NotNull
  protected abstract String getSessionId();
}
