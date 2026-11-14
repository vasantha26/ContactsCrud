# --- Build stage: compile the Spring Boot jar with Maven ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Cache dependencies first
COPY pom.xml .
RUN mvn -q dependency:go-offline
# Then build the app
COPY src ./src
RUN mvn -q clean package -DskipTests

# --- Run stage: small JRE image with just the jar ---
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/contacts-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
