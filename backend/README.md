# 渐构后端

后端提供 REST API，对接 `frontend` 与 `admin`。

## 架构目录

| 目录 | 说明 | 适用阶段 |
|---|---|---|
| [monolith/](./monolith/README.md) | 单体 Spring Boot JAR | MVP ~ 阶段 2（**默认**） |
| [microservices/](./microservices/README.md) | 分布式微服务 | 阶段 3+ |

## 技术约束

- Java 1.8 + Spring Boot 2
- MyBatis-Plus + Flyway + MySQL
- 统一 `ApiResult`、OpenAPI、JWT 鉴权

接口文档：[docs/API.md](../docs/API.md)
