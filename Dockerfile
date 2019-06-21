FROM openjdk:8

MAINTAINER Nick Talbot <nick.talbot@digital.justice.gov.uk>

RUN addgroup --gid 2000 --system appgroup && \
    adduser --uid 2000 --system appuser --gid 2000

RUN mkdir -p /app
WORKDIR /app


COPY ndelius2.jar /app/ndelius2.jar

RUN chown -R appuser:appgroup /app

USER 2000

ENTRYPOINT ["java", "-jar", "/app/ndelius2.jar"]

