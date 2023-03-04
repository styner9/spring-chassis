ARG BASE_IMAGE=amazoncorretto:19-alpine
FROM ${BASE_IMAGE:-amazoncorretto:19-alpine}


ENV APP_NAME=spring-chassis-webmvc-demo

ARG APP_VERSION=0.0.1-SNAPSHOT
ENV APP_VERSION=${APP_VERSION}

ENV APP_JAR_FILE=spring-chassis-webmvc-demo-${APP_VERSION}.jar

WORKDIR /app/spring-chassis-webmvc-demo
COPY build/libs/spring-chassis-webmvc-demo-${APP_VERSION}.jar .
COPY src/main/resources/bootstrap.yml ./env_file.yml
COPY entrypoint.sh .
RUN chmod +x entrypoint.sh



CMD ["./entrypoint.sh"]