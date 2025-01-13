FROM openjdk:17-jdk

COPY target/UserService-0.0.1-SNAPSHOT.jar /app/UserService-0.0.1-SNAPSHOT.jar

WORKDIR /app
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "UserService-0.0.1-SNAPSHOT.jar"]
