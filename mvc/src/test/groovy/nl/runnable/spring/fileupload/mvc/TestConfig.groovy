package nl.runnable.spring.fileupload.mvc

import nl.runnable.spring.fileupload.config.DefaultFileUploadConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.datasource.SingleConnectionDataSource
import org.springframework.web.servlet.config.annotation.EnableWebMvc

import javax.sql.DataSource

/**
 * @author Laurens Fridael
 */
@Configuration
@EnableWebMvc
@Import([DefaultFileUploadConfig, DefaultFileUploadMvcConfig])
class TestConfig {

  @Bean
  static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
    return new PropertyPlaceholderConfigurer()
  }

  @Bean
  @Qualifier("spring-file-upload-storage")
  DataSource dataSource() {
    def dataSource = new SingleConnectionDataSource()
    dataSource.url = 'jdbc:h2:mem:.;DB_CLOSE_ON_EXIT=FALSE'
    dataSource.username = 'sa'
    dataSource.password = ''
    return dataSource;
  }

}
