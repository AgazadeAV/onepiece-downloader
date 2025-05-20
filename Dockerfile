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

# Установка yt-dlp (самодостаточный бинарник, не требует Python)
ADD https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp /usr/local/bin/yt-dlp
RUN chmod +x /usr/local/bin/yt-dlp

# Копируем собранный JAR-файл
COPY --from=build /app/target/onepiece-downloader-0.0.1-SNAPSHOT.jar app.jar

# Создаём директорию для скачанных эпизодов
RUN mkdir -p downloads

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
