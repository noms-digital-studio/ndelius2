FROM java

MAINTAINER Nick Talbot <nick.talbot@digital.justice.gov.uk>

COPY target/scala-2.11/ndelius2.jar /root/

EXPOSE 9000

ENTRYPOINT ["/usr/bin/java", "-jar", "/root/ndelius2.jar"]

