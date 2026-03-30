# ─────────────────────────────────────────────
# Stage 1: Build the JAR with Maven
# ─────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy pom first so Maven can cache dependency downloads
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY backend/src ./src
RUN mvn clean package -DskipTests -B

# ─────────────────────────────────────────────
# Stage 2: Lightweight runtime image
# ─────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

# Install sqlite3 CLI (needed to seed the DB on first boot)
RUN apk add --no-cache sqlite

WORKDIR /app

# Copy the built JAR
COPY --from=builder /build/target/auction-system-1.0.0-SNAPSHOT.jar app.jar

# Copy SQL seed files so the entrypoint script can initialise the DB
COPY backend/src/main/resources/schema.sql ./schema.sql
COPY backend/src/main/resources/data.sql   ./data.sql

# Copy the entrypoint script
COPY docker-entrypoint.sh ./docker-entrypoint.sh
RUN chmod +x ./docker-entrypoint.sh

# The DB file will live in /app/data (mount a volume here for persistence)
VOLUME ["/app/data"]

EXPOSE 8080

ENTRYPOINT ["./docker-entrypoint.sh"]
