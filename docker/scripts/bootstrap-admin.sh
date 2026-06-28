#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

ENV_FILE="${DOCKER_DIR}/.env"
COMPOSE_FILE="${DOCKER_DIR}/docker-compose.prod.yml"
USERNAME="yanyu"
EMAIL="${BOOTSTRAP_ADMIN_EMAIL:-}"
PASSWORD="${BOOTSTRAP_ADMIN_PASSWORD:-}"
DISPLAY_NAME="${BOOTSTRAP_ADMIN_DISPLAY_NAME:-yanyu}"
TIMEOUT_SECONDS="${BOOTSTRAP_ADMIN_TIMEOUT_SECONDS:-180}"
RESTORE_BACKEND_ON_EXIT=0

usage() {
  cat <<'EOF'
Usage:
  bash docker/scripts/bootstrap-admin.sh --email <admin-email> [options]

Options:
  --email <value>         Admin email for the fixed bootstrap user yanyu.
  --password <value>      Admin password. If omitted, the script prompts securely.
  --display-name <value>  Optional display name. Default: yanyu
  --env-file <path>       Docker Compose env file. Default: docker/.env
  --compose-file <path>   Compose file. Default: docker/docker-compose.prod.yml
  --timeout <seconds>     Wait timeout for backend health. Default: 180
  -h, --help              Show this help message.

Environment fallbacks:
  BOOTSTRAP_ADMIN_EMAIL
  BOOTSTRAP_ADMIN_PASSWORD
  BOOTSTRAP_ADMIN_DISPLAY_NAME
  BOOTSTRAP_ADMIN_TIMEOUT_SECONDS
EOF
}

