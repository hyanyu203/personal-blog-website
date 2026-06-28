# 单体架构方案

适用阶段：MVP 到个人博客内容增强阶段，预计可支撑 3 到 5 年。

## 架构图

```text
Browser
   │ HTTPS
Nginx（TLS / Gzip / 限流 / 反代）
   ├── Nuxt SSR :3000      -> frontend/
   ├── /admin 静态 SPA     -> admin/
   └── /api/v1/*           -> backend/monolith/ :8080
                                │
                     ┌──────────┼──────────┐
                     │          │          │
                   MySQL      Redis      MinIO
```

## 组件

| 组件 | 路径 | 说明 |
|---|---|---|
| 前台 | `frontend/` | Nuxt 3.5.3 + Vue 3，SSR/SSG |
| 后台 | `admin/` | Vue 3 + Vite SPA |
| API | `backend/monolith/` | 单一 Spring Boot JAR，Java 1.8 / Spring Boot 2.7.18 |
| 数据库迁移 | `backend/monolith/src/main/resources/db/migration/` | Flyway 正式迁移入口 |
| 历史 SQL | `sql/` | 仅保留参考脚本，不作为 Docker 自动初始化入口 |

## 包结构

```text
backend/monolith/src/main/java/com/jiangou/
  common/       # ApiResult、异常、分页、通用服务
  config/       # Redis、MinIO、Swagger、MyBatis mapper 扫描
  security/     # JWT、Spring Security、限流过滤器
  auth/         # 登录、OAuth
  user/         # 用户、角色、权限
  article/      # 文章、版本、状态机
  category/     # 分类
  tag/          # 标签
  snippet/      # 代码片段
  note/         # 碎碎念
  comment/      # 评论、审核
  project/      # GitHub 项目同步
  upload/       # 媒体库
  search/       # 搜索索引
  subscription/ # 订阅
  friendlink/   # 友链
  system/       # 系统设置、统计
  schedule/     # RSS、索引、GitHub 同步、计数同步
```

## 进程内协作

- 文章发布后重建 RSS，并同步搜索索引。
- 评论审核状态变化后维护评论数和回复数。
- Redis 点赞计数由定时任务写回 MySQL，成功后再清理 dirty key。
- 后台审计通过 `AuditSupport` 记录关键管理动作。

## 部署

`docker-compose.yml` 包含 backend、frontend、mysql、redis、minio、nginx。

```bash
cd docker && docker compose up -d
cd backend/monolith && ./mvnw clean package -DskipTests
java -jar target/jiangou-1.0.0-SNAPSHOT.jar
```

Windows 本机：

```powershell
cd C:\Users\Lenovo\Documents\博客网站平台\backend\monolith
.\mvnw.cmd test
.\mvnw.cmd -DskipTests package
```

## 优缺点

| 优点 | 缺点 |
|---|---|
| 部署最简单，一个 JAR | 单点故障 |
| 事务边界清晰 | 无法单独扩容模块 |
| 调试方便，无跨服务调用 | 代码量增长后编译和回归成本上升 |
| 资源占用低 | 技术栈升级需要整体协调 |

## 演进

当搜索、通知、同步成为瓶颈时，参考 [microservices.md](./microservices.md) 渐进拆分，不建议一开始就全面微服务化。
