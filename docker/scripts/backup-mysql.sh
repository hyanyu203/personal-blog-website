#!/usr/bin/env bash
# MySQL 备份脚本 — 用于 cron 或手动执行
# 用法: ./backup-mysql.sh [输出目录]
set -euo pipefail

BACKUP_DIR="${1:-./backups}"
TIMESTAMP="$(date +%Y%m%d_%H%M%S)"
MYSQL_HOST="${MYSQL_HOST:-localhost}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_DATABASE="${MYSQL_DATABASE:-jiangou}"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-}"

mkdir -p "$BACKUP_DIR"
OUTPUT="$BACKUP_DIR/jiangou_${TIMESTAMP}.sql.gz"

echo "Backing up ${MYSQL_DATABASE}@${MYSQL_HOST}:${MYSQL_PORT} -> ${OUTPUT}"

if [ -n "$MYSQL_PASSWORD" ]; then
  export MYSQL_PWD="$MYSQL_PASSWORD"
fi

mysqldump \
  -h "$MYSQL_HOST" \
  -P "$MYSQL_PORT" \
  -u "$MYSQL_USER" \
  --single-transaction \
  --routines \
  --triggers \
  "$MYSQL_DATABASE" | gzip > "$OUTPUT"

echo "Done: ${OUTPUT} ($(du -h "$OUTPUT" | cut -f1))"

# 保留最近 7 天备份
find "$BACKUP_DIR" -name 'jiangou_*.sql.gz' -mtime +7 -delete 2>/dev/null || true
