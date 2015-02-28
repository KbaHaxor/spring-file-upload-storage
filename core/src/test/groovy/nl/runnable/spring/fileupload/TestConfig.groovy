package nl.runnable.spring.fileupload

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.SingleConnectionDataSource

import javax.sql.DataSource

/**
 * @author Laurens Fridael
 */
@Configuration
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
