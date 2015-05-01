package nl.runnable.spring.fileupload

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * @author Laurens Fridael
 */
@ContextConfiguration(classes = [TestConfig, CleanupExpiredFilesJobConfig])
class CleanupExpiredFilesJobSpec extends Specification {

  @Autowired
  MultipartFileStorage storage

  @Autowired
  CleanupExpiredFilesJob cleanupExpiredFilesJob;

  String fileId

  def setup() {
    def file = new MockMultipartFile(
        'test.pdf',
        'test.pdf.original',
        'application/pdf',
        [1, 2, 3, 4] as byte[]
    )
    fileId = storage.save(file, MultipartFileStorage.TTL_30_MINUTES, 'my-context', null)
  }

  def 'Cleans up expired files automatically'() {
    expect:
    storage.find(fileId)

    when:
    storage.setTimeToLive(fileId, 0)
    sleep(2000) // The clean-up interval is set to 1 second.
    then:
    !storage.find(fileId)
  }

}
