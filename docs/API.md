# 渐构 REST API 接口文档

> Base URL: `/api/v1`  
> Admin Base URL: `/api/v1/admin`  
> Version: 1.0 | Content-Type: `application/json`

## 1. 通用约定

### 1.1 统一响应

成功：

```json
{
  "code": 0,
  "message": "ok",
  "data": {}
}
```

失败：

```json
{
  "code": 40001,
  "message": "错误描述",
  "data": null
}
```

### 1.2 分页响应

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "items": [],
    "total": 100,
    "page": 1,
    "pageSize": 20,
    "hasMore": true
  }
}
```

### 1.3 HTTP 方法

| 方法 | 用途 |
|---|---|
| GET | 查询 |
| POST | 创建 / 动作 |
| PATCH | 局部更新 |
| DELETE | 软删除 |

### 1.4 认证

- 后台接口：`Authorization: Bearer <access_token>` 或 HttpOnly Cookie
- 公开读接口：无需认证
- **互动写操作（点赞、评论、友链申请等）：需登录**（`ROLE_USER` 或 `ROLE_ADMIN`）
- 详见 [USER_AUTH_DESIGN.md](./USER_AUTH_DESIGN.md)

### 1.5 常见错误码

| code | 说明 |
|---|---|
| 0 | 成功 |
| 40101 | 未登录 |
| 40301 | 无权限 |
| 40401 | 资源不存在 |
| 42901 | 限流 |
| 42201 | 参数校验失败 |

---

## 2. 公开 API

### 2.1 文章

#### GET `/articles`

文章列表（分页）。

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| page | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页条数，默认 20 |
| category | string | 否 | 分类 slug |
| tag | string | 否 | 标签 slug |
| keyword | string | 否 | 关键词 |

#### GET `/articles/slug/{slug}`

文章详情。

**响应示例：**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": 1,
    "title": "理解 Linux 进程模型",
    "slug": "linux-process-model",
    "summary": "从 fork/exec 到调度器。",
    "contentHtml": "<article>...</article>",
    "tags": ["Linux", "OS"],
    "category": { "name": "操作系统", "slug": "os" },
    "readingMinutes": 18,
    "wordCount": 5200,
    "viewCount": 1024,
    "publishedAt": "2026-01-15T08:00:00Z",
    "github": {
      "repo": "owner/repo",
      "commitSha": "abc123"
    }
  }
}
```

#### GET `/articles/{id}/related`

相关文章推荐。

#### GET `/articles/{id}/toc`

文章目录（TOC）。

#### POST `/articles/{id}/like`

文章点赞。

| 参数 | 类型 | 说明 |
|---|---|---|
| fingerprint | string | 匿名用户指纹（未登录时） |

#### GET `/articles/archives`

文章归档时间线（按年月分组）。

---

### 2.2 分类与标签

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/categories` | 分类列表 |
| GET | `/categories/{slug}` | 分类详情及文章 |
| GET | `/tags` | 标签列表 |
| GET | `/tags/{slug}` | 标签详情及文章 |

---

### 2.3 代码片段

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/snippets` | 代码片段列表 |
| GET | `/snippets/{slug}` | 片段详情 |
| GET | `/snippets/{slug}/raw` | Raw 代码（`text/plain`） |
| POST | `/snippets/{id}/copy` | 记录复制次数 |
| POST | `/snippets/{id}/like` | 点赞 |

---

### 2.4 碎碎念

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/notes` | 动态列表（时光机） |
| GET | `/notes/{id}` | 动态详情 |
| POST | `/notes/{id}/like` | 点赞 |

---

### 2.5 项目

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/projects` | GitHub 项目列表 |
| GET | `/projects/{owner}/{repo}` | 项目详情 |

---

### 2.6 评论

#### GET `/comments`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| targetType | string | 是 | article / note / snippet / guestbook |
| targetId | long | 是 | 目标 ID |

#### POST `/comments`

提交评论（最多两层嵌套）。

**请求体：**

```json
{
  "targetType": "article",
  "targetId": 1,
  "parentId": null,
  "nickname": "匿名开发者",
  "email": "dev@example.com",
  "website": "https://example.com",
  "contentMd": "写得很好。"
}
```

#### POST `/comments/{id}/like`

评论点赞。

---

### 2.7 搜索

#### GET `/search`

| 参数 | 类型 | 说明 |
|---|---|---|
| q | string | 搜索词 |
| type | string | article / snippet / note / project / all |
| page | int | 页码 |
| pageSize | int | 每页条数 |

**响应示例：**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "items": [
      {
        "type": "article",
        "title": "Redis 缓存设计",
        "url": "/posts/redis-cache-design",
        "snippet": "本文讨论 Redis 缓存穿透、击穿和雪崩...",
        "score": 0.98
      }
    ],
    "total": 1,
    "page": 1,
    "pageSize": 20,
    "hasMore": false
  }
}
```

#### GET `/search/suggest`

搜索建议（自动补全）。

| 参数 | 类型 | 说明 |
|---|---|---|
| q | string | 前缀 |

---

### 2.8 订阅

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/subscriptions` | 提交邮箱订阅 |
| POST | `/subscriptions/confirm` | 确认订阅（body: `{ "token": "..." }`） |
| POST | `/subscriptions/unsubscribe` | 退订（body: `{ "token": "..." }`） |

