# ===== STAGE 1: build =====
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Копіюємо pom.xml і завантажуємо залежності
COPY pom.xml .
RUN mvn dependency:go-offline

# Копіюємо весь проєкт і збираємо jar
COPY src ./src
RUN mvn clean package -DskipTests

# ===== STAGE 2: run =====
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app

# Копіюємо зібраний jar з першого етапу
COPY --from=builder /app/target/*.jar app.jar

# Порт, який надає Render
ENV PORT=8080
EXPOSE 8080

# Змінні для БД — Render підставить їх автоматично з Environment Variables
ENV DATABASE_URL=""
ENV DB_USERNAME=""
ENV DB_PASSWORD=""

# Запускаємо Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
