package nl.runnable.spring.fileupload

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * @author Laurens Fridael
 */
@ContextConfiguration(classes = [TestConfig])
class MultipartFileStorageSpec extends Specification {

  @Autowired
  MultipartFileStorage storage

  String fileId

  def setup() {
    def file = new MockMultipartFile(
        'test.pdf',
        'test.pdf.original',
        'application/pdf',
        [1, 2, 3, 4] as byte[]
    )
    fileId = storage.save(file, MultipartFileStorage.TTL_30_MINUTES, 'john')
  }

  def 'Retrieving a file yields data that is equivalent to that of the input file'() {
    when:
    def file = storage.find(fileId)
    def tempFile = File.createTempFile('test', 'file');
    file.transferTo(tempFile)
    then:
    file.name == 'test.pdf'
    file.originalFilename == 'test.pdf.original'
    file.contentType == 'application/pdf'
    file.size == 4
    !file.empty
    file.bytes == [1, 2, 3, 4] as byte[]
    file.id == fileId
    file.username == 'john'
    file.createdAt
    file.expiresAt.time == file.createdAt.time + (MultipartFileStorage.TTL_30_MINUTES * 1000)
  }

  def 'Transferring a multipart file\'s contents to a system file yields identical content'() {
    setup:
    def file = storage.find(fileId)
    def tempFile = File.createTempFile(file.originalFilename, '.tmp');
    tempFile.deleteOnExit()

    when:
    file.transferTo(tempFile)
    then:
    tempFile.bytes == [1, 2, 3, 4] as byte[]

    cleanup:
    tempFile.delete()
  }

  def 'Setting a file\'s time-to-live changes its expiration'() {
    when:
    def expiresAt = storage.setTimeToLive(fileId, 1000);
    def file = storage.find(fileId)
    then:
    expiresAt.time == file.expiresAt.time
  }

  def 'Deleting a file removes it from storage'() {
    expect:
    storage.delete(fileId) == 1
    !storage.find(fileId)
    storage.delete(fileId) == 0
  }

  def 'Deleting expired files removes them from storage'() {
    expect:
    storage.deleteExpired() == 0

    when:
    storage.setTimeToLive(fileId, 0)
    then:
    storage.deleteExpired() == 1
    !storage.find(fileId)
  }

}
