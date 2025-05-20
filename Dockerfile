# Этап сборки приложения
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Финальный контейнер
FROM openjdk:17-slim
WORKDIR /app

# Установка yt-dlp
RUN apt-get update && \
    apt-get install -y curl && \
    curl -L https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp -o /usr/local/bin/yt-dlp && \
    chmod +x /usr/local/bin/yt-dlp && \
    apt-get purge -y curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Копируем собранный JAR-файл
COPY --from=build /app/target/onepiece-downloader-0.0.1-SNAPSHOT.jar app.jar

# Создаём директорию для скачанных эпизодов
RUN mkdir -p downloads

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
