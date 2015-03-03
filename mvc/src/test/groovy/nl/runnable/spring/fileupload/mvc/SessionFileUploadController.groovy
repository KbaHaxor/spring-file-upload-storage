package nl.runnable.spring.fileupload.mvc

import nl.runnable.spring.fileupload.MultipartFileStorage
import nl.runnable.spring.fileupload.SessionMultipartFileStorage
import nl.runnable.spring.fileupload.StoredMultipartFile
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*

/**
 * Example controller for testing Spring MVC integration.
 *
 * @author Laurens Fridael
 */
@Controller
@RequestMapping("/session")
class SessionFileUploadController {

  /**
   * Tests {@link HttpSessionMultipartFileStorageArgumentResolver}
   */
  @RequestMapping(method = RequestMethod.POST)
  ResponseEntity<Void> post(SessionMultipartFileStorage storage, @RequestParam MultipartFile file) {
    def id = storage.save(file, MultipartFileStorage.TTL_30_MINUTES)
    def headers = new HttpHeaders()
    String location = linkTo(getClass()).slash(id).toString()
    headers.add("Location", location)
    return new ResponseEntity<Void>(headers, HttpStatus.CREATED)
  }

  /**
   * Tests {@link StoredMultipartFileReturnValueHandler}.
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{id}")
  StoredMultipartFile get(SessionMultipartFileStorage storage, @PathVariable String id) {
    return storage.find(id);
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void delete(SessionMultipartFileStorage storage, @PathVariable String id) {
    storage.delete(id);
  }


}
