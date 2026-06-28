FROM node:14-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
ARG NUXT_PUBLIC_API_BASE=/api/v1
ARG NUXT_PUBLIC_SITE_URL=http://localhost
ARG NUXT_API_BASE_INTERNAL=http://backend:8080/api/v1
ENV NUXT_PUBLIC_API_BASE=$NUXT_PUBLIC_API_BASE
ENV NUXT_PUBLIC_SITE_URL=$NUXT_PUBLIC_SITE_URL
ENV NUXT_API_BASE_INTERNAL=$NUXT_API_BASE_INTERNAL
RUN npm run build

FROM node:14-alpine
WORKDIR /app
ENV NODE_ENV=production
ENV HOST=0.0.0.0
ENV PORT=3000
ENV NUXT_PUBLIC_API_BASE=/api/v1
ENV NUXT_API_BASE_INTERNAL=http://backend:8080/api/v1
COPY --from=build /app/.output ./.output
EXPOSE 3000
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:3000/ || exit 1
CMD ["node", ".output/server/index.mjs"]
