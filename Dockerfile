# Stage 1: Build the application
FROM gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

# We use the memory limits HERE where the heavy lifting happens
RUN gradle bootJar --no-daemon -x test -Dorg.gradle.jvmargs="-Xmx384m -XX:MaxMetaspaceSize=128m"

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copy the generated jar from the build stage
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

EXPOSE 8080

# Run the app with memory limits too, to keep it stable on the free tier
ENTRYPOINT ["java", "-Xmx384m", "-jar", "app.jar"]
