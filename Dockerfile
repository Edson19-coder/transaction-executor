FROM gradle:8-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-jammy
EXPOSE 9001
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/transaction-executor.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=local", "-jar", "/app/transaction-executor.jar"]