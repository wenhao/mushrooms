FROM openjdk:8u171-jre-alpine3.7

VOLUME /tmp

ARG JAR_FILE

COPY ${JAR_FILE} /opt/app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/opt/app.jar"]