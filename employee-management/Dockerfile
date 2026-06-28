# ========================
# Stage 1: Build
# ========================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Cache Maven dependencies first
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ========================
# Stage 2: Runtime
# ========================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -S ems && adduser -S ems -G ems

COPY --from=builder /app/target/*.jar app.jar

RUN chown ems:ems app.jar
USER ems

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]
