package nl.runnable.spring.fileupload.example.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Generates a redirect to the {@link FileController}.
 *
 * @author Laurens Fridael
 */
@Controller
@RequestMapping("/")
public class IndexController {

  @RequestMapping(method = RequestMethod.GET)
  String redirectToFilesIndex() {
    return "redirect:/files";
  }

}
