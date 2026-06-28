# 渐构上线部署审查报告

> 审查日期：2026-06-27  
> 审查范围：整体代码、Docker 生产栈、安全与权限、用户认证缺口  
> 目标标准：可安全上线的个人/社区博客平台

---

## 1. 执行摘要

| 维度 | 当前状态 | 上线就绪度 |
|---|---|---|
| 基础设施与 Docker 全栈 | ✅ 已具备 | **就绪** |
| 后台 CMS 认证 | ✅ JWT + OAuth + RBAC | **就绪** |
| 内容公开浏览 | ✅ 文章/片段/项目等 SSR | **就绪** |
| 前台用户注册/登录 | ✅ 已实现 | **就绪** |
| 注册验证码 / 邮箱验证 | ✅ 已实现 | **就绪** |
| 忘记密码（邮箱验证码） | ✅ 已实现 | **就绪** |
| 访客 vs 登录用户权限隔离 | ✅ 已实现 | **就绪** |
| TLS / 邮件 / 备份 | ⚠️ 文档化，需运维配置 | **条件就绪** |

**结论：** 用户认证 P0 已完成。对外上线仍需完成运维配置（TLS、SMTP、密钥、备份）并执行 §6 验收清单。

---

## 2. 生产部署就绪清单

### 2.1 已就绪项 ✅

| 类别 | 项 | 证据 |
|---|---|---|
| 容器化 | 生产 Compose 全栈（nginx + backend + frontend + mysql + redis + minio） | `docker/docker-compose.prod.yml` |
| 反向代理 | Nginx 统一入口：`/` 前台、`/admin/` 后台、`/api/` API | `docker/nginx/default.conf` |
| 密钥校验 | 生产启动校验 `JWT_SECRET`（≥32 字符）、`MYSQL_PASSWORD` | `ProdEnvValidator.java` |
| 数据库迁移 | Flyway 自动迁移 V1–V11 | `backend/monolith/src/main/resources/db/migration/` |
| 健康检查 | `/actuator/health`、`/actuator/info` 公开 | `SecurityConfig.java` |
| 指标保护 | `/actuator/prometheus` 需 ADMIN | `SecurityConfig.java` |
| API 默认拒绝 | 未知路径 `denyAll()`，显式白名单放行 | `SecurityConfig.java` |
| Swagger 关闭 | 生产 profile 禁用 OpenAPI UI | `application-prod.yml` |
| CI/CD | 单元测试 + 镜像构建推送 GHCR | `.github/workflows/ci.yml`, `cd.yml` |
| E2E | Playwright 测试流水线 | `e2e/`, `.github/workflows/e2e.yml` |
| 限流 | Redis 限流（登录、评论、点赞、订阅） | `RateLimitFilter.java` |
| 上传安全 | 魔数校验、图片解码、安全 object key | 上传模块 |
| 搜索 | MySQL FULLTEXT/ngram + Meilisearch 可选 | `V6__search_fulltext_ngram.sql` |
| 监控 | Prometheus + Grafana（`--profile monitoring`） | `docker/README.md` |
| 备份脚本 | MySQL 备份 + 7 天清理 | `docker/scripts/` |

### 2.2 上线前必须配置 ⚠️

| 项 | 说明 | 参考 |
|---|---|---|
| `JWT_SECRET` | 随机字符串 ≥32 字符，禁止默认值 | `docker/.env.prod.example` |
| `MYSQL_*` / `MINIO_*` | 强密码，勿用 example 默认值 | 同上 |
| `JWT_COOKIE_SECURE=true` | HTTPS 环境下启用 Secure Cookie | `application-prod.yml` |
| TLS 终止 | Nginx 前加 LB 或 Certbot；裸 HTTP 仅内网/测试 | `docker/README.md` |
| `CORS_ALLOWED_ORIGINS` | 设为实际域名，勿用 `*` | `application-prod.yml` |
| `SITE_URL` / `ADMIN_URL` | 与对外域名一致（邮件链接、OAuth 回调） | `.env.prod.example` |
| 邮件 SMTP | 注册验证、找回密码、Newsletter 均依赖 | 见 §4 |
| MySQL 备份 cron | 生产需每日备份并验证恢复 | `docker/scripts/backup-mysql.sh` |
| 初始管理员 | 生产无 dev seed，需手动创建或迁移脚本 | 见 §5.3 |
| `PROMETHEUS_SCRAPE_TOKEN` | 生产启动强制校验，Prometheus 抓取凭证 | `docker/.env.prod.example` |
| Backend 网络隔离 | **8080 不得映射公网**，仅 Nginx 反代 | `docker/README.md` §网络安全 |

