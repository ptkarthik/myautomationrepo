FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean test

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target /app/target
COPY src/test/resources /app/resources
COPY testNG.xml /app/testNG.xml
