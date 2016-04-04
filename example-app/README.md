# Spring File Upload Storage example app

This example shows how to use [Spring File Upload Storage](..) in a Spring Boot web app.

## Build and run

### Gradle

```
# Unix
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

### Maven

```
mvn spring-boot:run
```

### Open app

[http://127.0.0.1:8080](http://127.0.0.1:8080)

You can upload, download and delete files.

Notes

* Files are bound to the HTTP session.
* In this example, uploaded files are stored in a temporary database in the servlet container temp directory.

## Integrate in your own app

To adapt the example to your own app, you should read the source.

Project setup:

* [build.gradle](build.gradle) for Gradle
* [pom.xml](pom.xml) for Maven

Implementation:

* [FileUploadConfig.java](src/main/java/nl/runnable/spring/fileupload/example/config/FileUploadConfig.java) shows how to
configure your app and how to provide a `DataSource` for storing file uploads.
* [FileController.java](src/main/java/nl/runnable/spring/fileupload/example/web/FileController.java) shows how to
implement the actual file upload logic using the Spring File Upload API.

## License

[Public Domain](https://github.com/lfridael/spring-file-upload-storage/blob/master/LICENSE)
