package nl.runnable.spring.fileupload.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @author Laurens Fridael
 */
@Configuration
@ComponentScan
public class DefaultFileUploadMvcConfig extends WebMvcConfigurerAdapter{

  @Bean
  StoredMultipartFileReturnValueHandler storedMultipartFileReturnValueHandler() {
    return new StoredMultipartFileReturnValueHandler();
  }

  @Configuration
  static class MvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private StoredMultipartFileReturnValueHandler returnValueHandler;

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
      returnValueHandlers.add(returnValueHandler);
    }
  }

}
