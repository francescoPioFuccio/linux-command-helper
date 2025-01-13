FROM openjdk:17-jdk

COPY target/HistoryService-0.0.1-SNAPSHOT.jar /app/HistoryService-0.0.1-SNAPSHOT.jar

WORKDIR /app
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "HistoryService-0.0.1-SNAPSHOT.jar"]