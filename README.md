# Spring File Upload Storage

This is a small library for managing the storage of multipart file uploads in Spring MVC applications. 

* Stores uploaded files in a database.
* Works with any database that supports JDBC and SQL.
* Automatic cleanup of stale files.
* Suitable for stateless HTTP applications. (E.g. JavaScript SPA frontends with a REST backend)
* Enforces access control to file uploads using Spring Security.
* Autoconfiguration for Spring Boot.

Storing file uploads is a simple problem, but ad-hoc solutions become cumbersome when you have multiple applications to maintain. This library addresses the common boilerplate.

## Dependencies

* Required:  
  * Java 1.6
  * Spring 4.1.x (might work with earlier versions, but these are not tested)
* Optional:
  * Spring Security 3.2.x
  * Spring Boot 1.2.x

