# Optimized Single-stage Dockerfile using locally compiled jar (Java 21)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Add non-root system user and prepare upload folder with proper permissions
RUN addgroup -S spring && adduser -S spring -G spring && \
    mkdir -p /app/uploads/room-images && \
    chown -R spring:spring /app

USER spring:spring

COPY --chown=spring:spring target/hotel-booking-service-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8090

# JVM options optimized for containers & high throughput
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
