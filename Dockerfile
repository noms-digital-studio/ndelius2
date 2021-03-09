FROM openjdk:8-jre-slim

COPY target/scala-2.12/ndelius2-*.jar /root/app.jar

RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

HEALTHCHECK CMD curl --fail http://localhost:9000/newTech/healthcheck || exit 1

EXPOSE 9000

CMD ["java", "-jar", "/root/app.jar"]