---

### 2.9 友链（公开）

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/friend-links` | 已审核友链列表 |
| POST | `/friend-links/apply` | 友链申请 |

---

### 2.10 系统

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/settings/public` | 前台公开配置 |
| GET | `/stats` | 站点统计（文章数、运行天数等） |
| GET | `/rss/feed.xml` | RSS 订阅源 |

---

## 3. 认证 API

| 方法 | 路径 | 说明 | 认证 |
|---|---|---|---|
| POST | `/auth/login` | 后台用户名密码登录 | 否 |
| POST | `/auth/logout` | 登出（拉黑 access/refresh token） | 否 |
| POST | `/auth/refresh` | 刷新 access token（旧 refresh 失效） | 否 |
| POST | `/auth/oauth/exchange` | GitHub OAuth 一次性 code 换 token | 否 |
| GET | `/auth/github` | GitHub OAuth 跳转 | 否 |
| GET | `/auth/github/callback` | GitHub OAuth 回调 | 否 |
| GET | `/auth/github/enabled` | OAuth 是否已配置 | 否 |
| GET | `/auth/me` | 当前登录用户 | 是 |

### POST `/auth/login`

**请求：**

```json
{
  "username": "admin",
  "password": "******"
}
```

**响应：**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "accessToken": "eyJ...",
    "refreshToken": "eyJ...",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "username": "admin",
      "displayName": "管理员",
      "roles": ["ADMIN"]
    }
  }
}
```

### POST `/auth/logout`

可选请求体（建议携带 refresh token 一并失效）：

```json
{
  "refreshToken": "eyJ..."
}
```

Header：`Authorization: Bearer {accessToken}`（可选）

### POST `/auth/oauth/exchange`

GitHub OAuth 回调后，前台用一次性 `code` 换取 token（code 有效期 2 分钟）：

```json
{
  "code": "abc123..."
}
```

响应同 `/auth/login`。

---

## 4. 后台 API（需鉴权）

> 路径前缀：`/api/v1/admin`  
> 权限示例：`article:create`、`article:publish`、`comment:review`

### 4.1 文章管理

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/articles` | 管理列表（含草稿） |
| POST | `/admin/articles` | 创建文章 |
| GET | `/admin/articles/{id}` | 详情 |
| PATCH | `/admin/articles/{id}` | 更新 |
| DELETE | `/admin/articles/{id}` | 软删除 |
| POST | `/admin/articles/{id}/publish` | 发布 |
| POST | `/admin/articles/{id}/unpublish` | 下线 |
| POST | `/admin/articles/{id}/archive` | 归档 |
| GET | `/admin/articles/{id}/versions` | 版本历史 |
| POST | `/admin/articles/{id}/restore/{version}` | 恢复指定版本 |

**创建/更新请求体（PATCH 部分字段）：**

```json
{
  "title": "文章标题",
  "slug": "article-slug",
  "summary": "摘要",
  "contentMd": "# Markdown 正文",
  "categoryId": 1,
  "tagIds": [1, 2],
  "visibility": "public",
  "pinned": false,
  "githubRepo": "owner/repo",
  "githubCommitSha": "abc123"
}
```

### 4.2 分类与标签

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/categories` | 分类列表 |
| POST | `/admin/categories` | 创建 |
| PATCH | `/admin/categories/{id}` | 更新 |
| DELETE | `/admin/categories/{id}` | 删除 |
| GET | `/admin/tags` | 标签列表 |
| POST | `/admin/tags` | 创建 |
| PATCH | `/admin/tags/{id}` | 更新 |
| DELETE | `/admin/tags/{id}` | 删除 |

### 4.3 代码片段

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/snippets` | 列表 |
| POST | `/admin/snippets` | 创建 |
| PATCH | `/admin/snippets/{id}` | 更新 |
| DELETE | `/admin/snippets/{id}` | 删除 |

### 4.4 碎碎念

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/notes` | 列表 |
| POST | `/admin/notes` | 创建 |
| PATCH | `/admin/notes/{id}` | 更新 |
| DELETE | `/admin/notes/{id}` | 删除 |
| POST | `/admin/notes/{id}/publish` | 发布 |

### 4.5 评论审核

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/comments` | 列表（可按 status 筛选） |
| POST | `/admin/comments/{id}/approve` | 通过 |
| POST | `/admin/comments/{id}/reject` | 拒绝 |
| POST | `/admin/comments/{id}/spam` | 标记垃圾 |
| DELETE | `/admin/comments/{id}` | 删除 |

