# 渐构 E2E 测试（Playwright）

## 安装

```bash
cd e2e
npm install
npm run install-browsers
```

## 本地运行（含登录闭环）

```bash
# 1. 启动基础设施
cd docker && docker compose up -d mysql redis minio mailhog

# 2. 启动后端（e2e 固定验证码 E2E1 + dev seed 账号 admin/admin123）
cd backend/monolith
mvn -DskipTests package
java -jar target/jiangou-1.0.0-SNAPSHOT.jar --spring.profiles.active=e2e,dev

# 3. 构建并预览前台 / 后台
cd frontend && npm run build && npx nuxt preview --port 3000 &
cd admin && npm run build && npx vite preview --port 5173 &

# 4. 跑测试（强制要求后端，登录用例不 skip）
cd e2e
FRONTEND_URL=http://127.0.0.1:3000 \
ADMIN_URL=http://127.0.0.1:5173/admin \
BACKEND_URL=http://127.0.0.1:8080 \
E2E_REQUIRE_BACKEND=1 \
npm test
```

仅静态页面（无后端）时去掉 `E2E_REQUIRE_BACKEND=1`，登录相关用例会自动 skip。

## CI

GitHub Actions：`.github/workflows/e2e.yml`

流水线会自动：

1. `docker compose up` MySQL / Redis / MinIO / Mailhog  
2. 构建并以 `e2e,dev` profile 启动后端  
3. 构建前台与 Admin 预览服务  
4. 运行 Playwright（`E2E_REQUIRE_BACKEND=1`，含 admin 登录闭环）

触发路径：`e2e/`、`frontend/`、`admin/`、`backend/monolith/`、`docker/docker-compose.yml`

## 网易 163 SMTP（生产）

**勿将授权码提交到 Git。** 在服务器 `docker/.env` 或 GitHub Repository Secrets 中配置：

| 变量 | 示例 |
|------|------|
| `MAIL_HOST` | `smtp.163.com` |
| `MAIL_PORT` | `465`（SSL）或 `587`（STARTTLS） |
| `MAIL_USER` | `yourname@163.com` |
| `MAIL_PASSWORD` | 163 邮箱「客户端授权码」 |

生产启用注册时另设 `REGISTRATION_ENABLED=true`。
