# Etapa 1: build
FROM gradle:8.4.0-jdk17 AS builder

WORKDIR /app
COPY ktor-flashcard-backend/ .

RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# Etapa 2: imagem final
FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]