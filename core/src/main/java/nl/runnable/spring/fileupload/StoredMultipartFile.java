package nl.runnable.spring.fileupload;

import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * Represents {@link MultipartFile}s stored in {@link nl.runnable.spring.fileupload.MultipartFileStorage}.
 * This interface provides extra information.
 *
 * @author Laurens Fridael
 */
public interface StoredMultipartFile extends MultipartFile {

  String getId();

  String getContext();

  Date getCreatedAt();

  Date getExpiresAt();

}
