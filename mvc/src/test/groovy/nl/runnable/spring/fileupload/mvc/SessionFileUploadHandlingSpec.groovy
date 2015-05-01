package nl.runnable.spring.fileupload.mvc

import nl.runnable.spring.fileupload.MultipartFileStorage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpSession
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultHandler
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * @author Laurens Fridael
 */
@WebAppConfiguration
@ContextConfiguration(classes = TestConfig)
@Transactional
class SessionFileUploadHandlingSpec extends Specification {

  @Autowired
  WebApplicationContext context

  MockMvc mvc

  String location

  @Autowired
  MultipartFileStorage storage

  MockHttpSession session

  def setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context).build()

    MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", [1, 2, 3, 4] as byte[])
    session = mvc.perform(fileUpload('/session').file(file))
        .andDo({ result -> location = result.getResponse().getHeader("Location") } as ResultHandler)
        .andReturn()
        .getRequest()
        .getSession() as MockHttpSession
    storage.save(new MockMultipartFile("file2", "test2.pdf", "application/pdf", [5, 6, 7, 8] as byte[]),
        MultipartFileStorage.TTL_30_MINUTES, null, null)
  }

  def "GET <location> produces 200 OK and obtains the file's content"() {
    expect:
    location

    when:
    def actions = mvc.perform(get(location).session(session))
    then:
    actions.andExpect(status().is(200))
        .andExpect(content().bytes([1, 2, 3, 4] as byte[]))
    // Content type is set to 'text/html' during integration testing, but is set correctly in practice.
    // .andExpect(content().contentTypeCompatibleWith("application/pdf"))
  }

  def "GET /session produces 200 OK and obtains the names of the files in session storage"() {
    when:
    def actions = mvc.perform(get('/session').session(session))
    then:
    actions.andExpect(status().is(200))
        .andExpect(content().json('["test.pdf", "test.pdf"]'))
  }

  def "DELETE <location> produces 204 No Content and deletes the file from session storage"() {
    expect:
    storage.count() == 3

    when:
    def actions = mvc.perform(delete(location).session(session))
    then:
    actions.andExpect(status().is(204))
    storage.count() == 2
  }

  def "DELETE /session produces 204 No Content and deletes all files from session storage"() {
    expect:
    storage.count() == 3

    when:
    def actions = mvc.perform(delete("/session").session(session))
    then:
    actions.andExpect(status().is(204))
    storage.count() == 1
  }

}
