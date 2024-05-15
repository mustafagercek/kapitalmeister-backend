FROM arm64v8/openjdk:18-jdk

ARG JAR_FILE=build/libs/kapitalmeister-backend-1.0.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
