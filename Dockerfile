FROM gradle:6.1.1-jdk15 AS build
WORKDIR /src
COPY build.gradle .
COPY src ./src/
USER root
RUN gradle shadowJar --no-daemon

FROM openjdk:15-jre

EXPOSE 8080

ENV APPLICATION_USER advbot_admin
RUN useradd $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

COPY --from=build /src/build/libs/AdvBot.jar /app/AdvBot.jar
WORKDIR /app

CMD ["java", "-server", "-XX:+UseContainerSupport", "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "AdvBot.jar"]