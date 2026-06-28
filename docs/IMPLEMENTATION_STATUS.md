# 渐构实施进度

> 单体架构 MVP 已完成；P0 生产部署、P1 后端补齐、P2/P3 前端与 CMS 增强已推进。

## 已完成模块

### 后端

| 模块 | 能力 |
|---|---|
| common / auth / security | 统一返回、JWT、refresh token、GitHub OAuth、异常 |
| article | CRUD、发布、归档、版本历史/恢复、相关推荐、TOC、Redis 点赞 + 定时同步 MySQL |
| category / tag | 公开 + 后台 CRUD |
| comment | 提交、两层回复、审核、点赞、spam/删除 |
| snippet / note / project / friendlink | 完整公开 + 后台 API；**友链后台 create/update** |
| upload | MinIO 上传、媒体库列表/删除 |
| subscription | 邮件订阅、确认、退订、**Newsletter 群发 API**、后台订阅列表 |
| search | MySQL 索引表搜索、Meilisearch 可选升级、suggest、索引重建 |
| schedule | RSS feed、定时重建、GitHub 定时同步、点赞计数同步 |
| system | 公开设置、扩展统计、后台仪表盘统计、后台设置、审计日志 |
| **user** | **用户列表/更新/角色分配、细粒度 permissions 加载** |

### 前台

全部文档页面 + 主题切换 + SEO composable + 首页侧边栏 + RSS 链接 + 隐私政策页 + ArticleCard 增强

### 后台 CMS

文章、片段/分类/标签/项目**编辑 UI**、用户管理、Newsletter 群发、GitHub OAuth 登录

### 工程化

- GitHub Actions CI（backend test + frontend/admin build）
- **CD 流水线**（Docker 镜像构建推送 GHCR）
- **生产 Docker Compose 全栈**（backend + frontend + nginx/admin + 依赖）
- JaCoCo 覆盖率报告
- 单元测试（含 ProdEnvValidator）

## 生产部署

```bash
cd docker
cp .env.prod.example .env
# 编辑 JWT_SECRET、MYSQL_PASSWORD 等
docker compose -f docker-compose.prod.yml up -d --build
```

访问 `http://localhost`（Nginx 统一入口）

## 可选配置

（Meilisearch、GitHub OAuth 等同前，见下方本地启动）

## 本地启动

```bash
cd docker && docker compose up -d
cd backend/monolith && .\mvnw.cmd spring-boot:run
cd frontend && npm install && npm run dev
cd admin && npm run dev
```

| 服务 | 地址 |
|---|---|
| API | http://localhost:8080/api/v1 |
| 生产入口 | http://localhost（docker-compose.prod.yml） |
| RSS | http://localhost:8080/api/v1/rss/feed.xml |
| 前台 | http://localhost:3000 |
| 后台 | http://localhost:5173/admin/ |

**账号:** `admin` / `admin123`

## 待完成

### P0 — 用户认证与权限 ✅ 已实现

| 项 | 状态 |
|---|---|
| 用户注册（图形验证码 + 邮箱验证码） | ✅ |
| 忘记密码（邮箱验证码重置） | ✅ |
| 前台登录/注册/找回密码页面 | ✅ |
| 访客可浏览、登录后才能点赞/评论/互动 | ✅ |
| `ROLE_USER` 角色与 SecurityConfig 鉴权调整 | ✅ |
| 生产 SMTP 必填校验（启用注册时） | ✅ |

### P1 — 运维与增强

| 项 | 状态 |
|---|---|
| Prometheus 告警规则（BackendDown / 5xx / JVM heap） | ✅ `docker/prometheus/alerts.yml` |
| CSP 拆分（Nginx 响应头，API 移除 CSP） | ✅ |
| 生产 MinIO 密钥校验 | ✅ `ProdEnvValidator` |
| 文章列表 N+1 优化 | ✅ 批量加载分类/标签 |
| 用户认证 E2E 基础用例 | ✅ `e2e/tests/auth.spec.ts` |
| 微服务业务逻辑从 monolith 逐模块迁移 | ⬜ scaffold |
| Grafana 通知渠道（邮件/Slack） | ⬜ 需在 Grafana UI 配置 |
| Webmention 出站通知 | ⬜ |

## 本轮新增

- **Playwright E2E**（`e2e/`，CI workflow）
- **Grafana + Prometheus**（`--profile monitoring`）
- **8 个微服务骨架**（content/user/comment/project/search/notification/upload/sync）
- **文章版本 Diff**（API + 后台编辑器 UI）
- **Webmention**（接收 API + 验证 + 后台列表 + 文章页 link 头）