### 4.6 项目管理

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/projects` | 列表 |
| POST | `/admin/projects` | 添加 GitHub 仓库 |
| PATCH | `/admin/projects/{id}` | 更新 |
| DELETE | `/admin/projects/{id}` | 删除 |
| POST | `/admin/projects/{id}/sync` | 同步单个 |
| POST | `/admin/projects/sync` | 同步全部 |

### 4.7 媒体库

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/attachments` | 列表 |
| POST | `/admin/attachments` | 上传（multipart/form-data） |
| DELETE | `/admin/attachments/{id}` | 删除 |

### 4.8 友链管理

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/friend-links` | 列表 |
| POST | `/admin/friend-links` | 创建 |
| PATCH | `/admin/friend-links/{id}` | 更新 |
| POST | `/admin/friend-links/{id}/approve` | 审核通过 |
| POST | `/admin/friend-links/{id}/reject` | 拒绝 |
| DELETE | `/admin/friend-links/{id}` | 删除 |

### 4.10 用户管理

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/users` | 用户列表（分页） |
| GET | `/admin/users/{id}` | 用户详情 |
| PATCH | `/admin/users/{id}` | 更新用户（含角色） |

### 4.11 邮件订阅

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/subscriptions` | 订阅列表 |
| POST | `/admin/subscriptions/newsletter` | Newsletter 群发 |

### 4.13 Webmention

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/webmention` | 接收 Webmention（form: source, target） |
| GET | `/admin/webmentions` | 管理列表 |

### 4.14 文章版本

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/articles/{id}/versions/diff?from=&to=` | 版本 Markdown Diff（to 省略则为当前版本） |

### 4.12 系统

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/settings` | 全部设置 |
| PATCH | `/admin/settings/{key}` | 更新单项 |
| GET | `/admin/audit-logs` | 审计日志 |
| POST | `/admin/rss/rebuild` | 重建 RSS |
| POST | `/admin/search/rebuild` | 重建搜索索引 |
| GET | `/admin/stats` | 后台仪表盘统计 |

---

## 4. 前台用户认证 API

> 状态：**已实现** | 设计详见 [USER_AUTH_DESIGN.md](./USER_AUTH_DESIGN.md)

### 4.1 验证码

#### GET `/auth/captcha`

获取图形验证码。

**响应：**

```json
{
  "code": 0,
  "data": {
    "captchaId": "550e8400-e29b-41d4-a716-446655440000",
    "imageBase64": "data:image/png;base64,..."
  }
}
```

### 4.2 注册

#### POST `/auth/register/send-code`

发送注册邮箱验证码（需先通过图形验证码）。

**请求：**

```json
{
  "email": "user@example.com",
  "captchaId": "550e8400-...",
  "captchaCode": "a3b7"
}
```

#### POST `/auth/register`

完成注册（邮箱验证码 + 账户信息）。

**请求：**

```json
{
  "username": "devuser",
  "email": "user@example.com",
  "password": "SecurePass123!",
  "emailCode": "123456"
}
```

**响应：** 与 `POST /auth/login` 相同（accessToken + refreshToken + user）。

### 4.3 忘记密码

#### POST `/auth/forgot-password/send-code`

发送重置密码邮箱验证码。

**请求：**

```json
{
  "email": "user@example.com",
  "captchaId": "550e8400-...",
  "captchaCode": "a3b7"
}
```

#### POST `/auth/reset-password`

**请求：**

```json
{
  "email": "user@example.com",
  "emailCode": "654321",
  "newPassword": "NewSecurePass456!"
}
```

### 4.4 权限变更（互动接口）

以下接口将从「公开 + fingerprint」改为 **需登录（`ROLE_USER` 或 `ROLE_ADMIN`）**：

| 方法 | 路径 |
|---|---|
| POST | `/articles/{id}/like` |
| POST | `/comments` |
| POST | `/comments/{id}/like` |
| POST | `/notes/{id}/like` |
| POST | `/snippets/{id}/like` |
| POST | `/friend-links/apply` |

未登录调用返回 `40101`。

### 4.5 扩展错误码

| code | 说明 |
|---|---|
| 40102 | 验证码错误或已过期 |
| 40103 | 图形验证码错误 |
| 40104 | 邮箱验证码发送过于频繁 |
| 40901 | 用户名或邮箱已存在 |
| 42202 | 密码强度不足 |

---

## 5. OpenAPI

后端启动后访问 Swagger UI：

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON：

```text
http://localhost:8080/v3/api-docs
```

---

## 6. 相关文档

- [PRODUCTION_READINESS.md](./PRODUCTION_READINESS.md) — 上线审查与验收标准
- [USER_AUTH_DESIGN.md](./USER_AUTH_DESIGN.md) — 用户认证详细设计
- [06-DATABASE](../JianGou_FULL_DOCS_v3_MySQL.md) — 表结构与字段
- [08-BUSINESS](../JianGou_FULL_DOCS_v3_MySQL.md) — 状态机与业务规则
- [09-SECURITY](../JianGou_FULL_DOCS_v3_MySQL.md) — 安全与 XSS
