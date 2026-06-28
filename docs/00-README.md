# 渐构（JianGou）工程文档包

> project: 渐构 JianGou | version: 1.0  
> frontend: Vue 3 + Nuxt 3.5.3 + TypeScript  
> backend: Java 1.8 + Spring Boot 2  
> database: MySQL 8.0+

本文档包用于指导开发者按模块搭建「渐构」个人技术博客平台。

## 文档清单

| 文件 | 用途 |
|---|---|
| [API.md](./API.md) | REST API 接口文档（公开 / 认证 / 后台） |
| [PRODUCTION_READINESS.md](./PRODUCTION_READINESS.md) | 上线部署审查、就绪清单、验收标准 |
| [USER_AUTH_DESIGN.md](./USER_AUTH_DESIGN.md) | 前台用户认证设计（注册验证码、找回密码、权限隔离） |
| [IMPLEMENTATION_STATUS.md](./IMPLEMENTATION_STATUS.md) | 实施进度与待办跟踪 |
| [CODE_AUDIT_2026-06-28.md](./CODE_AUDIT_2026-06-28.md) | 全栈源代码安全审计报告 |
| [TECH_STACK_UPGRADE.md](./TECH_STACK_UPGRADE.md) | 技术栈升级评估 |
| [architecture/monolith.md](./architecture/monolith.md) | 单体架构方案 |
| [architecture/microservices.md](./architecture/microservices.md) | 分布式微服务方案 |
| [../JianGou_FULL_DOCS_v3_MySQL.md](../JianGou_FULL_DOCS_v3_MySQL.md) | 完整蓝图（01~15 章 + DB 优化 + 架构演进） |

### 完整蓝图章节对照

| 章节 | 主题 | 在合并文档中的位置 |
|---|---|---|
| 01 | 项目定位、参考站分析 | `docs/01-PROJECT.md` |
| 02 | 总体架构、部署拓扑 | `docs/02-ARCHITECTURE.md` |
| 03 | 技术栈选型 | `docs/03-TECH_STACK.md` |
| 04 | 前端页面与组件 | `docs/04-FRONTEND.md` |
| 05 | 后端模块与分层 | `docs/05-BACKEND.md` |
| 06 | MySQL 表设计 | `docs/06-DATABASE.md` |
| 07 | REST API 设计 | `docs/07-API.md` → 详见 [API.md](./API.md) |
| 08 | 关键业务逻辑 | `docs/08-BUSINESS.md` |
| 09 | 安全设计 | `docs/09-SECURITY.md` |
| 10 | 搜索方案 | `docs/10-SEARCH.md` |
| 11 | 部署设计 | `docs/11-DEPLOY.md` |
| 12 | 后台 CMS | `docs/12-ADMIN.md` |
| 13 | 编码规范 | `docs/13-CODE_STYLE.md` |
| 14 | Codex 开发规则 | `docs/14-CODEX_RULES.md` |
| 15 | 开发路线 | `docs/15-ROADMAP.md` |

## 代码目录对照

| 目录 | 技术 | 说明 |
|---|---|---|
| [../frontend/README.md](../frontend/README.md) | Nuxt 3.5.3 | 访客前台 SSR/SSG |
| [../admin/README.md](../admin/README.md) | Vue 3 + Vite | 后台 CMS |
| [../backend/monolith/README.md](../backend/monolith/README.md) | Spring Boot 2 单体 | 默认后端，MVP 起步 |
| [../backend/microservices/README.md](../backend/microservices/README.md) | Spring Cloud 微服务 | 阶段 3+ 演进 |

## 重要约束

1. 前端优先 Vue；后端 Java + Spring Boot 2；主库 MySQL。
2. 接口 RESTful，基础路径 `/api/v1`，后台 `/api/v1/admin`。
3. 统一 `ApiResult` 返回；Flyway 管理迁移；OpenAPI 注解。
4. 所有表软删除 + 审计字段；内容 XSS 防护。
5. 可渐进开发，先 MVP 再扩展。

## 推荐第一条开发指令

```text
请阅读 docs/00-README.md 与 docs/API.md，以及 JianGou_FULL_DOCS_v3_MySQL.md。
在 backend/monolith 实现 article 模块骨架：Entity/DTO/VO/Mapper/Service/Controller、
Flyway 迁移、ApiResult、OpenAPI 注解。前台 frontend 实现 /posts 列表页。
```
