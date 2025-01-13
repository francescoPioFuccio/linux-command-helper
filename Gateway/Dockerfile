FROM openjdk:17-jdk

COPY target/Gateway-0.0.1-SNAPSHOT.jar /app/Gateway-0.0.1-SNAPSHOT.jar

WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "Gateway-0.0.1-SNAPSHOT.jar"]