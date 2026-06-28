# 渐构 Docker 环境

## 本地开发（仅依赖服务）

启动 MySQL、Redis、MinIO、Mailhog、Meilisearch，应用在宿主机运行：

```bash
cd docker
docker compose up -d
```

| 服务 | 端口 | 说明 |
|---|---|---|
| MySQL | 3306 | jiangou / jiangou:jiangou |
| Redis | 6379 | 缓存 |
| MinIO | 9000 / 9001 | 对象存储 |
| Mailhog | 1025 / 8025 | 开发邮件 |
| Meilisearch | 7700 | 可选搜索引擎 |

## 生产全栈部署

一键启动 backend + frontend + admin（nginx 静态）+ 依赖：

```bash
cd docker
cp .env.prod.example .env
# 编辑 .env，至少设置 JWT_SECRET、MYSQL_ROOT_PASSWORD、MYSQL_PASSWORD、MINIO_ROOT_PASSWORD

bash ./scripts/bootstrap-admin.sh --email admin@example.com
```

| 服务 | 说明 |
|---|---|
| nginx | 统一入口 `:80` — `/` 前台 SSR、`/admin/` 后台 SPA、`/api/` 后端 API |
| backend | Spring Boot，Flyway 自动迁移 |
| frontend | Nuxt SSR |
| mysql / redis / minio | 数据与缓存 |

访问：`http://localhost`（或 `.env` 中 `SITE_URL`）

### 上线前检查清单

部署前请逐项确认（完整审查见 [docs/PRODUCTION_READINESS.md](../docs/PRODUCTION_READINESS.md)）：

| 类别 | 检查项 |
|---|---|
| 密钥 | `JWT_SECRET` ≥32 字符随机值；MySQL/MinIO 强密码 |
| 域名 | `SITE_URL`、`ADMIN_URL`、`CORS_ALLOWED_ORIGINS` 与实际域名一致 |
| TLS | 生产启用 HTTPS，`JWT_COOKIE_SECURE=true` |
| 邮件 | 启用用户注册/找回密码时 **必填** SMTP（见 `.env.prod.example`） |
| 备份 | 配置 `scripts/backup-mysql.sh` 定时任务 |
| 管理员 | 运行 `bash ./scripts/bootstrap-admin.sh --email <管理员邮箱>` 一次性初始化固定管理员 `yanyu` |
| 网络安全 | backend **8080 不得映射公网**；`PROMETHEUS_SCRAPE_TOKEN` 必填（见 `.env.prod.example`） |
| 功能 | 用户认证/权限隔离已落地；上线前重点改为 HTTPS、SMTP、备份、首个 ADMIN 与冒烟验收 |

### 首个 ADMIN 引导

生产环境不再依赖 `dev` seed，也不需要手工下库创建管理员。当前推荐直接运行一次性 bootstrap 脚本：

```bash
cp .env.prod.example .env
# 先把 .env 中的生产密钥、域名、MySQL/Redis/MinIO 等真实值填好

bash ./scripts/bootstrap-admin.sh --email admin@example.com --display-name "站点管理员"
```

- 脚本会固定创建首个管理员用户名 `yanyu`
- 若未通过 `--password` 或 `BOOTSTRAP_ADMIN_PASSWORD` 提供密码，脚本会安全提示输入
- 脚本内部只在本次 `docker compose up -d --build` 期间临时注入 `BOOTSTRAP_ADMIN_*`
- 创建成功后会自动把 `backend` 按常规环境再重启一次，避免管理员初始密码残留在运行中的容器环境里

非交互场景可这样执行：

```bash
BOOTSTRAP_ADMIN_PASSWORD='ChangeMe123' \
bash ./scripts/bootstrap-admin.sh --email admin@example.com
```

### 冒烟验证

首次上线后建议至少验证：

```bash
docker compose -f docker-compose.prod.yml ps
curl -f https://your-domain.com/
curl -f https://your-domain.com/admin/
curl -f https://your-domain.com/api/v1/settings/public
```

### 启用 Meilisearch

```bash
docker compose -f docker-compose.prod.yml --profile meilisearch up -d --build
# .env 中设置 SEARCH_ENGINE=meilisearch
```

### TLS

生产环境建议在 nginx 前加云负载均衡或 Certbot 终止 TLS，并将 `JWT_COOKIE_SECURE=true`。

## 环境变量

- 开发：`.env.example`
- 生产：`.env.prod.example`

## CI/CD

- `/.github/workflows/ci.yml` — 单元测试与构建
- `/.github/workflows/cd.yml` — 仅在 CI 成功后构建并推送 Docker 镜像至 GHCR；配置 SSH secrets 后自动远程部署
- `/.github/workflows/container-scan.yml` — Trivy 容器镜像扫描（PR / main / 定时）

### CD 远程部署（可选，不影响本地 `--build`）

首次部署建议先用 bootstrap 脚本完成 `yanyu` 初始化；后续日常更新仍可直接：

```bash
docker compose -f docker-compose.prod.yml up -d --build
```

GitHub Actions 在 push 到 `main`/`master` 时会构建并推送 `jiangou-backend`、`jiangou-frontend`、`jiangou-nginx` 到 GHCR。若配置了以下 **Repository secrets**，会 SSH 到服务器执行 `docker/scripts/deploy-prod-remote.sh`（仅拉取应用镜像并重启，MySQL/Redis/MinIO 等保持不变）：

