FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /orders-processor

COPY pom.xml .
COPY src ./src

RUN mvn clean package

FROM eclipse-temurin:21-jdk

COPY --from=build /orders-processor/target/*.jar orders-processor.jar

ENTRYPOINT ["java","-jar","/orders-processor.jar"]