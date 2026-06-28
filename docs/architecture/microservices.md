# 分布式微服务架构方案

> 适用阶段：阶段 3 工程化以后，或日 UV > 10 万、SLA ≥ 99.9%。

## 架构图

```text
Browser → CDN/Nginx → API Gateway :9000
                          │
    ┌─────────┬───────────┼───────────┬─────────┐
    │         │           │           │         │
 content  comment      user      project    upload
 :8001    :8003        :8002     :8004      :8007
    │         │           │           │         │
  MySQL   MySQL       MySQL      MySQL      MinIO
    └─────────┴───────────┴───────────┴─────────┘
                          │
                    RabbitMQ 消息总线
                          │
              search / notification / sync
                          │
                    Elasticsearch
```

## 服务划分

| 服务 | 端口 | 目录 | 独立库 |
|---|---|---|---|
| gateway-service | 9000 | `backend/microservices/gateway-service/` | — |
| content-service | 8001 | `backend/microservices/content-service/` | jiangou_content |
| user-service | 8002 | `backend/microservices/user-service/` | jiangou_user |
| comment-service | 8003 | `backend/microservices/comment-service/` | jiangou_comment |
| project-service | 8004 | `backend/microservices/project-service/` | jiangou_project |
| search-service | 8005 | `backend/microservices/search-service/` | Elasticsearch |
| notification-service | 8006 | `backend/microservices/notification-service/` | — |
| upload-service | 8007 | `backend/microservices/upload-service/` | MinIO |
| sync-service | 8008 | `backend/microservices/sync-service/` | jiangou_project |

## 消息驱动

| Exchange | Routing Key | 消费方 |
|---|---|---|
| article.events | article.published | search.index, notification.rss |
| article.events | article.updated | search.index, cache.evict |
| comment.events | comment.created | notification.comment, counter.incr |
| sync.events | sync.github | sync.worker |

失败策略：重试 3 次 → DLQ → Sentry 告警；消息体含 `idempotencyKey`。

## 数据隔离

- 禁止跨服务直连数据库，仅 REST 或 MQ。
- 跨服务引用用 ID 冗余 + 缓存。
- 分布式事务用 Saga：本地事务 + 事件 + 补偿。

## 增量技术栈

| 组件 | 技术 |
|---|---|
| 注册 / 配置 | Nacos 2.x |
| 网关 | Spring Cloud Gateway |
| 服务调用 | OpenFeign + Resilience4j |
| 消息 | RabbitMQ 3.x |
| 搜索 | Elasticsearch 8.x |
| 链路追踪 | SkyWalking 9.x |
| 编排 | Docker Compose / K8s |

## 演进路径

1. **阶段 0~2**：单体 + 进程内事件（`backend/monolith`）
2. **阶段 3**：抽取 search-service，引入 RabbitMQ
3. **阶段 4**：抽取 notification、sync，加 API Gateway
4. **阶段 5**：全量微服务 + Nacos + K8s

## 优缺点

| 优点 | 缺点 |
|---|---|
| 故障隔离、独立扩容 | 运维复杂度高 |
| 团队并行开发 | 本地需启多个服务 |
| 技术栈可按服务选型 | 跨服务事务复杂 |
| 高可用 | 初期资源成本高（≥3 节点） |
