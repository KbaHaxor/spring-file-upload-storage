package nl.runnable.spring.fileupload.impl;

import nl.runnable.spring.fileupload.IdGenerator;

import java.util.UUID;

/**
 * @author Laurens Fridael
 */
public class UuidGenerator implements IdGenerator {

  @Override
  public String generateId() {
    return UUID.randomUUID().toString();
  }

}
