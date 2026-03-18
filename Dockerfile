# Multi-stage build for curriculum-service
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies (this layer will be cached)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the built artifact from build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership to spring user
RUN chown spring:spring app.jar

USER spring

# Expose application port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8082/actuator/health || exit 1

# Set Spring profile and other runtime args
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-prod}
ENV APP_VERSION=${APP_VERSION:-latest}

# Run the application
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", \
  "app.jar"]
