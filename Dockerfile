FROM openjdk:8

MAINTAINER Nick Talbot <nick.talbot@digital.justice.gov.uk>

COPY target/scala-2.12/ndelius2-*.jar /root/ndelius2.jar

EXPOSE 9000

ENTRYPOINT ["/usr/bin/java", "-jar", "/root/ndelius2.jar"]

