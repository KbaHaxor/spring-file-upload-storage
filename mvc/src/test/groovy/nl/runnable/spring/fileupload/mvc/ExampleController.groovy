package nl.runnable.spring.fileupload.mvc

import nl.runnable.spring.fileupload.MultipartFileStorage
import nl.runnable.spring.fileupload.StoredMultipartFile
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*

/**
 * @author Laurens Fridael
 */
@Controller
@RequestMapping("/example")
class ExampleController {

  @Autowired
  MultipartFileStorage storage

  @RequestMapping(method = RequestMethod.POST)
  ResponseEntity<Void> post(@RequestParam MultipartFile file) {
    def id = storage.save(file, MultipartFileStorage.TTL_30_MINUTES, null)
    def headers = new HttpHeaders()
    String location = linkTo(getClass()).slash(id).toString()
    headers.add("Location", location)
    return new ResponseEntity<Void>(headers, HttpStatus.CREATED)
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{id}")
  StoredMultipartFile get(@PathVariable String id) {
    return storage.find(id);
  }
}
