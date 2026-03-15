# Use a lightweight Java runtime
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the JAR you built locally into the Docker image
COPY deploy/app.jar app.jar

# Expose the port Spring Boot uses
EXPOSE 8080

# Run the app with a safe memory limit for Render
ENTRYPOINT ["java", "-Xmx384m", "-jar", "app.jar"]