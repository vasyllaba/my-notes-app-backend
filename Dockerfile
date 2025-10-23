# ===== STAGE 1: Build =====
FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder
WORKDIR /app

# Копіюємо тільки pom.xml спочатку (для кешування залежностей)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копіюємо код і білдимо
COPY src ./src
RUN mvn clean package -DskipTests -B

# ===== STAGE 2: Run =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Копіюємо jar з builder stage
COPY --from=builder /app/target/*.jar app.jar

# Render автоматично встановить PORT
EXPOSE 8080

# JVM налаштування для production
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS $JAVA_TOOL_OPTIONS -jar app.jar"]