#!/bin/sh
# docker-entrypoint.sh
# Initialises the SQLite database on first boot, then starts the Spring Boot app.

set -e

DB_PATH="/app/data/auction.db"

echo "=== Atlas Auction System Entrypoint ==="

# Always re-apply schema (safe — all tables use CREATE IF NOT EXISTS)
echo "[INFO] Applying schema to $DB_PATH ..."
mkdir -p /app/data
sqlite3 "$DB_PATH" < /app/schema.sql

# Always re-run data.sql so fresh auctions are seeded on every restart
# The SQL uses DELETE+INSERT so old duplicates are cleared automatically
echo "[INFO] Seeding data into $DB_PATH ..."
sqlite3 "$DB_PATH" < /app/data.sql

echo "[INFO] Database ready."
echo "[INFO] Starting Spring Boot on port 8080 ..."

exec java \
  -Dspring.datasource.url="jdbc:sqlite:$DB_PATH" \
  -jar /app/app.jar
