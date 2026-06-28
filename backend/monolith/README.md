# 渐构后端 — 单体架构（Monolith）

默认后端实现：**Java 1.8 + Spring Boot 2 + MyBatis-Plus + MySQL + Redis + Flyway**。

单一可执行 JAR，适合 MVP 至阶段 2，详见 [docs/architecture/monolith.md](../../docs/architecture/monolith.md)。

## 技术栈

| 类别 | 选型 |
|---|---|
| 运行时 | Java 1.8 |
| 框架 | Spring Boot 2.x |
| ORM | MyBatis-Plus |
| 迁移 | Flyway |
| 安全 | Spring Security + JWT |
| 文档 | springdoc-openapi |
| 缓存 | Redis |
| 存储 | MinIO SDK |

## 目录结构

```text
backend/monolith/
├── pom.xml
├── src/main/java/com/jiangou/
│   ├── JiangouApplication.java
│   ├── common/
│   │   ├── result/ApiResult.java
│   │   ├── result/PageResult.java
│   │   └── exception/
│   ├── config/
│   ├── security/
│   ├── auth/
│   ├── user/
│   ├── article/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── mapper/
│   │   ├── entity/
│   │   ├── dto/
│   │   └── vo/
│   ├── category/
│   ├── tag/
│   ├── snippet/
│   ├── note/
│   ├── comment/
│   ├── project/
│   ├── upload/
│   ├── search/
│   ├── subscription/
│   ├── friendlink/
│   ├── system/
│   ├── schedule/
│   ├── event/
│   └── counter/
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   └── db/migration/          # Flyway SQL
└── src/test/
```

## 分层规范

```text
Controller  → HTTP 入参、调用 Service（admin 路由由 SecurityConfig hasRole 保护）
Service     → 业务逻辑、@Transactional
Mapper      → MyBatis-Plus + XML
Entity      → 表映射
DTO         → 请求体
VO          → 响应体
```

## 环境变量

```env
SPRING_PROFILES_ACTIVE=dev
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=jiangou
MYSQL_USER=jiangou
MYSQL_PASSWORD=jiangou
REDIS_HOST=localhost
REDIS_PORT=6379
MINIO_ENDPOINT=http://localhost:9000
JWT_SECRET=change-me-in-production
GITHUB_OAUTH_CLIENT_ID=
GITHUB_OAUTH_CLIENT_SECRET=
GITHUB_OAUTH_ALLOWED_USERNAMES=
ADMIN_URL=http://localhost:5173/admin
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
```

## 生产部署

使用 `application-prod.yml`（`SPRING_PROFILES_ACTIVE=prod`）：

```env
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=<至少 32 字符随机字符串，必填>
JWT_COOKIE_SECURE=true
MYSQL_HOST=
MYSQL_PORT=3306
MYSQL_DATABASE=jiangou
MYSQL_USER=
MYSQL_PASSWORD=
MYSQL_USE_SSL=true
REDIS_HOST=
REDIS_PORT=6379
REDIS_PASSWORD=
CORS_ALLOWED_ORIGINS=https://your-domain.com,https://admin.your-domain.com
ADMIN_URL=https://admin.your-domain.com/admin
GITHUB_OAUTH_CLIENT_ID=
GITHUB_OAUTH_CLIENT_SECRET=
GITHUB_OAUTH_ALLOWED_USERNAMES=your_github_username
MINIO_ENDPOINT=
MINIO_ACCESS_KEY=
MINIO_SECRET_KEY=
MAIL_HOST=
MAIL_PORT=587
MAIL_USER=
MAIL_PASSWORD=
```

生产环境特性：
- Swagger 关闭
- 启动时校验 `JWT_SECRET` 非默认值
- MyBatis SQL 日志关闭
- 登出/刷新时 refresh token 加入 Redis 黑名单

## 开发

```bash
# 先启动 docker 依赖
cd ../../docker && docker compose up -d

# 启动后端
cd ../backend/monolith
./mvnw spring-boot:run

# 打包
./mvnw clean package -DskipTests
java -jar target/jiangou-*.jar
```

- API Base: `http://localhost:8080/api/v1`
- Swagger: `http://localhost:8080/swagger-ui.html`

## 统一返回

```java
ApiResult.ok(data)
ApiResult.fail(code, message)
PageResult<T>  // items, total, page, pageSize, hasMore
```

## 模块开发顺序（推荐）

1. common（ApiResult、异常、分页）
2. auth + user + security
3. article + category + tag
4. comment
5. search + snippet + note
6. project + upload + friendlink
7. schedule（RSS、GitHub 同步）

## API 文档

- 接口清单：[docs/API.md](../../docs/API.md)
- 运行时 OpenAPI：`/v3/api-docs`

## 演进至微服务

当搜索、通知、同步成为瓶颈时，按 [backend/microservices/README.md](../microservices/README.md) 渐进拆分，单体代码可作为各服务的初始模板。
