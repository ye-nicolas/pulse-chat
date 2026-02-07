FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/*.jar app.jar


EXPOSE 8080
EXPOSE 8810

ENTRYPOINT ["java", "-jar", "app.jar"]