package nl.runnable.spring.fileupload.impl;

/**
 * @author Laurens Fridael
 */
abstract class SqlConstants {

  static final int COLUMN_COUNT = 10;

  static final String INSERT_INTO = "INSERT INTO sfus_file " +
      "(id, name, original_filename, content_type, size, data, context, metadata, created_at, expires_at) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

  static final String SELECT_BY_ID =
      "SELECT id, name, original_filename, content_type, size, context, metadata, created_at, expires_at " +
          "FROM sfus_file WHERE id = ?";

  static final String SELECT_BY_CONTEXT =
      "SELECT id, name, original_filename, content_type, size, context, metadata, created_at, expires_at " +
          "FROM sfus_file WHERE context = ?";


  static final String SELECT_DATA_BY_ID = "SELECT data FROM sfus_file WHERE id = ?";

  static final String SELECT_COUNT = "SELECT COUNT(*) FROM sfus_file";

  static final String UPDATE_EXPIRES_AT = "UPDATE sfus_file SET expires_at = ? WHERE id = ?";

  static final String UPDATE_METADATA = "UPDATE sfus_file SET metadata = ? WHERE id = ?";

  static final String DELETE_BY_ID = "DELETE FROM sfus_file WHERE id = ?";

  static final String DELETE_BY_CONTEXT = "DELETE FROM sfus_file WHERE context = ?";

  static final String DELETE_EXPIRED = "DELETE FROM sfus_file WHERE expires_at <= ?";

  static final String DELETE_ALL = "DELETE FROM sfus_file";

  static final String COUNT = "SELECT COUNT(*) FROM sfus_file";

}
