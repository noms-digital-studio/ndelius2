# 'NDelius New Technology' Web Application

[![CircleCI](https://circleci.com/gh/noms-digital-studio/ndelius-new-tech.svg?style=svg)](https://circleci.com/gh/noms-digital-studio/ndelius-new-tech)

A [Play Framework](https://www.playframework.com/) based website, developed in [Java 8](http://www.oracle.com/technetwork/java/javase/8-whats-new-2157071.html) with additional [Lombok](https://projectlombok.org/features/all) support.

Fully asynchronous and non-blocking from the ground up, with the potential to serve 10,000 concurrent users from a single server.

### Building and running

Prerequisites:
- sbt (Scala Build Tool) http://www.scala-sbt.org/release/docs

Build command (includes running unit and integration tests):
- `sbt assembly`

Run locally:
- `sbt run`

Standalone:

Add the following environment vairable to run in standalone mode

- `STANDALONE_OPERATION=true`

Running deployable fat jar (after building):
- `APPLICATION_SECRET=abcdefghijk java -jar ndelius2.jar` (in the `target/scala-2.11` directory)

Configuration parameters can be supplied via environment variables, e.g.:

- `STORE_ALFRESCO_URL=http://alfresco/ sbt run`
- `STORE_ALFRESCO_URL=http://alfresco/ APPLICATION_SECRET=abcdefghijk java -jar ndelius2.jar`
The website endpoint defaults to local port 9000.

### Development notes

The [Play Framework](https://www.playframework.com/) provides the [Google Guice](https://github.com/google/guice/wiki/Motivation) Dependency Injection framework as standard, and [MVC](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller) based webpages are generated via Play Framework [Twirl](https://www.playframework.com/documentation/2.5.x/ScalaTemplates) templates.

Spell Checking validation of appropriate user-entered form fields is achieved via the open-source [LanguageTool](https://www.languagetool.org/) library in British English.

### Building and running with Docker

- Build Docker Image `./buildDocker.sh`
- Run Docker Container `docker run -d -p 9000:9000 --name ndelius2 -e APPLICATION_SECRET=abcdef ndelius2`

### Elastic Search
To configure an 'offenders' index.
```
curl -XPUT 'http://localhost:9200/offender?pretty' -H 'Content-Type: application/json' -d'
{
    "settings" : {
        "index" : {
            "number_of_shards" : 6, 
            "number_of_replicas" : 1 
        }
    },
    "mappings": {
      "document": {
        "properties": {
          "dateOfBirth": {
            "type":   "date",
            "format": "yyyy-MM-dd||yyyy/MM/dd||dd-MM-yy||dd/MM/yy||dd-MM-yyyy||dd/MM/yyyy"
          }
        }
      }
    }
}
'
```

To insert a document.
```
curl -XPUT 'http://localhost:9200/offender/document/4500020000?pretty' -H 'Content-Type: application/json' -d'
    {
      "offenderId": 4500020000,
      ...
    }
'
```
See `./test/resources/offender-search-result.json` for example data structure.

Bulk insert
```
curl -H "Content-Type: application/json" -XPOST 'localhost:9200/offender/_bulk?pretty&refresh' --data-binary "@es-test-data.txt"

```

