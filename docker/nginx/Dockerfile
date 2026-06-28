FROM node:14-alpine AS admin-build
WORKDIR /app
COPY admin/package.json admin/package-lock.json ./
RUN npm ci
COPY admin/ .
RUN npm run build

FROM nginx:1.27-alpine
COPY docker/nginx/templates /etc/nginx/templates
COPY docker/nginx/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
COPY --from=admin-build /app/dist /usr/share/nginx/html/admin
RUN mkdir -p /etc/nginx/certs /var/www/certbot
EXPOSE 80 443
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget -qO- http://localhost/admin/ || wget -qO- --no-check-certificate https://localhost/admin/ || exit 1
ENTRYPOINT ["/entrypoint.sh"]
