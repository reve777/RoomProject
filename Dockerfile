# Multi-stage Dockerfile for Java 25 Spring Boot Application
# Stage 1: Build
FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /workspace

# Copy Maven wrapper / configuration
COPY pom.xml .
COPY src ./src

# In environments with internet, we could run mvn package
# For container builds, ensure maven is present or jar is pre-built
# Install maven in builder
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Add a non-root system user for security best practices
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=builder /workspace/target/*.jar app.jar

EXPOSE 8080 8443

# JVM options optimized for containers & Spring Boot on Java 25
ENTRYPOINT ["java", "-XX:+UseZGC", "-XX:+ZGenerational", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
