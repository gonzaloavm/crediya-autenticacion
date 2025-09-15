# Build Stage
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Gradle wrapper and configuration files
COPY gradlew .
COPY gradle/wrapper/gradle-wrapper.jar gradle/wrapper/
COPY gradle/wrapper/gradle-wrapper.properties gradle/wrapper/

# Copy build scripts
COPY build.gradle .
COPY settings.gradle .
COPY main.gradle .
COPY lombok.config .

# Copy source code for all modules
COPY applications applications/
COPY domain domain/
COPY infrastructure infrastructure/

# Make gradlew executable
RUN chmod +x gradlew

# Build the application
# Use --no-daemon to avoid issues in Docker builds
# Use -x test to skip tests during build, can be removed if tests are fast and reliable
RUN ./gradlew bootJar --no-daemon -x test

# Run Stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the JAR from the build stage
# The JAR is typically found in applications/app-service/build/libs/
COPY --from=builder /app/applications/app-service/build/libs/crediya-autenticacion.jar crediya-autenticacion.jar

VOLUME /tmp

ENV JAVA_OPTS=" -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

# Use a non-root user for security
USER nobody

EXPOSE 8080

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar crediya-autenticacion.jar" ]