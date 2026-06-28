#!/bin/sh
set -eu

CONF_DIR="/etc/nginx/conf.d"
TEMPLATE_DIR="/etc/nginx/templates"
CERT="/etc/nginx/certs/fullchain.pem"
KEY="/etc/nginx/certs/privkey.pem"

rm -f "${CONF_DIR}"/*.conf

cp "${TEMPLATE_DIR}/00-shared.conf" "${CONF_DIR}/00-shared.conf"

if [ -f "${CERT}" ] && [ -f "${KEY}" ]; then
  echo "TLS certificates found — enabling HTTPS on 443 and HTTP→HTTPS redirect."
  cp "${TEMPLATE_DIR}/http-redirect.conf" "${CONF_DIR}/01-http-redirect.conf"
  cp "${TEMPLATE_DIR}/https-app.conf" "${CONF_DIR}/02-https-app.conf"
else
  echo "No TLS certificates — serving HTTP only on port 80."
  cp "${TEMPLATE_DIR}/http-app.conf" "${CONF_DIR}/01-http-app.conf"
fi

exec nginx -g 'daemon off;'
