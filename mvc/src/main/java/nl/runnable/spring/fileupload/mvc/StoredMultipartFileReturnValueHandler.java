package nl.runnable.spring.fileupload.mvc;

import nl.runnable.spring.fileupload.StoredMultipartFile;
import org.springframework.core.MethodParameter;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Laurens Fridael
 */
public class StoredMultipartFileReturnValueHandler implements HandlerMethodReturnValueHandler {

  @Override
  public boolean supportsReturnType(MethodParameter returnType) {
    return StoredMultipartFile.class.isAssignableFrom(returnType.getParameterType());
  }

  @Override
  public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest) throws Exception {
    HttpServletResponse response = (HttpServletResponse) webRequest.getNativeResponse();
    if (returnValue == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    StoredMultipartFile multipartFile = (StoredMultipartFile) returnValue;
    response.setContentType(multipartFile.getContentType());
    response.setContentLength((int) multipartFile.getSize());
    FileCopyUtils.copy(multipartFile.getInputStream(), response.getOutputStream());
  }
}
