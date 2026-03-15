# Stage 1: Build
FROM gradle:8.12-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

# Lowering -Xmx to 256m to stay safely under the 512m limit
RUN gradle bootJar --no-daemon -x test \
    -Dorg.gradle.jvmargs="-Xmx256m -XX:MaxMetaspaceSize=128m" \
    -Dspring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration

# Stage 2: Run
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx300m", "-jar", "app.jar"]
