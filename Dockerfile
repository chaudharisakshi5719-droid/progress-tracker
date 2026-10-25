
# Build Stage
FROM maven:3.9-eclipse-temurin-25 AS builder

LABEL authors="Progress Tracker"

WORKDIR /app

# Copy Maven configuration for layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build executable JAR
COPY src src
RUN mvn clean package -DskipTests -B

# Runtime Stage
FROM eclipse-temurin:25-jre

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Create non-root system user
RUN useradd -ms /bin/bash spring \
    && mkdir -p /app/logs \
    && chown -R spring:spring /app

# Copy executable JAR
COPY --chown=spring:spring --from=builder /app/target/*.jar app.jar

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