### 2.3 建议改进（非阻塞） 📋

| 优先级 | 项 | 现状 | 建议 |
|---|---|---|---|
| P1 | CSP 策略 | API 层 CSP 已移除 | ✅ Nginx 统一配置 CSP |
| P1 | 生产环境变量扩展校验 | JWT + MySQL + MinIO + MAIL | ✅ ProdEnvValidator |
| P2 | 日志与告警 | Grafana 仪表盘 + Prometheus 规则 | ✅ alerts.yml；Grafana 通知渠道待配 |
| P2 | 文章列表 N+1 | 批量加载 | ✅ ArticleService.toListItems |
| P3 | Webmention 出站 | 接收已实现，出站 ping 未做 | 按路线图补全 |

---

## 3. 当前权限模型（已实现）

```
访客（无 JWT）
  ├─ ✅ 浏览文章/片段/项目/搜索/RSS
  └─ ❌ 不可点赞、评论、留言、友链申请（返回 40101，前端引导登录）

注册用户（前台 JWT，ROLE_USER）
  ├─ ✅ 点赞、评论、留言板、友链申请
  └─ ❌ 不可访问 /api/v1/admin/**

管理员 / 委派权限（JWT + ADMIN 或细粒度 permissions）
  └─ ✅ CMS 功能（/admin SPA + 对应 API 权限）
```

