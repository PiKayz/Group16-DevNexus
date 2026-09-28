FROM eclipse-temurin:25
WORKDIR /app
COPY target/semApp.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]