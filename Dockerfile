# --- Build stage ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy pom first for better layer caching
COPY pom.xml /workspace/pom.xml
COPY src /workspace/src

# Build the application (skip tests inside container build)
RUN mvn -q -DskipTests package

# --- Runtime stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy jar produced by Spring Boot Maven plugin
COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]