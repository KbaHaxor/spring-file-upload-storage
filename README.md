# Spring File Upload Storage [![Build Status](https://travis-ci.org/lfridael/spring-file-upload-storage.svg?branch=master)](https://travis-ci.org/lfridael/spring-file-upload-storage)

Utility library for managing the temporary storage of multipart file uploads in Spring MVC applications.

## Problem

Spring MVC has a convenient abstraction for handling file uploads through multipart requests. But where do you store uploaded files during user interactions that span multiple HTTP requests? For example, in a multi-page form.

* Storing uploads as temporary files on the filesystem is easily implemented, but is not the most secure approach when dealing with privacy-sensitive information.
* Storing uploads as database BLOBs is more secure, but is not trivial to implement.
* You also need a way to manage uploads in a timely manner, not keeping files around longer than necessary.

Spring File Upload Storage aims to address these issues.

## Approach

* Stores uploaded files in a database.
* Works with any database that supports JDBC and SQL.
* Maintains files using a time-to-live mechanism.
* Cleans up expired files automatically.

## Core API

* [MultipartFileStorage](https://github.com/lfridael/spring-file-upload-storage/blob/master/core/src/main/java/nl/runnable/spring/fileupload/MultipartFileStorage.java) for accessing the global file storage
* [SessionMultipartFileStorage](https://github.com/lfridael/spring-file-upload-storage/blob/master/core/src/main/java/nl/runnable/spring/fileupload/SessionMultipartFileStorage.java) for accessing storage that is bound to a user session

## Example app

The [Example app](https://github.com/lfridael/spring-file-upload-storage/tree/master/example-app) shows to use the library in a Spring Boot web app.

## Dependencies

* Java 1.6+
* Spring 4.0+, specifically these libraries
    * spring-web
    * spring-jdbc
* slf4j-api

## Build

```
# *NIX
./gradlew

# Windows
./gradlew.bat
```

## License

[Public Domain](https://github.com/lfridael/spring-file-upload-storage/blob/master/LICENSE)
