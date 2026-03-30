#!/bin/sh
# docker-entrypoint.sh
# Initialises the SQLite database on first boot, then starts the Spring Boot app.

set -e

DB_PATH="/app/data/auction.db"

echo "=== Auction System Docker Entrypoint ==="

# Seed the DB only if it doesn't exist yet (first run)
if [ ! -f "$DB_PATH" ]; then
  echo "[INFO] No database found — creating and seeding $DB_PATH ..."
  sqlite3 "$DB_PATH" < /app/schema.sql
  sqlite3 "$DB_PATH" < /app/data.sql
  echo "[INFO] Database initialised successfully."
else
  echo "[INFO] Existing database found at $DB_PATH — skipping seed."
fi

echo "[INFO] Starting Spring Boot application on port 8080 ..."
exec java \
  -Dspring.datasource.url="jdbc:sqlite:$DB_PATH" \
  -jar /app/app.jar
