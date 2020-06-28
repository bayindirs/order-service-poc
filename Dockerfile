FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
ARG CONFIG_FOLDER=config
COPY ${JAR_FILE} app.jar
COPY ${CONFIG_FOLDER} config
ENTRYPOINT ["java","-jar","/app.jar"]