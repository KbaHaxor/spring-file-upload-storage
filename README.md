# Spring File Upload Storage [![Build Status](https://travis-ci.org/lfridael/spring-file-upload-storage.svg?branch=master)](https://travis-ci.org/lfridael/spring-file-upload-storage)

Utility library for managing the temporary storage of multipart file uploads in Spring MVC applications.

## Problem

Spring MVC has a nice abstraction for handling file uploads through multipart requests. But where do you store uploaded files when user interactions in your application span multiple HTTP requests?

* Storing uploads as temporary files on the filesystem is not the most secure approach when dealing with privacy-sensitive information.
* Storing uploads as database BLOBs is the better approach with regards to security, but is not a trivial solution to implement.
* You also need a way to manage uploads in a timely manner, not deleting them too soon and not keeping them around longer than necessary.

Spring File Upload Storage aims to address the issues.

## Approach

* Stores uploaded files in a database.
* Works with any database that supports JDBC and SQL.
* Uses a time-to-live mechanism to keep files 'alive'.
* Cleans up expired files automatically. (The API also offers lets you delete files manually.)
* Autoconfiguration for Spring Boot.

## Dependencies

* Required:  
  * Java 1.6
  * Spring 4.1.x (might work with Spring 4.0.x and Spring 3.2.x, but these version are not tested)
* Optional:
  * Spring Security 3.2.x
  * Spring Boot 1.2.x

## Build

```
# *NIX
./gradlew

# Windows
./gradlew.bat
```
