package nl.runnable.spring.fileupload.example.config;

import nl.runnable.spring.fileupload.config.DefaultFileUploadConfig;
import nl.runnable.spring.fileupload.mvc.DefaultFileUploadMvcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.io.File;

/**
 * Example {@code @Configuration} that shows how to set up Spring File Upload Storage in your application.
 * <ul>
 * <li>Import {@link DefaultFileUploadConfig}</li>
 * <li>Import {@link DefaultFileUploadMvcConfig}</li>
 * <li>Provide {@link DataSource} bean with the {@code @Qualifier} "spring-file-upload-storage".</li>
 * </ul>
 * <p>
 * This example sets up a temporary H2 database for storing file uploads.
 * </p>
 *
 * @author Laurens Fridael
 */
@Configuration
@Import({DefaultFileUploadConfig.class, DefaultFileUploadMvcConfig.class})
public class FileUploadConfig {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Provides a {@code DataSource} for file upload storage. The {@code DataSource} must have the qualifier
   * 'spring-file-upload-storage'.
   * <p>This implementation creates an H2 database in the servlet container's temporary directory.</p>
   * <p>Using a temporary database works well enough for demonstrations, but for production-level applications you may
   * want to connect to your app's existing database.</p>
   *
   * @param servletContext
   * @return
   */
  @Bean
  @Qualifier("spring-file-upload-storage")
  DataSource fileUploadDataSource(ServletContext servletContext) {
    File tempDir = (File) servletContext.getAttribute(ServletContext.TEMPDIR);
    File databaseFile = new File(tempDir, "spring-file-upload-storage");
    String filename = databaseFile.getAbsolutePath();
    logger.info("File uploads are stored in '{}.h2.db'.", filename);
    return DataSourceBuilder.create()
        .driverClassName("org.h2.Driver")
        .url("jdbc:h2:file:" + filename)
        .username("sa")
        .build();
  }

}
