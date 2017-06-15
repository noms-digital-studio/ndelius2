# 'NDelius2' Web Application

A [Play Framework](https://www.playframework.com/) based website, developed in [Java 8](http://www.oracle.com/technetwork/java/javase/8-whats-new-2157071.html) with additional [Lombok](https://projectlombok.org/features/all) support.

Full asynchronous and non-blocking from the ground up, with the potential to server 10,000 concurrent users from a single server.

### Building and running

Build command (includes running unit and integration tests):

- `sbt assembly`

Run locally:

- `sbt run`

Running deployable fat jar (after building):

- `APPLICATION_SECRET=abcdefghijk java -jar ndelius2.jar` (in the `target/scala-2.11` directory)

Configuration parameters can be supplied via environment variables, e.g.:

- `STORE_ALFRESCO_URL=http://alfresco/ sbt run`
- `STORE_ALFRESCO_URL=http://alfresco/ APPLICATION_SECRET=abcdefghijk java -jar ndelius2.jar`

The website endpoint defaults to local port 9000.

### Development notes

The [Play Framework](https://www.playframework.com/) provides [Google Guice](https://github.com/google/guice/wiki/Motivation) the Dependency Injection framework as standard, and [MVC](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller) based webpages are generated using Play Framework [Twirl](https://www.playframework.com/documentation/2.5.x/ScalaTemplates) templates.

Spell Checking validation of appropriate user-entered form fields is achieved via the open-source [LanguageTool](https://www.languagetool.org/) library in British English.

### Building and running with Docker

- Build Docker Image `./buildDocker.sh`
- Run Docker Container `docker run -d -p 9000:9000 --name ndelius2 -e APPLICATION_SECRET=abcdef ndelius2`
