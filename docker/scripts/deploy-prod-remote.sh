#!/usr/bin/env bash
# Remote production deploy: pull prebuilt app images and restart (infra unchanged).
# Run on the server from repository root, after exporting image variables.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
COMPOSE_FILES=(-f docker-compose.prod.yml -f docker-compose.prod.cd.yml)

: "${JIANGOU_BACKEND_IMAGE:?JIANGOU_BACKEND_IMAGE is required}"
: "${JIANGOU_FRONTEND_IMAGE:?JIANGOU_FRONTEND_IMAGE is required}"
: "${JIANGOU_NGINX_IMAGE:?JIANGOU_NGINX_IMAGE is required}"

cd "${DOCKER_DIR}"

if [[ ! -f .env ]]; then
  echo "Missing ${DOCKER_DIR}/.env — copy .env.prod.example and configure secrets first." >&2
  exit 1
fi

echo "Pulling ${JIANGOU_BACKEND_IMAGE}"
echo "Pulling ${JIANGOU_FRONTEND_IMAGE}"
echo "Pulling ${JIANGOU_NGINX_IMAGE}"

docker compose "${COMPOSE_FILES[@]}" pull backend frontend nginx
docker compose "${COMPOSE_FILES[@]}" up -d --no-build --remove-orphans backend frontend nginx

HEALTH_URL="${HEALTH_URL:-http://127.0.0.1/api/v1/settings/public}"
echo "Waiting for health at ${HEALTH_URL}"
for i in $(seq 1 30); do
  if curl -fsS "${HEALTH_URL}" >/dev/null; then
    echo "Health check passed."
    echo "Deploy complete."
    exit 0
  fi
  sleep 5
done

echo "Health check failed after deploy." >&2
exit 1
