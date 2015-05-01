package nl.runnable.spring.fileupload;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * Represents {@link MultipartFile}s stored in {@link nl.runnable.spring.fileupload.MultipartFileStorage}.
 *
 * @author Laurens Fridael
 */
public interface StoredMultipartFile extends MultipartFile {

  @NotNull
  String getId();

  @Nullable
  String getContext();

  @Nullable
  String getMetadata();

  @NotNull
  Date getCreatedAt();

  @NotNull
  Date getExpiresAt();

}
