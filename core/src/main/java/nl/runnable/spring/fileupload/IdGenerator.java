package nl.runnable.spring.fileupload;

/**
 * Strategy for generating file IDs. The default implementation generates UUIDs.
 *
 * @author Laurens Fridael
 */
public interface IdGenerator {

  String generateId();
}