| Secret | 说明 |
|---|---|
| `DEPLOY_HOST` | 服务器地址（未设置则跳过部署，仅推送镜像） |
| `DEPLOY_USER` | SSH 用户名 |
| `DEPLOY_SSH_KEY` | SSH 私钥 |
| `DEPLOY_PATH` | 可选，仓库路径，默认 `/opt/jiangou` |
| `GHCR_DEPLOY_TOKEN` | 可选，服务器拉私有镜像用的 PAT；未设置则用 `GITHUB_TOKEN` |

服务器需预先：`git clone` 本仓库、`cp .env.prod.example .env`，填好生产配置后运行一次 `bash ./scripts/bootstrap-admin.sh --email <管理员邮箱>` 完成首个管理员 `yanyu` 初始化与首次部署（同时会初始化数据卷）。后续 CD 只更新 backend/frontend/nginx 三个容器。

建议在 Repository **Variables** 中设置 `SITE_URL`（前台构建时的公网地址），并在 **Branch protection** 中要求 `CI` 与 `E2E` 检查通过后才允许合并到 `main` / `master`。

## 微服务扩展

阶段 3+ 启动 RabbitMQ、Elasticsearch、Nacos：

```bash
docker compose -f docker-compose.yml -f docker-compose.microservices.yml --profile microservices up -d
```

Gateway 骨架见 `backend/microservices/gateway-service/`（迁移期反代至 monolith）。

## 数据库备份

```bash
# Linux / macOS
MYSQL_PASSWORD=your_password ./scripts/backup-mysql.sh ./backups

# Windows
set MYSQL_PASSWORD=your_password
scripts\backup-mysql.bat backups
```

建议 cron 每日执行，脚本自动清理 7 天前备份。

## 监控（Grafana + Prometheus）

```bash
docker compose -f docker-compose.prod.yml --profile monitoring up -d
```

| 服务 | 地址 | 说明 |
|---|---|---|
| Prometheus | http://localhost:9090 | 指标采集 |
| Grafana | http://localhost:3001 | 默认 admin / admin |

预置仪表盘：**渐构 · 后端概览**（JVM、HTTP 请求率/延迟）。

Prometheus 告警规则见 `prometheus/alerts.yml`（BackendDown、HighHttp5xxRate、JvmHeapUsageHigh）。启动 monitoring profile 后可在 http://localhost:9090/alerts 查看。

Grafana 通知渠道通过 `grafana/provisioning/alerting/contactpoints.yml` 预置（默认邮件 `admin@example.com`，请改为真实地址），并在 `.env` 中配置 `GRAFANA_SMTP_*` 后重启 Grafana。

## HTTPS / TLS

Nginx 会在检测到 `docker/certs/fullchain.pem` + `privkey.pem` 时自动：

1. 在 **443** 提供 HTTPS（含 HSTS）
2. 将 **80** 重定向到 HTTPS（保留 `/.well-known/acme-challenge/` 供 ACME 验证）

**自签证书（本地/ staging）：**

```bash
bash docker/scripts/generate-self-signed-cert.sh localhost
docker compose -f docker-compose.prod.yml up -d --build nginx
```

生产环境建议使用 Let's Encrypt：将证书挂载到 `docker/certs/`，Webroot 目录为 `docker/certbot/www/`。

启用 HTTPS 后请设置 `SITE_URL=https://your-domain`、`JWT_COOKIE_SECURE=true`。

## 网易 163 SMTP

注册 / 找回密码 / Newsletter 需配置 SMTP。163 邮箱使用 **客户端授权码**（邮箱设置 → POP3/SMTP 开启后生成），不是登录密码。

`docker/.env` 示例（**勿将授权码提交到 Git**）：

```env
MAIL_HOST=smtp.163.com
MAIL_PORT=465
MAIL_USER=yourname@163.com
MAIL_PASSWORD=your-163-client-auth-code
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=false
MAIL_SMTP_SSL=true
REGISTRATION_ENABLED=true
```

GitHub / 服务器部署时，将 `MAIL_PASSWORD` 写入 **Repository Secrets** 或服务器环境变量，不要写在 workflow 或代码里。

**本地快速配置：**

```powershell
# 1. 复制 docker\.env.mail.example 为 docker\.env.mail，填入 MAIL_USER 与客户端授权码
# 2. 本地跑后端并启用注册邮件
.\docker\scripts\run-backend-with-mail.ps1

# 生产 Docker：合并到 .env.prod 后启动
.\docker\scripts\merge-mail-into-prod-env.ps1
cd docker
docker compose -f docker-compose.prod.yml --env-file .env.prod up -d --build
```

## Playwright E2E

```bash
cd e2e && npm install && npm run install-browsers
# 见 e2e/README.md：docker compose + java -jar (e2e,dev) + preview
E2E_REQUIRE_BACKEND=1 npm test
```

CI（`.github/workflows/e2e.yml`）会自动拉起 MySQL/Redis 与 `e2e,dev` 后端并跑 **admin 登录闭环**。

## 网络安全

生产部署时 **Spring Boot 8080 不得映射到公网**。`docker-compose.prod.yml` 中 backend 仅在 Docker 内网暴露，对外入口仅为 Nginx 80/443。限流与审计依赖 Nginx 注入的 `X-Real-IP`；若 backend 可被直连，攻击者可伪造该头绕过 IP 限流。

**Prometheus 指标**：生产环境 `ProdEnvValidator` 强制要求设置 `PROMETHEUS_SCRAPE_TOKEN`（见 `.env.prod.example`）。未配置时应用拒绝启动，避免 `/actuator/prometheus` 在无 token 时对内网 IP 开放。启用 `--profile monitoring` 时，Prometheus 使用该 token 作为 Bearer 凭证抓取指标。
