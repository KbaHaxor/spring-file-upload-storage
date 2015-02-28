package nl.runnable.spring.fileupload

import nl.runnable.spring.fileupload.config.JdbcConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification


/**
 * @author Laurens Fridael
 */
@ContextConfiguration(classes = JdbcConfig)
class JdbcMultipartFileRepositorySpec extends Specification {

  @Autowired
  MultipartFileRepository repository

  def 'Is initialized'() {
    expect:
    repository
  }

}
