package nl.runnable.spring.fileupload.example.web;

import nl.runnable.spring.fileupload.SessionMultipartFileStorage;
import nl.runnable.spring.fileupload.StoredMultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Controller for managing files.
 * <p>
 * This implementation shows how to use {@link SessionMultipartFileStorage}.
 * </p>
 *
 * @author Laurens Fridael
 */
@Controller
@RequestMapping("/files")
public class FileController {

  /**
   * An easy way of making the time-to-live setting configurable, is to use {@code @Value}.
   */
  @Value("${app.fileupload.ttl:3600}")
  private int timeToLiveInSeconds;

  /**
   * Handles the index request.
   * <p>
   * This implementation populates the model with a "files" variable containing a {@code List} of
   * {@link StoredMultipartFile}s sorted by creation date.
   * </p>
   *
   * @param model
   * @param storage
   * @return
   */
  @RequestMapping(method = RequestMethod.GET)
  String index(Map<String, Object> model, SessionMultipartFileStorage storage) {
    List<StoredMultipartFile> files = new ArrayList<StoredMultipartFile>(storage.findAll());
    Collections.sort(files, new Comparator<StoredMultipartFile>() {
      @Override
      public int compare(StoredMultipartFile o1, StoredMultipartFile o2) {
        return o1.getCreatedAt().compareTo(o2.getCreatedAt());
      }
    });
    model.put("files", files);
    return "files/index";
  }

  /**
   * Handles file uploads.
   * <p>
   * This implementation stores the {@link MultipartFile} in the {@link SessionMultipartFileStorage}. If the file is
   * empty it does nothing.
   * </p>
   *
   * @param file
   * @param storage
   * @param attributes
   * @return
   */
  @RequestMapping(method = RequestMethod.POST)
  String upload(@RequestParam("file") MultipartFile file, SessionMultipartFileStorage storage,
                RedirectAttributes attributes) {
    if (!file.isEmpty()) {
      storage.save(file, timeToLiveInSeconds);
    }
    return "redirect:/files";
  }

  /**
   * Obtains the file with the given ID.
   * <p>
   * This implementation returns a {@link StoredMultipartFile}, which will, in turn, be handled by
   * {@link nl.runnable.spring.fileupload.mvc.StoredMultipartFileReturnValueHandler}.
   * </p>
   *
   * @param id
   * @param storage
   * @return The matching file.
   * @throws FileNotFoundException If the file cannot be found.
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{id}")
  StoredMultipartFile download(@PathVariable("id") String id, SessionMultipartFileStorage storage) throws
      FileNotFoundException {
    StoredMultipartFile file = storage.find(id);
    if (file == null) {
      throw new FileNotFoundException(String.format("Cannot find file '%s'.", id));
    }
    return file;
  }

  /**
   * Deletes the file with the given id.
   *
   * @param id
   * @param storage
   * @return
   * @throws FileNotFoundException If the file cannot be found.
   */
  @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
  String delete(@PathVariable("id") String id, SessionMultipartFileStorage storage) throws FileNotFoundException {
    int count = storage.delete(id);
    if (count == 0) {
      throw new FileNotFoundException(String.format("Cannot find file '%s'.", id));
    }
    return "redirect:/files";
  }

  /**
   * Generates a {@code 404 Not Found} response for {@link FileNotFoundException}s.
   */
  @ExceptionHandler(FileNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  void handleFileNotFound() {
  }

}
