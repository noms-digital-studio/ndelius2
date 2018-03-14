# 'NDelius New Technology' Web Application

[![CircleCI](https://circleci.com/gh/noms-digital-studio/ndelius-new-tech.svg?style=svg)](https://circleci.com/gh/noms-digital-studio/ndelius-new-tech)

A [Play Framework](https://www.playframework.com/) based website, developed in [Java 8](http://www.oracle.com/technetwork/java/javase/8-whats-new-2157071.html) with additional [Lombok](https://projectlombok.org/features/all) support.

Fully asynchronous and non-blocking from the ground up, with the potential to serve 10,000 concurrent users from a single server.

### Building and running

Prerequisites:
- sbt (Scala Build Tool) http://www.scala-sbt.org/release/docs

Build command (includes running unit and integration tests):
- `sbt assembly`

Running locally:
`
PARAMS_USER_TOKEN_VALID_DURATION=2000d \
STORE_PROVIDER=mongo \
OFFENDER_API_PROVIDER=stub \
APPLICATION_SECRET=mySuperSecretKeyThing \
ELASTIC_SEARCH_HOST=<the hostname of your ES cluster> \
ELASTIC_SEARCH_PORT=443 \
NOMIS_API_BASE_URL=<the URL of the NOMIS system> \
NOMIS_PAYLOAD_TOKEN=<the NOMIS API payload token> \
NOMIS_PRIVATE_KEY=<the NOMIS API private key> \
ANALYTICS_MONGO_CONNECTION=<the URL of you MongoDb instance>/analytics \
sbt -Dlogback.application.level=DEBUG run`

Running deployable fat jar (after building):
- `APPLICATION_SECRET=abcdefghijk java -jar ndelius2.jar` (in the `target/scala-2.11` directory)

Configuration parameters can be supplied via environment variables. See `application.conf` for full list of variables. 

e.g.:
- `STORE_ALFRESCO_URL=http://alfresco/ sbt run`
- `STORE_ALFRESCO_URL=http://alfresco/ APPLICATION_SECRET=abcdefghijk java -jar ndelius2.jar`

The website endpoint defaults to local port 9000.

Run all tests:
- sbt clean test

Run frontend tests:
- sbt mocha

### Development notes

The [Play Framework](https://www.playframework.com/) provides the [Google Guice](https://github.com/google/guice/wiki/Motivation) Dependency 
Injection framework as standard, and [MVC](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller) based 
Webpages are generated via Play Framework [Twirl](https://www.playframework.com/documentation/2.5.x/ScalaTemplates) templates.

### Building and running with Docker

- Build Docker Image `./buildDocker.sh`
- Run Docker Container `docker run -d -p 9000:9000 --name ndelius2 -e APPLICATION_SECRET=abcdef ndelius2`

### Dependencies
 - Alfresco
 - Delius Offender API 
 - Elastic Search
 - PDF Generator
 - MongoDb (for Analytics)
 - NOMIS API
