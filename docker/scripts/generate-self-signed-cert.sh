#!/usr/bin/env bash
# Generate a self-signed certificate for local/staging HTTPS (365 days).
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CERT_DIR="$(cd "${SCRIPT_DIR}/../certs" && pwd)"
DOMAIN="${1:-localhost}"

mkdir -p "${CERT_DIR}"

openssl req -x509 -nodes -newkey rsa:2048 -days 365 \
  -keyout "${CERT_DIR}/privkey.pem" \
  -out "${CERT_DIR}/fullchain.pem" \
  -subj "/CN=${DOMAIN}" \
  -addext "subjectAltName=DNS:${DOMAIN},DNS:localhost,IP:127.0.0.1"

echo "Generated:"
echo "  ${CERT_DIR}/fullchain.pem"
echo "  ${CERT_DIR}/privkey.pem"
echo "Restart nginx to enable HTTPS."
