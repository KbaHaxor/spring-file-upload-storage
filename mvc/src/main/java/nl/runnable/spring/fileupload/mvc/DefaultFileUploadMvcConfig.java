package nl.runnable.spring.fileupload.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @author Laurens Fridael
 */
@Configuration
@ComponentScan
public class DefaultFileUploadMvcConfig extends WebMvcConfigurerAdapter {

  @Bean
  StoredMultipartFileReturnValueHandler storedMultipartFileReturnValueHandler() {
    return new StoredMultipartFileReturnValueHandler();
  }

  @Bean
  HttpSessionMultipartFileStorageArgumentResolver httpSessionMultipartFileStorageArgumentResolver() {
    return new HttpSessionMultipartFileStorageArgumentResolver();
  }

  @Bean
  HttpSessionMultipartFileStorageInterceptor httpSessionMultipartFileStorageInterceptor() {
    return new HttpSessionMultipartFileStorageInterceptor();
  }

  @Configuration
  static class MvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    StoredMultipartFileReturnValueHandler returnValueHandler;

    @Autowired
    HttpSessionMultipartFileStorageArgumentResolver argumentResolver;

    @Autowired
    HttpSessionMultipartFileStorageInterceptor interceptor;

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
      returnValueHandlers.add(returnValueHandler);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
      argumentResolvers.add(argumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(interceptor);
    }
  }

}
