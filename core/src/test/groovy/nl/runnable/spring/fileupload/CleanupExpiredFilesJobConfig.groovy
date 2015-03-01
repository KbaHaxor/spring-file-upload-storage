package nl.runnable.spring.fileupload

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author Laurens Fridael
 */
@Configuration
class CleanupExpiredFilesJobConfig {

  @Bean
  static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
    def configurer = new PropertyPlaceholderConfigurer()
    def properties = new Properties()
    properties.put("spring-file-upload-storage.database.cleanupInterval", "1");
    configurer.properties = properties
    return configurer;
  }
}
