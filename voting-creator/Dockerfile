FROM maven:3.9.9-eclipse-temurin-21-alpine AS maven
WORKDIR /app
COPY src /app/src
COPY pom.xml /app
RUN mvn clean package -DskipTests

FROM amazoncorretto:17.0.18-al2023
WORKDIR /app
COPY --from=maven /app/target/voting-creator-0.0.1-SNAPSHOT.jar /app/voting-creator-0.0.1-SNAPSHOT.jar
CMD ["java","-jar","/app/voting-creator-0.0.1-SNAPSHOT.jar"]