)
FROM gradle:8.12-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src


RUN gradle bootJar --no-daemon -x test -Dorg.gradle.jvmargs="-Xmx384m -XX:MaxMetaspaceSize=128m"


FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx384m", "-jar", "app.jar"]
