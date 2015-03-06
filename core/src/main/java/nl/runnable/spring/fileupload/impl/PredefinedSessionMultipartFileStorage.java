package nl.runnable.spring.fileupload.impl;

import nl.runnable.spring.fileupload.MultipartFileStorage;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

/**
 * {@link nl.runnable.spring.fileupload.SessionMultipartFileStorage} implementation that uses a predefined session
 * identifier. This implementation is suitable for cases where the session is managed outside of the web 
 * container.
 *
 * @author Laurens Fridael
 */
public class PredefinedSessionMultipartFileStorage extends AbstractSessionMultipartFileStorage {

  private final String sessionId;

  public PredefinedSessionMultipartFileStorage(@NotNull MultipartFileStorage storage, @NotNull String sessionId) {
    super(storage);
    Assert.hasText(sessionId, "Session ID cannot be empty.");
    this.sessionId = sessionId;
  }

  @NotNull
  @Override
  public String getSessionId() {
    return sessionId;
  }
  
}
