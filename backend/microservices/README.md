# 渐构后端 — 分布式微服务（Microservices）

> **⚠️ 非运行时代码**：本目录仅为阶段 3+ 演进骨架，**当前生产与开发均使用 [`backend/monolith/`](../monolith/README.md)**。请勿在此目录实现业务功能，除非明确启动微服务迁移项目。

阶段 3+ 架构：**Spring Cloud Gateway + 多服务 + RabbitMQ + Elasticsearch**。

> 初期请使用 [monolith](../monolith/README.md)，本目录为演进目标结构。

## 服务清单

| 服务 | 端口 | 目录 | 说明 |
|---|---|---|---|
| gateway-service | 9000 | `gateway-service/` | 路由、JWT、全局限流 |
| content-service | 8001 | `content-service/` | 文章、片段、碎碎念、分类、标签 |
| user-service | 8002 | `user-service/` | 用户、角色、权限、审计 |
| comment-service | 8003 | `comment-service/` | 评论、点赞、友链 |
| project-service | 8004 | `project-service/` | GitHub 项目元数据 |
| search-service | 8005 | `search-service/` | Elasticsearch 全文搜索 |
| notification-service | 8006 | `notification-service/` | 邮件、RSS、Newsletter |
| upload-service | 8007 | `upload-service/` | 文件上传、媒体库 |
| sync-service | 8008 | `sync-service/` | GitHub API 定时同步 |

## 目录结构

```text
backend/microservices/
├── pom.xml                    # 父 POM（可选）
├── common-api/                # 共享 DTO、常量、Feign 接口
├── gateway-service/
├── content-service/
├── user-service/
├── comment-service/
├── project-service/
├── search-service/
├── notification-service/
├── upload-service/
└── sync-service/
```

每个服务标准结构：

```text
{service}/
├── pom.xml
├── src/main/java/com/jiangou/{service}/
│   ├── *Application.java
│   ├── controller/
│   ├── service/
│   ├── mapper/
│   └── ...
└── src/main/resources/
    ├── application.yml
    └── db/migration/
```

## 基础设施

| 组件 | 用途 |
|---|---|
| Nacos | 服务注册与配置中心 |
| RabbitMQ | article.events / comment.events / sync.events |
| Elasticsearch | search-service 索引 |
| Redis Cluster | 缓存、限流、分布式锁 |
| SkyWalking | 链路追踪 |

## 消息约定

| Exchange | Routing Key | 消费者 |
|---|---|---|
| article.events | article.published | search, notification |
| article.events | article.updated | search, cache |
| comment.events | comment.created | notification, counter |
| sync.events | sync.github | sync-service |

详见 [docs/architecture/microservices.md](../../docs/architecture/microservices.md)。

## 本地开发（规划）

```bash
# 1. 启动基础设施
cd ../../docker && docker compose -f docker-compose.microservices.yml up -d

# 2. 按依赖顺序启动服务
cd backend/microservices
./mvnw -pl gateway-service spring-boot:run
./mvnw -pl content-service spring-boot:run
# ...
```

对外统一入口：`http://localhost:9000/api/v1`

## 数据隔离

- 各服务独立 MySQL 库（如 `jiangou_content`、`jiangou_user`）
- 禁止跨库 JOIN；跨服务数据通过 Feign + 缓存获取
- 分布式事务：Saga + 补偿

## API 文档

网关聚合 Swagger 或各服务独立 `/swagger-ui.html`。  
接口契约与单体版一致，见 [docs/API.md](../../docs/API.md)。

## 从单体迁移

1. 复制 `monolith` 对应模块到目标 service
2. 抽取 `common-api` 共享模型
3. 同步调用改 Feign；异步改 RabbitMQ
4. 搜索模块接 Elasticsearch 替换 MySQL FTS
