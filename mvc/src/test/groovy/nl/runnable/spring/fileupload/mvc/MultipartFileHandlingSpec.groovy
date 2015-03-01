package nl.runnable.spring.fileupload.mvc

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultHandler
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * @author Laurens Fridael
 */
@WebAppConfiguration
@ContextConfiguration(classes = TestConfig)
class MultipartFileHandlingSpec extends Specification {

  @Autowired
  WebApplicationContext context

  MockMvc mvc

  def setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context).build()
  }

  def 'Posting and obtaining file uploads'() {
    when:
    MockMultipartFile file = new MockMultipartFile("file", "test.bin", "application/pdf", [1, 2, 3, 4] as byte[])
    def postResult = mvc.perform(fileUpload('/example').file(file))
    then:
    String location = null
    postResult.andExpect(status().is(201))
        .andDo({ res -> location = res.getResponse().getHeader("Location") } as ResultHandler)

    expect:
    location
    mvc.perform(get(location)).andExpect(status().is(200))

  }

}
