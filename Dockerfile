
# Stage 1: Build - Using 8.14 to satisfy the Spring Boot 4.x requirement
FROM gradle:8.14-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

# We add -Dspring.autoconfigure.exclude to prevent it from looking for a DB during build
RUN gradle bootJar --no-daemon -x test \
    -Dorg.gradle.jvmargs="-Xmx384m -XX:MaxMetaspaceSize=128m" \
    -Dspring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration

# Stage 2: Run
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

EXPOSE 8080

# Final run command
ENTRYPOINT ["java", "-Xmx384m", "-jar", "app.jar"]
