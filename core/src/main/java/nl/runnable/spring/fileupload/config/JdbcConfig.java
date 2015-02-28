package nl.runnable.spring.fileupload.config;

import nl.runnable.spring.fileupload.MultipartFileStorage;
import nl.runnable.spring.fileupload.impl.JdbcMultipartFileStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Laurens Fridael
 */
@Configuration
public class JdbcConfig {

  @Bean
  MultipartFileStorage multipartFileRepository() {
    return new JdbcMultipartFileStorage();
  }

}