resolve_path() {
  local path="$1"
  case "$path" in
    /*) printf '%s\n' "$path" ;;
    *) printf '%s/%s\n' "$PWD" "$path" ;;
  esac
}

cleanup() {
  if [[ "$RESTORE_BACKEND_ON_EXIT" -eq 1 ]]; then
    docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" up -d backend >/dev/null 2>&1 || true
  fi
}

trap cleanup EXIT

require_option_value() {
  local option="$1"
  local value="${2:-}"
  if [[ -z "$value" || "$value" == --* ]]; then
    echo "Missing value for ${option}." >&2
    exit 1
  fi
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --email)
      require_option_value "$1" "${2:-}"
      EMAIL="${2:-}"
      shift 2
      ;;
    --password)
      require_option_value "$1" "${2:-}"
      PASSWORD="${2:-}"
      shift 2
      ;;
    --display-name)
      require_option_value "$1" "${2:-}"
      DISPLAY_NAME="${2:-}"
      shift 2
      ;;
    --env-file)
      require_option_value "$1" "${2:-}"
      ENV_FILE="$(resolve_path "${2:-}")"
      shift 2
      ;;
    --compose-file)
      require_option_value "$1" "${2:-}"
      COMPOSE_FILE="$(resolve_path "${2:-}")"
      shift 2
      ;;
    --timeout)
      require_option_value "$1" "${2:-}"
      TIMEOUT_SECONDS="${2:-}"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown argument: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
done

if [[ -z "$EMAIL" && -t 0 ]]; then
  read -r -p "Email for bootstrap admin yanyu: " EMAIL
fi

if [[ -z "$PASSWORD" && -t 0 ]]; then
  read -r -s -p "Password for bootstrap admin yanyu: " PASSWORD
  echo
fi

if [[ ! -f "$ENV_FILE" ]]; then
  echo "Missing env file: $ENV_FILE" >&2
  exit 1
fi

if [[ ! -f "$COMPOSE_FILE" ]]; then
  echo "Missing compose file: $COMPOSE_FILE" >&2
  exit 1
fi

if ! command -v docker >/dev/null 2>&1; then
  echo "docker is required." >&2
  exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
  echo "docker compose is required." >&2
  exit 1
fi

if [[ -z "$EMAIL" ]]; then
  echo "Admin email is required. Use --email or BOOTSTRAP_ADMIN_EMAIL." >&2
  exit 1
fi

if [[ "$EMAIL" != *"@"* ]]; then
  echo "Admin email must contain '@'." >&2
  exit 1
fi

if [[ -z "$PASSWORD" ]]; then
  echo "Admin password is required. Use --password, BOOTSTRAP_ADMIN_PASSWORD, or the secure prompt." >&2
  exit 1
fi

if (( ${#PASSWORD} < 8 )) || [[ ! "$PASSWORD" =~ [A-Za-z] ]] || [[ ! "$PASSWORD" =~ [0-9] ]]; then
  echo "Admin password must be at least 8 characters and include letters and numbers." >&2
  exit 1
fi

if [[ ! "$TIMEOUT_SECONDS" =~ ^[0-9]+$ ]] || (( TIMEOUT_SECONDS <= 0 )); then
  echo "Timeout must be a positive integer." >&2
  exit 1
fi

read_env_value() {
  local key="$1"
  local fallback="${2:-}"
  local line
  line="$(grep -E "^${key}=" "$ENV_FILE" | tail -n 1 || true)"
  if [[ -z "$line" ]]; then
    printf '%s' "$fallback"
    return
  fi
  line="${line#*=}"
  line="${line%$'\r'}"
  printf '%s' "$line"
}

wait_for_backend() {
  local deadline
  deadline=$(( $(date +%s) + TIMEOUT_SECONDS ))

  while (( $(date +%s) < deadline )); do
    local status
    status="$(docker inspect --format '{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' jiangou-backend 2>/dev/null || true)"
    if [[ "$status" == "healthy" ]]; then
      return 0
    fi
    if [[ "$status" == "unhealthy" || "$status" == "exited" || "$status" == "dead" ]]; then
      break
    fi
    sleep 5
  done

  echo "Backend did not become healthy within ${TIMEOUT_SECONDS}s." >&2
  docker logs --tail 200 jiangou-backend >&2 || true
  return 1
}

run_mysql_query() {
  local sql="$1"
  local mysql_user
  local mysql_password
  local mysql_database

  mysql_user="$(read_env_value MYSQL_USER "jiangou")"
  mysql_password="$(read_env_value MYSQL_PASSWORD "")"
  mysql_database="$(read_env_value MYSQL_DATABASE "jiangou")"

  docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" exec -T mysql \
    env MYSQL_PWD="$mysql_password" \
    mysql -u"$mysql_user" "$mysql_database" -Nse "$sql"
}

verify_bootstrap_admin() {
  local yanyu_admin_count
  local any_admin_count
  local existing_admin

  yanyu_admin_count="$(run_mysql_query "SELECT COUNT(*) FROM users u INNER JOIN user_roles ur ON ur.user_id = u.id INNER JOIN roles r ON r.id = ur.role_id WHERE u.username = 'yanyu' AND u.deleted_at IS NULL AND r.code = 'ADMIN' AND r.deleted_at IS NULL;")"
  yanyu_admin_count="${yanyu_admin_count%$'\r'}"
  if [[ "$yanyu_admin_count" =~ ^[0-9]+$ ]] && (( yanyu_admin_count > 0 )); then
    return 0
  fi

  any_admin_count="$(run_mysql_query "SELECT COUNT(*) FROM users u INNER JOIN user_roles ur ON ur.user_id = u.id INNER JOIN roles r ON r.id = ur.role_id WHERE u.deleted_at IS NULL AND r.code = 'ADMIN' AND r.deleted_at IS NULL;")"
  any_admin_count="${any_admin_count%$'\r'}"
  if [[ "$any_admin_count" =~ ^[0-9]+$ ]] && (( any_admin_count > 0 )); then
    existing_admin="$(run_mysql_query "SELECT u.username FROM users u INNER JOIN user_roles ur ON ur.user_id = u.id INNER JOIN roles r ON r.id = ur.role_id WHERE u.deleted_at IS NULL AND r.code = 'ADMIN' AND r.deleted_at IS NULL ORDER BY u.id ASC LIMIT 1;")"
    existing_admin="${existing_admin%$'\r'}"
    echo "An ADMIN user already exists (${existing_admin:-unknown}), so bootstrap user yanyu was not created." >&2
    return 1
  fi

  echo "Backend is healthy, but bootstrap admin yanyu was not found." >&2
  return 1
}

echo "Starting production stack with temporary bootstrap credentials for yanyu..."
RESTORE_BACKEND_ON_EXIT=1
BOOTSTRAP_ADMIN_USERNAME="$USERNAME" \
BOOTSTRAP_ADMIN_EMAIL="$EMAIL" \
BOOTSTRAP_ADMIN_PASSWORD="$PASSWORD" \
BOOTSTRAP_ADMIN_DISPLAY_NAME="$DISPLAY_NAME" \
docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" up -d --build

wait_for_backend
verify_bootstrap_admin

echo "Bootstrap admin yanyu created. Restarting backend without bootstrap secrets..."
docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" up -d backend
wait_for_backend
RESTORE_BACKEND_ON_EXIT=0

echo "Bootstrap complete."
echo "Username: yanyu"
echo "Email: $EMAIL"
