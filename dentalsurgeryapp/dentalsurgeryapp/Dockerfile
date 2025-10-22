# Multi-stage Dockerfile for building and running the Spring Boot application
# Stage 1: build with Maven and JDK
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# Copy maven wrapper and pom first for better caching
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

# Copy source
COPY src ./src

# Make mvnw executable (Linux container)
RUN chmod +x mvnw

# Build the jar (skip tests to speed up builds in container; you can remove -DskipTests to run tests)
RUN ./mvnw -B clean package -DskipTests

# Stage 2: runtime image
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

# Copy the jar built in the previous stage. The Spring Boot plugin produces a jar in target/.
# Use a wildcard to allow for -SNAPSHOT versions.
COPY --from=build /workspace/target/*-SNAPSHOT.jar app.jar

# Expose port used by the Spring Boot app (default 8080)
EXPOSE 8080

# Run as non-root for better security (optional)
# Create an unprivileged user and switch to it
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser || true
USER appuser

ENTRYPOINT ["java","-jar","/app/app.jar"]
