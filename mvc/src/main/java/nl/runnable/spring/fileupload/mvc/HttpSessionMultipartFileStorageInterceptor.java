package nl.runnable.spring.fileupload.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Laurens Fridael
 */
public class HttpSessionMultipartFileStorageInterceptor extends HandlerInterceptorAdapter {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                              Exception ex) throws Exception {
    HttpSessionMultipartFileStorage sessionStorage = (HttpSessionMultipartFileStorage) request.getAttribute
        (HttpSessionMultipartFileStorageArgumentResolver.REQUEST_ATTRIBUTE);
    if (sessionStorage != null) {
      HttpSession session = request.getSession(false);
      if (session != null) {
        int timeToLiveInSeconds = session.getMaxInactiveInterval();
        logger.debug("Setting time-to-live for session-bound files to {} seconds", timeToLiveInSeconds);
        sessionStorage.setTimeToLive(timeToLiveInSeconds);
      }
    }
  }
}
