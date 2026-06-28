# 渐构（JianGou）

渐次构建，理解计算机世界 —— 个人技术博客 / 技术知识沉淀平台。

## 技术栈

| 层级 | 技术 |
|---|---|
| 前台 | Vue 3 + Nuxt 3 + TypeScript |
| 后台 CMS | Vue 3 + Vite + TypeScript |
| 后端 | Java 1.8 + Spring Boot 2 |
| 数据库 | MySQL 8.0+ |
| 缓存 | Redis 7+ |
| 对象存储 | MinIO / OSS |

## 目录结构

```text
├── frontend/                 # 访客前台（Nuxt 3 SSR）
├── admin/                    # 管理后台（Vue 3 + Vite SPA）
├── backend/
│   ├── monolith/             # 单体架构（MVP ~ 阶段 2，推荐起步）
│   └── microservices/        # 分布式微服务架构（阶段 3+ 演进）
├── docs/                     # 工程文档与接口规范
├── sql/                      # 数据库初始化脚本
├── docker/                   # 本地开发依赖（MySQL / Redis / MinIO）
└── JianGou_FULL_DOCS_v3_MySQL.md  # 完整工程蓝图（合并版）
```

## 架构选型

| 阶段 | 推荐架构 | 说明 |
|---|---|---|
| MVP ~ 内容增强 | [backend/monolith](./backend/monolith/README.md) | 单一 Spring Boot JAR，部署简单 |
| 工程化 ~ 高可用 | [backend/microservices](./backend/microservices/README.md) | 按域拆分微服务，MQ + ES |

详见 [docs/architecture/](./docs/00-README.md)。

## 快速开始

### 1. 启动基础设施

```bash
cd docker
docker compose up -d
```

### 2. 初始化数据库

Docker 会创建空的 `jiangou` 数据库；表结构和索引由后端启动时的 Flyway 迁移自动执行。
不要再手动执行 `sql/indexes.sql`，避免和 `backend/monolith/src/main/resources/db/migration/V2__indexes.sql` 重复。

### 3. 启动各应用

```bash
# 前台（Nuxt）
cd frontend && npm install && npm run dev

# 后台 CMS
cd admin && npm install && npm run dev

# 后端（单体，默认）
cd backend/monolith && .\mvnw.cmd spring-boot:run
```

## 文档索引

| 文档 | 说明 |
|---|---|
| [docs/00-README.md](./docs/00-README.md) | 文档包总览 |
| [docs/API.md](./docs/API.md) | REST API 接口文档 |
| [docs/PRODUCTION_READINESS.md](./docs/PRODUCTION_READINESS.md) | **上线部署审查与验收标准** |
| [docs/CODE_AUDIT_2026-06-28.md](./docs/CODE_AUDIT_2026-06-28.md) | **全栈源代码审计报告** |
| [docs/USER_AUTH_DESIGN.md](./docs/USER_AUTH_DESIGN.md) | **用户注册/验证码/找回密码/权限设计** |
| [docs/architecture/monolith.md](./docs/architecture/monolith.md) | 单体架构方案 |
| [docs/architecture/microservices.md](./docs/architecture/microservices.md) | 分布式架构方案 |
| [JianGou_FULL_DOCS_v3_MySQL.md](./JianGou_FULL_DOCS_v3_MySQL.md) | 完整设计（01~15 章 + 优化方案） |

## 重要约束

1. 前端优先 Vue 技术栈，后台 API 统一 `/api/v1`。
2. 所有表含审计字段（`created_at` / `updated_at` / `deleted_at`）与软删除。
3. 统一返回 `ApiResult`，异常走 `GlobalExceptionHandler`。
4. 用户输入 Markdown 必须 sanitize，防 XSS。
5. 渐进式开发，不要求一次实现全部功能。

## 许可证

Private — 个人项目。