认证入口：**frontend/** 登录/注册/找回密码 + **admin/** CMS 登录（含 GitHub OAuth）。

实现参考：[USER_AUTH_DESIGN.md](./USER_AUTH_DESIGN.md)、Flyway `V7__user_auth.sql`。

---

## 4. 待修复项（代码审计摘要）

> **2026-06-28 复核更新：** C1 已修复（新增 `StartupSecretGuard`，非 dev 环境用默认 JWT 密钥即拒绝启动）；C2/M6 复核已在 `.env.prod.example` 修复；C4 已修复（`docker-compose.prod.yml` backend 现透传 `MYSQL_USE_SSL`/`MYSQL_ALLOW_PUBLIC_KEY_RETRIEVAL`）；H1 复核已修复（限流默认不信任代理头）。H2/H3 疑已修待 diff 确认；evict 降权延迟、M1/M2/M7/H5 逐项进行中。详见 `docs/CODE_AUDIT_2026-06-27.md` §0 与 `JianGou_审计报告_2026-06-28.md`。


> 完整报告见 [CODE_AUDIT_2026-06-27.md](./CODE_AUDIT_2026-06-27.md)

| 优先级 | 项 | 说明 |
|---|---|---|
| P0 | 部署 env 与 ProdEnvValidator 对齐 | 勿用 localhost；配置 `MYSQL_USE_SSL=false`（Docker） |
| P0 | 生产首个 ADMIN 账号 | 通过 `docker/scripts/bootstrap-admin.sh` 一次性创建固定管理员 `yanyu`，不再手工建号 |
| P1 | 限流信任链 | 直连环境关闭 `RATE_LIMIT_TRUST_PROXY` |
| P1 | 注册发码反枚举 | `sendRegisterCode` 与 reset 流程对齐 |
| P1 | 文章点赞发布校验 | `ArticleLikeService` 增加 published/public |
| P1 | CSP / captcha img 校验 | Nginx CSP 收紧；前后台 captcha src 白名单 |
| P2 | Webmention body 上限、友链限流 | 见审计报告 M1–M3 |

---

## 5. 实施路线图

### Phase 0 — 上线前运维（1–2 天）🔴 当前重点

- [ ] 配置 `.env` 生产密钥与**真实域名**（参考更新后的 `docker/.env.prod.example`）
- [ ] 启用 TLS + `JWT_COOKIE_SECURE=true`
- [ ] 配置 SMTP（Newsletter；启用 `REGISTRATION_ENABLED` 时必填）
- [ ] 配置 MySQL 定时备份
- [ ] 运行 `bash docker/scripts/bootstrap-admin.sh --email <管理员邮箱>`，完成首个管理员 `yanyu` 初始化与首轮部署
- [ ] 冒烟测试：确认 bootstrap 脚本完成后 `docker compose -f docker-compose.prod.yml ps` 全部核心服务正常

### Phase 1 — 审计 P1 代码修复（约 3–5 天）

- [x] H1–H3：限流、注册枚举、文章点赞（见 [CODE_AUDIT_2026-06-27.md](./CODE_AUDIT_2026-06-27.md)）
- [x] CI 门禁 CD（CD 改为等待 CI 成功；branch protection 仍需在 GitHub 仓库设置）

### Phase 2 — 体验与安全增强

- [x] Admin CSRF 预热、订阅 token 改 POST
- [ ] Grafana 通知渠道（需真实 SMTP / 收件人）
- [x] 容器镜像扫描

### Phase 3 — 架构演进（可选）

- [ ] Webmention 出站
- [ ] 微服务按域迁移（见 `backend/microservices/`）

---

## 6. 验收标准（上线 Gate）

### 6.1 部署验收

- [ ] `bash docker/scripts/bootstrap-admin.sh --email <管理员邮箱>` 执行完成后，全服务 healthy
- [ ] HTTPS 访问前台、后台、API 正常
- [ ] Flyway 迁移无报错
- [ ] 备份脚本可执行且可恢复

### 6.2 认证与权限验收

- [ ] 未登录：可浏览文章列表与详情
- [ ] 未登录：点赞/评论/留言返回 401，前端引导登录
- [ ] 注册：图形验证码错误拒绝；邮箱验证码错误拒绝；重复邮箱拒绝
- [ ] 注册成功后邮箱已验证，可登录
- [ ] 忘记密码：邮箱验证码有效窗口内可重置；过期拒绝
- [ ] 登录用户：可点赞、评论；重复点赞仍被去重
- [ ] 管理员：CMS 功能不受影响

### 6.3 安全验收

- [ ] 生产 Swagger 不可访问
- [ ] 未知 API 路径返回 403
- [ ] JWT_SECRET 非默认值且 ≥32 字符
- [ ] 验证码接口有频率限制（防刷）
- [ ] 邮箱验证码错误尝试超过 5 次后拒绝（需重新获取）
- [ ] **Backend 8080 不对公网暴露**（仅 Nginx 内网反代；`X-Real-IP` 由可信代理注入）
- [ ] HTTPS 站点已设置 `JWT_COOKIE_SECURE=true`

---

## 7. 相关文档

| 文档 | 说明 |
|---|---|
| [USER_AUTH_DESIGN.md](./USER_AUTH_DESIGN.md) | 注册/验证码/找回密码/权限隔离详细设计 |
| [API.md](./API.md) | 现有 API + 规划中的用户认证接口 |
| [IMPLEMENTATION_STATUS.md](./IMPLEMENTATION_STATUS.md) | 实施进度跟踪 |
| [docker/README.md](../docker/README.md) | 生产部署与运维 |
| [analysis/README.md](./analysis/README.md) | 代码质量与待办 |
| [CODE_AUDIT_2026-06-27.md](./CODE_AUDIT_2026-06-27.md) | **全栈源代码审计报告（本次）** |
