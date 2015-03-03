package nl.runnable.spring.fileupload.mvc;

import nl.runnable.spring.fileupload.MultipartFileStorage;
import nl.runnable.spring.fileupload.impl.AbstractSessionAssociatedMultipartFileStorage;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Laurens Fridael
 */
class HttpSessionMultipartFileStorage extends AbstractSessionAssociatedMultipartFileStorage {

  private final HttpServletRequest request;

  public HttpSessionMultipartFileStorage(@NotNull MultipartFileStorage storage, @NotNull HttpServletRequest request) {
    super(storage);
    Assert.notNull(request);
    this.request = request;
  }

  @NotNull
  protected String getSessionId() {
    return request.getSession(true).getId();
  }

}
