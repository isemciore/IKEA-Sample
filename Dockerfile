FROM openjdk:8-jdk-alpine

LABEL maintainer="Me me and me"

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=target/demo-0.0.1-SNAPSHOT.jar

ADD ${JAR_FILE} ikea-demo.jar

ENTRYPOINT ["java", "-jar", "/ikea-demo.jar"]