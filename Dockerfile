# Stage 1: Build
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# Копируем только файлы для загрузки зависимостей (кеширование)
COPY gradlew .
COPY gradle/wrapper/gradle-wrapper.properties gradle/wrapper/
COPY gradle/wrapper/gradle-wrapper.jar gradle/wrapper/
COPY build.gradle settings.gradle ./

RUN ./gradlew --no-daemon dependencies

# Копируем исходники
COPY src ./src

# Собираем JAR (без распаковки)
RUN ./gradlew clean build --no-daemon -x test

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Копируем собранный JAR целиком
COPY --from=builder /app/build/libs/*.jar app.jar

# Запускаем через java -jar
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-XX:+HeapDumpOnOutOfMemoryError", \
            "-XX:HeapDumpPath=/tmp/heapdump.hprof", \
            "-jar", "app.jar"]
