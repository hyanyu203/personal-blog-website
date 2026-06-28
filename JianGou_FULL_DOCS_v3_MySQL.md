# 渐构（JianGou）完整工程文档合并版

前端：Vue 3 + Nuxt 4；后端：Java 1.8 + Spring Boot 2；数据库：MySQL。

## docs/00-README.md

project: 渐构 JianGou

version: 1.0

frontend: Vue 3 + Nuxt 4 + TypeScript

backend: Java 1.8 + Spring Boot 2

database: MySQL

purpose: 给 Codex / AI Coding Agent 使用的完整工程蓝图

## 渐构（JianGou）完整工程文档包

本文档包用于指导 Codex、Claude Code、Gemini CLI 等 AI Coding Agent 按模块搭建「渐构」个人技术博客 / 技术知识沉淀平台。

### 文档清单

| 文件 | 用途 |
|---|---|
| 01-PROJECT.md | 项目定位、参考站分析、产品边界 |
| 02-ARCHITECTURE.md | 总体架构、模块交互、部署拓扑 |
| 03-TECH_STACK.md | Vue/Nuxt + Java/Spring Boot 技术选型 |
| 04-FRONTEND.md | 前端页面、组件树、视觉规范 |
| 05-BACKEND.md | 后端模块、包结构、分层规范 |
| 06-DATABASE.md | MySQL 完整表设计 |
| 07-API.md | REST API 设计 |
| 08-BUSINESS.md | 关键业务逻辑 |
| 09-SECURITY.md | 权限、安全、风控 |
| 10-SEARCH.md | 搜索方案 |
| 11-DEPLOY.md | Docker、Nginx、部署 |
| 12-ADMIN.md | 后台 CMS |
| 13-CODE_STYLE.md | 编码规范 |
| 14-CODEX_RULES.md | Codex 开发提示词与任务拆分 |
| 15-ROADMAP.md | MVP → V1 → V2 路线 |
| sql/init.sql | MySQL 初始化建表示例 |
| sql/indexes.sql | 索引示例 |
| docker/docker-compose.yml | 本地开发依赖环境 |

### 重要约束

1. 前端优先使用 Vue 技术栈。

2. 后端使用 Java 技术栈。

3. 所有业务数据以 MySQL 为主库。

4. 所有接口统一 RESTful 风格。

5. 所有表必须有审计字段和软删除字段。

6. 文章、代码、评论内容必须考虑 XSS 防护。

7. 项目应能渐进式开发，不要求一次完成全部功能。

### 给 Codex 的第一条指令建议

```text
请阅读 docs/00-README.md 到 docs/15-ROADMAP.md，严格按照文档约束实现项目。
先生成 monorepo 项目骨架，包含 frontend、admin、backend、docs、sql、docker 目录。
不要跳过数据库迁移、统一返回结构、异常处理、OpenAPI 注释和 Docker Compose。
```

## docs/01-PROJECT.md

project: 渐构 JianGou

version: 1.0

frontend: Vue 3 + Nuxt 4 + TypeScript

backend: Java 1.8 + Spring Boot 2

database: MySQL

purpose: 给 Codex / AI Coding Agent 使用的完整工程蓝图

## 01. 项目定位与参考站分析

### 1.1 项目名称

**渐构（JianGou）**

含义：渐次构建，理解计算机世界。

### 1.2 核心定位

渐构不是传统日记型博客，而是面向开发者、计算机学生和技术爱好者的个人技术知识沉淀平台。

它融合：

- Blog：长文技术文章
- Wiki：体系化知识页
- Gist：短代码片段
- Project Showcase：开源项目陈列
- Digital Garden：碎碎念、开发日志、思考记录

### 1.3 内容范围

| 模块 | 内容 |
|---|---|
| 技术文章 | 计算机基础、编程语言、架构、算法、系统、工程实践 |
| 代码片段 | 短小代码、配置、命令、脚本 |
| 碎碎念 | 技术随想、踩坑记录、开发日志 |
| 项目陈列 | GitHub 开源项目、Star、语言、描述、README 摘要 |
| 专题 | Linux、Java、Vue、数据库、网络、算法等知识专题 |
| 页面 | 关于、友链、留言、订阅、隐私政策 |

### 1.4 目标用户

1. 开发者：查阅文章、代码片段、项目笔记。

2. 计算机学生：学习基础知识和工程实践。

3. 技术爱好者：浏览碎碎念、友链、项目动态。

4. 博主本人：长期沉淀知识、管理项目、复盘经验。

### 1.5 用户体验关键词

- 沉浸阅读
- 代码优先
- 检索便捷
- 低打扰互动
- 极简界面
- 东亚审美
- 可长期维护

### 1.6 参考站点分析

#### 1.6.1 blog.canmoe.com 可借鉴点

根据公开页面可见信息，该站包含主页、归档、开往、GitHub、站点监控、友链、留言、关于、RSS、Sitemap、主题切换、公告、分类、标签、站点统计、文章字数、阅读时间、目录、评论区等元素。

渐构应借鉴：

- 主页清晰导航
- 亮色 / 暗色 / 跟随系统主题
- 站点公告
- 分类与标签聚合
- 文章字数、阅读时间、最后更新
- 文章目录
- RSS / Sitemap
- 友链与留言
- 站点统计

但渐构应强化：

- 技术文章结构化
- 代码块体验
- GitHub commit 关联
- 项目元数据同步
- 全文搜索
- 后台 CMS

#### 1.6.2 imsuk.cn 可借鉴点

根据公开搜索结果可见，该站包含时光机、友链申请、分类、归档、留言板、隐私政策、教程、服务搭建、Web 随笔、站点信息等内容。

渐构应借鉴：

- 时光机 / 碎碎念页面
- 友链申请入口
- 归档页面
- 分类导航
- 留言板
- 站点信息展示
- 技术与生活内容的轻量混合

但渐构的内容边界应更偏技术，生活内容只放在碎碎念，不进入主文章流。

### 1.7 产品原则

#### 内容优先

页面设计服务于阅读，不让动画、广告、社交按钮打扰内容。

#### 代码优先

代码块必须具备：

- 语法高亮
- 复制按钮
- 文件名展示
- 行号
- Diff 高亮
- Raw 查看
- 超长折叠

#### 搜索优先

随着文章数量增加，搜索比分页更重要。初期使用 MySQL FULLTEXT，后期接入 Meilisearch 或 Elasticsearch。

#### 可维护优先

优先选择成熟技术：

- Vue 3
- Nuxt
- Java 1.8
- Spring Boot 2
- MySQL
- Redis
- MinIO

### 1.8 MVP 功能边界

MVP 必须实现：

- 文章列表
- 文章详情
- Markdown 渲染
- 标签
- 分类
- 登录
- 评论
- 后台文章管理
- RSS
- 基础搜索

MVP 暂不强制实现：

- Newsletter
- Elasticsearch
- 高级数据统计
- 多主题市场
- Webmention
- AI 摘要

## docs/02-ARCHITECTURE.md

project: 渐构 JianGou

version: 1.0

frontend: Vue 3 + Nuxt 4 + TypeScript

backend: Java 1.8 + Spring Boot 2

database: MySQL

purpose: 给 Codex / AI Coding Agent 使用的完整工程蓝图

## 02. 总体架构设计

### 2.1 架构目标

渐构采用前后端分离 + SSR/SSG + REST API + MySQL 主库的架构。

目标：

- 前台阅读体验快
- 后台管理能力完整
- 后端 Java 生态稳定
- 数据库结构可长期演进
- 搜索、评论、同步任务可扩展
- 能被 Codex 分模块实现

### 2.2 总体架构图

```text
┌────────────────────────────┐
│          Browser            │
└──────────────┬─────────────┘
               │ HTTPS
┌──────────────▼─────────────┐
│        CDN / Nginx          │
│ TLS / Gzip / Cache / Proxy  │
└──────────────┬─────────────┘
               │
     ┌─────────┴──────────┐
     │                    │
┌────▼─────┐        ┌─────▼─────┐
│ Nuxt Web │        │ Vue Admin │
│ SSR/SSG  │        │ Vite SPA  │
└────┬─────┘        └─────┬─────┘
     │ REST / JSON        │ REST / JSON
┌────▼────────────────────▼────┐
│        Spring Boot 2 API       │
│ Security / REST / OpenAPI      │
└────┬─────────────────────┬────┘
     │                     │
┌────▼─────┐         ┌─────▼────┐
│MySQL│         │  Redis   │
│ Main DB  │         │Cache/Job │
└────┬─────┘         └─────┬────┘
     │                     │
┌────▼─────┐      ┌────────▼─────────┐
│ MinIO/OSS│      │ External Services │
│ Media    │      │ GitHub/Mail/Spam  │
└──────────┘      └──────────────────┘
```

### 2.3 应用划分

| 应用 | 技术 | 说明 |
|---|---|---|
| frontend | Nuxt 4 + Vue 3 | 面向访客的前台站点 |
| admin | Vue 3 + Vite | 后台 CMS |
| backend | Java 1.8 + Spring Boot 2 | REST API |
| postgres | MySQL 8.0+ | 主数据库 |
| redis | Redis 7+ | 缓存、限流、会话、队列 |
| minio | MinIO | 本地对象存储 |
| nginx | Nginx | 网关、反代、静态缓存 |

### 2.4 后端模块边界

```text
backend/src/main/java/com/jiangou/
  common/
  config/
  security/
  auth/
  user/
  article/
  category/
  tag/
  snippet/
  note/
  comment/
  project/
  upload/
  search/
  subscription/
  friendlink/
  system/
  audit/
  schedule/
```

### 2.5 请求链路

#### 文章详情

```text
GET /posts/linux-process
→ Nuxt SSR
→ GET /api/v1/articles/slug/linux-process
→ Redis cache
→ MySQL
→ 返回 ArticleDetailVO
→ Nuxt 渲染页面
```

#### 评论提交

```text
POST /api/v1/comments
→ Rate Limit
→ 参数校验
→ 登录态或匿名身份识别
→ Markdown 安全渲染
→ Akismet / 自研规则
→ MySQL 写入 pending / approved
→ 清理 Redis 评论缓存
```

#### GitHub 项目同步

```text
Quartz 定时任务
→ 读取 projects
→ 调用 GitHub API
→ 更新 stars/language/description/topics
→ 写入 github_sync_logs
→ 失败时保留旧数据并标记 stale
```

### 2.6 缓存策略

| 数据 | 缓存位置 | TTL |
|---|---|---|
| 首页文章列表 | Redis + CDN | 5 分钟 |
| 文章详情 | Redis + CDN | 10 分钟 |
| 标签 / 分类 | Redis | 30 分钟 |
| 评论列表 | Redis | 1 分钟 |
| GitHub 项目 | Redis | 6 小时 |
| RSS | 静态文件 + CDN | 5 分钟 |

### 2.7 部署拓扑

开发环境：

```text
Docker Compose:
MySQL
Redis
MinIO
Mailhog
```

生产环境：

```text
Nginx
├── frontend Nuxt server
├── admin static files
└── backend Spring Boot
Data:
├── MySQL
├── Redis
└── OSS/R2/MinIO
```

### 2.8 架构取舍

| 选项 | 取舍 |
|---|---|
| Nuxt 而不是纯 Vite | 前台需要 SEO 与 SSR |
| Spring Boot 而不是 Node | 用户明确要求 Java，且适合长期维护 |
| MySQL 而不是其他 DB | JSON、FTS、复杂索引更适合内容系统 |
| Redis 而不是本地缓存 | 支持分布式部署和限流 |
| MinIO/OSS 而不是本地磁盘 | 便于迁移、扩容和 CDN |

## docs/03-TECH_STACK.md

project: 渐构 JianGou

version: 1.0

frontend: Vue 3 + Nuxt 4 + TypeScript

backend: Java 1.8 + Spring Boot 2

database: MySQL

purpose: 给 Codex / AI Coding Agent 使用的完整工程蓝图

## 03. 技术栈选型

### 3.1 总览

| 层级 | 技术 | 说明 |
|---|---|---|
| 前台 | Nuxt 4 + Vue 3 + TypeScript | SSR/SSG、SEO、内容站 |
| 后台 | Vue 3 + Vite + TypeScript | 管理端单页应用 |
| UI | Naive UI / Element Plus | 后台效率高 |
| 样式 | UnoCSS / Tailwind CSS | 极简、主题化、响应式 |
| 状态 | Pinia | Vue 官方生态 |
| 请求 | ofetch / Axios | REST API |
| 服务端 | Java 1.8 + Spring Boot 2 | 主后端 |
| 安全 | Spring Security + JWT + OAuth2 Client | 管理员登录、GitHub 登录 |
| ORM | MyBatis-Plus | SQL 可控，适合复杂查询 |
| 迁移 | Flyway | 数据库版本管理 |
| 数据库 | MySQL 8.0+ | 主库，InnoDB 引擎，utf8mb4 字符集 |
| 缓存 | Redis 7+ | 缓存、限流、Session |
| 对象存储 | MinIO / OSS / R2 | 图片、附件、媒体 |
| 搜索 | MySQL FULLTEXT → Meilisearch / Elasticsearch | 渐进式演进 |
| 文档 | springdoc-openapi | Swagger / OpenAPI |
| 定时任务 | Spring Scheduler / Quartz | GitHub 同步、RSS |
| 日志 | Logback + JSON Encoder | 结构化日志 |
| 监控 | Actuator + Prometheus + Grafana | 可观测 |
| 错误追踪 | Sentry | 前后端异常 |
| 部署 | Docker Compose / Docker | 一键部署 |

### 3.2 前端选型

#### Nuxt 4

适合前台：

- SEO 友好
- 支持 SSR / SSG
- 自动路由
- 静态页面缓存
- 对内容型网站友好

#### Vue 3

原因：

- 用户明确希望前端尽可能使用 Vue
- Composition API 适合模块化
- 中文社区成熟
- 与后台管理系统生态良好

#### UnoCSS / Tailwind

用于构建极简视觉风格：

- 快速主题切换
- 原子化 CSS
- 易于维护一致间距
- 对深色模式友好

### 3.3 后端选型

#### Java 1.8

原因：

- 长期支持版（LTS），广泛用于生产环境
- 与 Spring Boot 2.x 兼容，生态成熟稳定
- 企业级项目主流选择，社区支持完善

#### Spring Boot 2

原因：

- 与 Spring Security、Actuator、Validation、OpenAPI、Redis 集成成熟
- 后期扩展任务、消息、监控方便
- 适合 Codex 生成标准化项目结构

#### MyBatis-Plus

原因：

- 对 SQL 控制更直接
- 复杂搜索、归档、统计查询更方便
- 学习成本较低
- 适合 MySQL JSON 和自定义 SQL

### 3.4 数据库选型

#### MySQL

原因：

- JSON 适合 metadata 扩展字段
- FULLTEXT INDEX + ngram 分词支持中文全文搜索，后期无缝切换 Meilisearch / Elasticsearch
- InnoDB 引擎支持事务、行级锁、MVCC，满足业务一致性要求
- 生态最成熟，MyBatis-Plus / Flyway 对 MySQL 支持最完善，国内云厂商一键 RDS 部署

### 3.5 搜索选型

阶段一：LIKE / ILIKE

适合文章数量极少时。

阶段二：MySQL FULLTEXT 全文搜索

适合数千篇以内文章。

阶段三：Meilisearch

适合个人站点体验升级。

阶段四：Elasticsearch / OpenSearch

适合复杂搜索、聚合、权重、日志分析。

### 3.6 Markdown 渲染策略

推荐混合方案：

1. 后台保存 Markdown 原文。

2. Java 后端提取纯文本、TOC、元数据。

3. 前台 Nuxt 使用 Markdown 渲染组件。

4. 文章发布时可预渲染安全 HTML。

5. 所有用户提交内容必须进行 sanitize。

可选库：

- markdown-it
- unified / remark / rehype
- mermaid
- KaTeX
- Shiki

### 3.7 代码高亮策略

优先使用 Shiki：

- 颜色准确
- 支持 VS Code 主题
- 对技术博客友好

实现方式：

- Nuxt 服务端渲染阶段高亮
- 或使用 Node sidecar 作为代码高亮服务
- Java 后端保存原始 Markdown 和渲染后 HTML

### 3.8 反垃圾与风控

- Akismet
- IP hash
- UA hash
- 浏览器 fingerprint hash
- Redis Rate Limit
- 评论状态审核
- 后台黑名单词库

## docs/04-FRONTEND.md

project: 渐构 JianGou

version: 1.0

frontend: Vue 3 + Nuxt 4 + TypeScript

backend: Java 1.8 + Spring Boot 2

database: MySQL

purpose: 给 Codex / AI Coding Agent 使用的完整工程蓝图

## 04. 前端设计

### 4.1 前端应用

分为两个应用：

```text
frontend/  面向访客的 Nuxt 前台
admin/     面向管理员的 Vue + Vite 后台
```

### 4.2 前台页面结构

| 页面 | 路由 | 说明 |
|---|---|---|
| 首页 | / | 站点介绍、公告、最近文章、项目、碎碎念 |
| 文章列表 | /posts | 技术文章列表 |
| 文章详情 | /posts/:slug | 沉浸式阅读 |
| 分类页 | /categories/:slug | 分类文章 |
| 标签页 | /tags/:slug | 标签文章 |
| 归档页 | /archives | 时间线归档 |
| 代码片段 | /snippets | Gist 风格短代码 |
| 代码详情 | /snippets/:slug | 高亮、raw、复制 |
| 碎碎念 | /notes | Time Machine |
| 项目 | /projects | GitHub 项目陈列 |
| 友链 | /friends | 友链与申请 |
| 留言 | /guestbook | 留言板 |
| 关于 | /about | 技术栈、经历 |
| 搜索 | /search | 全站搜索 |
| 订阅 | /subscribe | RSS / 邮件订阅 |

### 4.3 视觉风格

基调：

- 极简
- 留白
- 东亚审美
- 衬线标题
- 等宽代码
- 低饱和配色
- 亮色 / 暗色 / 跟随系统

CSS Token：

```css
:root {
  --color-bg: #faf7f0;
  --color-text: #24211d;
  --color-muted: #7a746b;
  --color-border: #e7dfd2;
  --color-accent: #7b5f3a;
  --font-serif: "Noto Serif SC", "Source Han Serif SC", serif;
  --font-sans: "Inter", "Noto Sans SC", system-ui, sans-serif;
  --font-mono: "JetBrains Mono", "Fira Code", monospace;
}
```

### 4.4 布局设计

#### 首页

```text
Header
├── Logo 渐构
├── Nav
├── Search
└── ThemeToggle
Hero
├── 站点名
├── 标语：渐次构建，理解计算机世界
└── 公告
Main
├── 最近文章
├── 精选专题
├── 代码片段
├── 最近碎碎念
└── GitHub 项目
Aside
├── 分类
├── 标签云
├── 站点统计
└── RSS
Footer
├── 版权
├── RSS / Sitemap
└── Powered by JianGou
```

#### 文章详情

```text
ArticleLayout
├── Header
├── ArticleTitle
├── ArticleMeta
├── ArticleGitHubCommit
├── ArticleContent
│   ├── Markdown
│   ├── CodeBlock
│   ├── Mermaid
│   └── KaTeX
├── ArticleCopyright
├── ArticleNav
└── Comments
Floating
├── TOC
├── BackTop
└── ReadingProgress
```

### 4.5 核心组件

```text
components/
  site/
    AppHeader.vue
    AppFooter.vue
    ThemeToggle.vue
    SearchDialog.vue
  article/
    ArticleCard.vue
    ArticleMeta.vue
    ArticleContent.vue
    ArticleTOC.vue
    ArticleCopyright.vue
    ArticleGitHubRef.vue
  code/
    CodeBlock.vue
    CodeToolbar.vue
    DiffBlock.vue
    RawCodeButton.vue
  comment/
    CommentTree.vue
    CommentItem.vue
    CommentEditor.vue
  project/
    ProjectCard.vue
    ProjectStats.vue
  note/
    NoteCard.vue
    NoteTimeline.vue
  friend/
    FriendCard.vue
    FriendApplyForm.vue
```

### 4.6 文章卡片设计

字段：

- 标题
- 摘要
- 发布时间
- 分类
- 标签
- 阅读时间
- 字数
- 浏览量
- 是否置顶

### 4.7 代码块设计

功能：

- 语言标识
- 文件名
- 复制按钮
- Raw 查看
- 行号
- Diff 高亮
- 超长折叠
- 主题跟随系统

示例属性：

```vue
<CodeBlock
  language="java"
  filename="ArticleService.java"
  :show-line-numbers="true"
  :collapsible="true"
/>
```

### 4.8 后台页面结构

| 页面 | 路由 | 说明 |
|---|---|---|
| 仪表盘 | /admin | 统计 |
| 文章管理 | /admin/articles | 文章 CRUD |
| 写文章 | /admin/articles/editor | Markdown 编辑器 |
| 代码片段 | /admin/snippets | 代码片段管理 |
| 碎碎念 | /admin/notes | 动态管理 |
| 评论审核 | /admin/comments | 审核 |
| 项目管理 | /admin/projects | GitHub 同步 |
| 媒体库 | /admin/media | 图片附件 |
| 友链审核 | /admin/friends | 审核友链 |
| 用户管理 | /admin/users | 用户 |
| 系统设置 | /admin/settings | 配置 |

### 4.9 前端状态管理

Pinia Store：

```text
stores/
  auth.ts
  theme.ts
  article.ts
  search.ts
  comment.ts
  admin.ts
```

### 4.10 前端 API Service

```text
services/
  http.ts
  article.api.ts
  snippet.api.ts
  note.api.ts
  comment.api.ts
  project.api.ts
  auth.api.ts
  admin.api.ts
```

### 4.11 SEO

每篇文章生成：

- title
- description
- canonical
- Open Graph
- Twitter Card
- JSON-LD Article
- Sitemap
- RSS item

### 4.12 性能优化

- 首页和文章页使用 SSR/SSG
- 图片懒加载
- 代码块增强功能延迟挂载
- 评论区客户端异步加载
- 搜索弹窗按需加载
- 主题状态本地缓存

## docs/05-BACKEND.md

project: 渐构 JianGou

version: 1.0

frontend: Vue 3 + Nuxt 4 + TypeScript

backend: Java 1.8 + Spring Boot 2

database: MySQL

purpose: 给 Codex / AI Coding Agent 使用的完整工程蓝图

## 05. 后端设计

### 5.1 后端目标

后端采用 Java 1.8 + Spring Boot 2，提供稳定、可扩展、标准化的 REST API。

核心目标：

- 统一返回
- 统一异常
- 统一权限
- OpenAPI 文档
- DTO/VO 分离
- 软删除
- 审计日志
- 可测试
- 可由 Codex 按模块生成

### 5.2 后端目录结构

```text
backend/src/main/java/com/jiangou/
  JiangouApplication.java
  common/
    result/
    exception/
    pagination/
    validation/
    security/
    config/
    util/
  auth/
    controller/
    service/
    dto/
    vo/
  user/
    controller/
    service/
    mapper/
    entity/
    dto/
    vo/
  article/
    controller/
    service/
    mapper/
    entity/
    dto/
    vo/
  comment/
  snippet/
  note/
  project/
  upload/
  search/
  subscription/
  friendlink/
  system/
  audit/
  schedule/
```

### 5.3 分层规范

```text
Controller
  只负责 HTTP 入参、权限注解、调用 Service
Service
  业务逻辑、事务、状态机
Mapper
  数据库访问，MyBatis-Plus BaseMapper + XML
Entity
  数据库表映射
DTO
  请求参数
VO
  响应对象
Converter
  Entity / DTO / VO 转换，可使用 MapStruct
```

### 5.4 统一返回结构

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResult<T> {
    private Integer code;
    private String message;
    private T data;
    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(0, "ok", data);
    }
    public static <T> ApiResult<T> fail(Integer code, String message) {
        return new ApiResult<>(code, message, null);
    }
}
```

分页：

```java
@Data
public class PageResult<T> {
    private List<T> items;
    private Long total;
    private Long page;
    private Long pageSize;
    private Boolean hasMore;
}
```

### 5.5 全局异常

异常类型：

- BusinessException
- NotFoundException
- UnauthorizedException
- ForbiddenException
- RateLimitException
- ValidationException

统一处理：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ApiResult<Void> handleBusiness(BusinessException e) {
        return ApiResult.fail(e.getCode(), e.getMessage());
    }
}
```

### 5.6 权限模型

角色：

- SUPER_ADMIN
- ADMIN
- EDITOR
- USER
- GUEST

权限示例：

- article:create
- article:update
- article:publish
- comment:review
- project:sync
- setting:update

### 5.7 认证方式

后台：

- 用户名密码登录
- JWT Access Token
- Refresh Token
- Spring Security

评论：

- GitHub OAuth 登录
- 匿名评论
- 可选邮箱 Hash 头像

### 5.8 关键服务

#### ArticleService

职责：

- 创建文章
- 更新草稿
- 发布文章
- 生成版本
- 渲染 Markdown
- 更新索引
- 刷新 RSS
- 清理缓存

#### CommentService

职责：

- 提交评论
- 两层嵌套校验
- 反垃圾
- 审核
- 查询评论树
- 点赞

#### ProjectSyncService

职责：

- 同步 GitHub 仓库
- 处理限流
- 记录日志
- 降级到旧数据

#### SearchService

职责：

- 聚合搜索文章、代码片段、碎碎念
- 生成摘要
- 后期适配 Elasticsearch

### 5.9 事务边界

文章发布：

```text
事务内：
更新 articles
插入 article_versions
更新 article_tags
写 audit_logs
事务外：
RSS 生成
搜索索引
CDN 刷新
```

评论提交：

```text
事务内：
插入 comments
更新目标 comment_count
事务外：
邮件通知
缓存清理
```

### 5.10 定时任务

| 任务 | 周期 |
|---|---|
| GitHub 项目同步 | 每天 02:00 |
| RSS 重新生成 | 每小时 |
| 搜索索引重建 | 每天 03:00 |
| 清理过期 token | 每天 |
| 附件孤儿文件扫描 | 每周 |

## docs/06-DATABASE.md

project: 渐构 JianGou

version: 1.0

frontend: Vue 3 + Nuxt 4 + TypeScript

backend: Java 1.8 + Spring Boot 2

database: MySQL

purpose: 给 Codex / AI Coding Agent 使用的完整工程蓝图

## 06. 数据库设计

### 6.1 设计原则

数据库使用 MySQL 8.0+（InnoDB 引擎，字符集 utf8mb4，排序规则 utf8mb4_unicode_ci）。

所有核心业务表必须包含：

- id BIGINT UNSIGNED AUTO_INCREMENT（主键，替代 UUID）
- created_at
- updated_at
- deleted_at
- metadata JSON

命名规范：

- 表名使用复数：articles
- 字段使用 snake_case
- 外键使用 xxx_id
- 状态使用 varchar + 约束或应用层枚举
- 所有软删除表查询时默认过滤 deleted_at is null
- 主键使用 BIGINT UNSIGNED AUTO_INCREMENT，外键为对应 BIGINT UNSIGNED
- DATETIME 字段存储应用层转为 UTC 的时间，不依赖 MySQL TIMESTAMP 的时区自动转换
- JSON 列不可直接建索引；需按 JSON 字段过滤时，使用 GENERATED COLUMN（虚拟列）加 B-Tree 索引
- MySQL 不支持条件唯一索引（Partial Index），涉及防重的复杂场景结合 Redis 或拆分表处理

### 6.2 表清单

| 表名 | 模块 |
|---|---|
| users | 用户 |
| roles | 角色 |
| permissions | 权限 |
| user_roles | 用户角色 |
| role_permissions | 角色权限 |
| categories | 分类 |
| tags | 标签 |
| articles | 文章 |
| article_versions | 文章版本 |
| article_tags | 文章标签 |
| snippets | 代码片段 |
| notes | 碎碎念 |
| projects | 项目 |
| comments | 评论 |
| likes | 点赞 |
| attachments | 附件 |
| friend_links | 友链 |
| subscriptions | 订阅 |
| system_settings | 系统设置 |
| audit_logs | 审计日志 |
| github_sync_logs | GitHub 同步日志 |
| search_documents | 搜索文档 |

### 6.3 users

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | bigint unsigned | pk, auto_increment | 用户 ID |
| username | varchar(64) | unique not null | 用户名 |
| display_name | varchar(128) | not null | 显示名 |
| email | varchar(255) | unique | 邮箱 |
| password_hash | varchar(255) | nullable | 后台账号密码 |
| avatar_url | text | nullable | 头像 |
| provider | varchar(32) | nullable | github/local |
| provider_id | varchar(128) | nullable | 第三方 ID |
| status | varchar(32) | not null | active/disabled |
| bio | text | nullable | 简介 |
| last_login_at | DATETIME | nullable | 最近登录 |
| metadata | json | not null | 扩展 |
| created_at | DATETIME | not null | 创建 |
| updated_at | DATETIME | not null | 更新 |
| deleted_at | DATETIME | nullable | 软删除 |

索引：

```sql
create unique index idx_users_provider on users(provider, provider_id);
create index idx_users_status on users(status);
```

### 6.4 roles / permissions

roles：

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | 角色 ID |
| code | varchar(64) | SUPER_ADMIN/ADMIN/EDITOR/USER |
| name | varchar(128) | 名称 |
| description | text | 描述 |
| metadata | json | 扩展 |
| created_at | DATETIME | 创建 |
| updated_at | DATETIME | 更新 |
| deleted_at | DATETIME | 软删除 |

permissions：

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | 权限 ID |
| code | varchar(128) | article:create |
| name | varchar(128) | 名称 |
| module | varchar(64) | 模块 |
| description | text | 描述 |

### 6.5 categories

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | 分类 ID |
| name | varchar(64) | 名称 |
| slug | varchar(64) | URL |
| description | text | 描述 |
| parent_id | bigint unsigned | 父分类 |
| sort_order | int | 排序 |
| post_count | int | 文章数 |
| metadata | json | 扩展 |
| created_at | DATETIME | 创建 |
| updated_at | DATETIME | 更新 |
| deleted_at | DATETIME | 软删除 |

### 6.6 tags

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | 标签 ID |
| name | varchar(64) | 标签名 |
| slug | varchar(64) | URL |
| description | text | 描述 |
| color | varchar(16) | 颜色 |
| usage_count | int | 使用次数 |
| metadata | json | 扩展 |
| created_at | DATETIME | 创建 |
| updated_at | DATETIME | 更新 |
| deleted_at | DATETIME | 软删除 |

### 6.7 articles

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | 文章 ID |
| author_id | bigint unsigned | 作者 |
| category_id | bigint unsigned | 分类 |
| title | varchar(255) | 标题 |
| slug | varchar(255) | URL |
| summary | text | 摘要 |
| cover_attachment_id | bigint unsigned | 封面 |
| content_md | text | Markdown 原文 |
| content_html | text | 渲染 HTML |
| content_text | text | 搜索文本 |
| status | varchar(32) | draft/review/published/archived |
| visibility | varchar(32) | public/private/unlisted |
| pinned | boolean | 置顶 |
| published_at | DATETIME | 发布时间 |
| reading_minutes | int | 阅读时间 |
| word_count | int | 字数 |
| view_count | bigint | 浏览 |
| like_count | bigint | 点赞 |
| comment_count | bigint | 评论 |
| github_repo | varchar(255) | 关联仓库 |
| github_commit_sha | varchar(64) | commit |
| version | int | 版本 |
| metadata | json | TOC/SEO/代码块统计 |
| created_at | DATETIME | 创建 |
| updated_at | DATETIME | 更新 |
| deleted_at | DATETIME | 软删除 |

索引：

```sql
create unique index idx_articles_slug on articles(slug);
create index idx_articles_status_time on articles(status, published_at desc);
create index idx_articles_category on articles(category_id);
create index idx_articles_author on articles(author_id);
-- MySQL 不支持直接索引 JSON 列；如需按 JSON 字段查询，建 GENERATED COLUMN + INDEX
```

### 6.8 article_versions

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | 版本 ID |
| article_id | bigint unsigned | 文章 |
| version | int | 版本号 |
| title | varchar(255) | 标题快照 |
| content_md | text | Markdown 快照 |
| content_html | text | HTML 快照 |
| change_note | text | 修改说明 |
| created_by | bigint unsigned | 修改人 |
| metadata | json | Diff 信息 |
| created_at | DATETIME | 创建 |

### 6.9 article_tags

| 字段 | 类型 | 说明 |
|---|---|---|
| article_id | bigint unsigned | 文章 |
| tag_id | bigint unsigned | 标签 |
| created_at | DATETIME | 创建 |

主键：

```sql
PRIMARY KEY(article_id, tag_id)  -- 两列均为 BIGINT UNSIGNED，组成复合主键
```

### 6.10 snippets

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | 片段 ID |
| author_id | bigint unsigned | 作者 |
| title | varchar(255) | 标题 |
| slug | varchar(255) | URL |
| language | varchar(64) | 语言 |
| code | text | 代码 |
| highlighted_html | text | 高亮 HTML |
| description_md | text | 描述 |
| description_html | text | 描述 HTML |
| visibility | varchar(32) | public/private/unlisted |
| raw_token | varchar(128) | raw token |
| view_count | bigint | 浏览 |
| copy_count | bigint | 复制 |
| metadata | json | 文件名/依赖 |
| created_at | DATETIME | 创建 |
| updated_at | DATETIME | 更新 |
| deleted_at | DATETIME | 软删除 |

### 6.11 notes

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | 动态 ID |
| author_id | bigint unsigned | 作者 |
| content_md | text | Markdown |
| content_html | text | HTML |
| content_text | text | 搜索文本 |
| status | varchar(32) | draft/published/hidden |
| visibility | varchar(32) | public/private/unlisted |
| published_at | DATETIME | 发布 |
| like_count | bigint | 点赞 |
| metadata | json | 设备、来源、标签 |
| created_at | DATETIME | 创建 |
| updated_at | DATETIME | 更新 |
| deleted_at | DATETIME | 软删除 |

### 6.12 projects

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | 项目 ID |
| owner | varchar(128) | GitHub owner |
| repo | varchar(128) | GitHub repo |
| name | varchar(255) | 名称 |
| description | text | 描述 |
| homepage_url | text | 主页 |
| github_url | text | GitHub |
| language | varchar(64) | 主语言 |
| stars | int | Star |
| forks | int | Fork |
| open_issues | int | Issue |
| license | varchar(128) | 许可证 |
| pushed_at | DATETIME | GitHub push |
| synced_at | DATETIME | 同步 |
| sync_status | varchar(32) | ok/failed/stale |
| pinned | boolean | 置顶 |
| sort_order | int | 排序 |
| metadata | json | topics/readme |
| created_at | DATETIME | 创建 |
| updated_at | DATETIME | 更新 |
| deleted_at | DATETIME | 软删除 |

### 6.13 comments

采用邻接表 + path，最多两层。

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | 评论 ID |
| target_type | varchar(32) | article/note/snippet |
| target_id | bigint unsigned | 目标 |
| parent_id | bigint unsigned | 父评论 |
| root_id | bigint unsigned | 根评论 |
| path | text | 排序路径 |
| depth | int | 0/1 |
| user_id | bigint unsigned | 登录用户 |
| nickname | varchar(64) | 匿名昵称 |
| email_hash | varchar(128) | 邮箱 hash |
| website | text | 网站 |
| content_md | text | Markdown |
| content_html | text | HTML |
| status | varchar(32) | pending/approved/rejected/spam |
| ip_hash | varchar(128) | IP hash |
| user_agent_hash | varchar(128) | UA hash |
| like_count | bigint | 点赞 |
| metadata | json | 反垃圾评分 |
| created_at | DATETIME | 创建 |
| updated_at | DATETIME | 更新 |
| deleted_at | DATETIME | 软删除 |

### 6.14 likes

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | 点赞 ID |
| target_type | varchar(32) | article/note/snippet/comment |
| target_id | bigint unsigned | 目标 |
| user_id | bigint unsigned | 用户 |
| fingerprint_hash | varchar(128) | 指纹 |
| ip_hash | varchar(128) | IP |
| created_at | DATETIME | 创建 |
| deleted_at | DATETIME | 取消 |

### 6.15 attachments

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | 附件 ID |
| uploader_id | bigint unsigned | 上传者 |
| filename | varchar(255) | 原文件名 |
| object_key | text | 对象 key |
| url | text | URL |
| mime_type | varchar(128) | MIME |
| size_bytes | bigint | 大小 |
| width | int | 宽 |
| height | int | 高 |
| sha256 | varchar(128) | 哈希 |
| status | varchar(32) | processing/ready/rejected |
| metadata | json | 缩略图/扫描 |
| created_at | DATETIME | 创建 |
| updated_at | DATETIME | 更新 |
| deleted_at | DATETIME | 软删除 |

### 6.16 friend_links

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | 友链 ID |
| name | varchar(128) | 站点名 |
| url | text | 地址 |
| avatar_url | text | 图标 |
| description | text | 描述 |
| owner_email | varchar(255) | 邮箱 |
| status | varchar(32) | pending/approved/rejected |
| sort_order | int | 排序 |
| metadata | json | 审核备注 |
| created_at | DATETIME | 创建 |
| updated_at | DATETIME | 更新 |
| deleted_at | DATETIME | 软删除 |

### 6.17 subscriptions

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | 订阅 ID |
| email | varchar(255) | 邮箱 |
| status | varchar(32) | pending/confirmed/unsubscribed |
| confirm_token | varchar(128) | 确认 |
| unsubscribe_token | varchar(128) | 退订 |
| metadata | json | 偏好 |
| created_at | DATETIME | 创建 |
| updated_at | DATETIME | 更新 |

### 6.18 system_settings

| 字段 | 类型 | 说明 |
|---|---|---|
| key | varchar(128) | 主键 |
| value | json | 设置值 |
| description | text | 描述 |
| is_public | boolean | 前台可读 |
| updated_by | bigint unsigned | 更新人 |
| updated_at | DATETIME | 更新时间 |

### 6.19 audit_logs

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | 日志 ID |
| actor_id | bigint unsigned | 操作者 |
| action | varchar(128) | 动作 |
| target_type | varchar(64) | 目标类型 |
| target_id | bigint unsigned | 目标 ID |
| ip_hash | varchar(128) | IP |
| metadata | json | 变更 Diff |
| created_at | DATETIME | 创建 |

### 6.20 github_sync_logs

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | 日志 ID |
| project_id | bigint unsigned | 项目 |
| status | varchar(32) | success/failed |
| request_count | int | 请求次数 |
| error_message | text | 错误 |
| metadata | json | API 响应摘要 |
| created_at | DATETIME | 创建 |

### 6.21 search_documents

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | 搜索文档 ID |
| target_type | varchar(32) | article/snippet/note/project |
| target_id | bigint unsigned | 目标 |
| title | varchar(255) | 标题 |
| content | text | 内容 |
| tags | json | 标签 |
| status | varchar(32) | active/deleted |
| metadata | json | 摘要、权重 |
| created_at | DATETIME | 创建 |
| updated_at | DATETIME | 更新 |

## docs/07-API.md

project: 渐构 JianGou

version: 1.0

frontend: Vue 3 + Nuxt 4 + TypeScript

backend: Java 1.8 + Spring Boot 2

database: MySQL

purpose: 给 Codex / AI Coding Agent 使用的完整工程蓝图

## 07. REST API 设计

### 7.1 API 约定

基础路径：

```text
/api/v1
```

后台路径：

```text
/api/v1/admin
```

统一返回：

```json
{
  "code": 0,
  "message": "ok",
  "data": {}
}
```

分页返回：

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

### 7.2 公开 API

#### 文章

| 方法 | 路径 | 参数 | 说明 |
|---|---|---|---|
| GET | /articles | page,pageSize,category,tag,keyword | 文章列表 |
| GET | /articles/slug/{slug} | slug | 文章详情 |
| GET | /articles/{id}/related | id | 相关文章 |
| POST | /articles/{id}/like | fingerprint | 点赞 |
| GET | /articles/{id}/toc | id | 目录 |

文章详情返回：

```json
{
  "id": "uuid",
  "title": "理解 Linux 进程模型",
  "slug": "linux-process-model",
  "summary": "从 fork/exec 到调度器。",
  "contentHtml": "<article>...</article>",
  "tags": ["Linux", "OS"],
  "category": {"name": "操作系统", "slug": "os"},
  "readingMinutes": 18,
  "wordCount": 5200,
  "github": {
    "repo": "owner/repo",
    "commitSha": "abc123"
  }
}
```

#### 分类与标签

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /categories | 分类列表 |
| GET | /categories/{slug} | 分类详情 |
| GET | /tags | 标签列表 |
| GET | /tags/{slug} | 标签详情 |

#### 代码片段

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /snippets | 代码片段列表 |
| GET | /snippets/{slug} | 代码片段详情 |
| GET | /snippets/{slug}/raw | raw 代码 |
| POST | /snippets/{id}/copy | 记录复制 |
| POST | /snippets/{id}/like | 点赞 |

#### 碎碎念

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /notes | 动态列表 |
| GET | /notes/{id} | 动态详情 |
| POST | /notes/{id}/like | 点赞 |

#### 项目

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /projects | 项目列表 |
| GET | /projects/{owner}/{repo} | 项目详情 |

#### 评论

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /comments | targetType,targetId |
| POST | /comments | 提交评论 |
| POST | /comments/{id}/like | 评论点赞 |

提交评论：

```json
{
  "targetType": "article",
  "targetId": "uuid",
  "parentId": null,
  "nickname": "匿名开发者",
  "email": "dev@example.com",
  "website": "https://example.com",
  "contentMd": "写得很好。"
}
```

#### 搜索

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /search | q,type,page,pageSize |
| GET | /search/suggest | q |

#### 订阅

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | /subscriptions | 订阅 |
| GET | /subscriptions/confirm | 确认 |
| GET | /subscriptions/unsubscribe | 退订 |

#### 系统

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /settings/public | 前台公开设置 |
| GET | /stats | 站点统计 |

### 7.3 认证 API

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | /auth/login | 后台登录 |
| POST | /auth/logout | 登出 |
| POST | /auth/refresh | 刷新 token |
| GET | /auth/github | GitHub OAuth |
| GET | /auth/github/callback | GitHub 回调 |
| GET | /auth/me | 当前用户 |

### 7.4 后台 API

#### 文章管理

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /admin/articles | 管理列表 |
| POST | /admin/articles | 创建 |
| GET | /admin/articles/{id} | 详情 |
| PATCH | /admin/articles/{id} | 更新 |
| DELETE | /admin/articles/{id} | 软删除 |
| POST | /admin/articles/{id}/publish | 发布 |
| POST | /admin/articles/{id}/unpublish | 下线 |
| POST | /admin/articles/{id}/archive | 归档 |
| GET | /admin/articles/{id}/versions | 版本 |
| POST | /admin/articles/{id}/restore/{version} | 恢复版本 |

#### 分类标签

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /admin/categories | 列表 |
| POST | /admin/categories | 创建 |
| PATCH | /admin/categories/{id} | 更新 |
| DELETE | /admin/categories/{id} | 删除 |
| GET | /admin/tags | 列表 |
| POST | /admin/tags | 创建 |
| PATCH | /admin/tags/{id} | 更新 |
| DELETE | /admin/tags/{id} | 删除 |

#### 代码片段管理

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /admin/snippets | 列表 |
| POST | /admin/snippets | 创建 |
| PATCH | /admin/snippets/{id} | 更新 |
| DELETE | /admin/snippets/{id} | 删除 |

#### 碎碎念管理

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /admin/notes | 列表 |
| POST | /admin/notes | 创建 |
| PATCH | /admin/notes/{id} | 更新 |
| DELETE | /admin/notes/{id} | 删除 |
| POST | /admin/notes/{id}/publish | 发布 |

#### 评论审核

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /admin/comments | 列表 |
| POST | /admin/comments/{id}/approve | 通过 |
| POST | /admin/comments/{id}/reject | 拒绝 |
| POST | /admin/comments/{id}/spam | 垃圾 |
| DELETE | /admin/comments/{id} | 删除 |

#### 项目管理

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /admin/projects | 列表 |
| POST | /admin/projects | 添加 |
| PATCH | /admin/projects/{id} | 更新 |
| DELETE | /admin/projects/{id} | 删除 |
| POST | /admin/projects/{id}/sync | 同步单个 |
| POST | /admin/projects/sync | 同步全部 |

#### 媒体库

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /admin/attachments | 列表 |
| POST | /admin/attachments | 上传 |
| DELETE | /admin/attachments/{id} | 删除 |

#### 友链

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /admin/friend-links | 列表 |
| POST | /admin/friend-links | 创建 |
| PATCH | /admin/friend-links/{id} | 更新 |
| POST | /admin/friend-links/{id}/approve | 通过 |
| POST | /admin/friend-links/{id}/reject | 拒绝 |
| DELETE | /admin/friend-links/{id} | 删除 |

#### 系统设置

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /admin/settings | 列表 |
| PATCH | /admin/settings/{key} | 更新 |
| GET | /admin/audit-logs | 审计日志 |
| POST | /admin/rss/rebuild | 重建 RSS |
| POST | /admin/search/rebuild | 重建搜索索引 |

## docs/08-BUSINESS.md

project: 渐构 JianGou

version: 1.0

frontend: Vue 3 + Nuxt 4 + TypeScript

backend: Java 1.8 + Spring Boot 2

database: MySQL

purpose: 给 Codex / AI Coding Agent 使用的完整工程蓝图

## 08. 关键业务逻辑

### 8.1 文章状态机

状态：

```text
draft → review → published → archived
  ↑        ↓          ↓
  └────────┴──────────┘
```

转换：

| 当前 | 目标 | 说明 |
|---|---|---|
| draft | review | 提交审核 |
| draft | published | 管理员直接发布 |
| review | published | 审核通过 |
| published | draft | 撤回修改 |
| published | archived | 归档 |
| archived | published | 恢复 |
| any | deleted_at | 软删除 |

发布动作：

1. 校验标题、slug、正文。

2. 渲染 Markdown。

3. 提取 TOC。

4. 提取纯文本。

5. 统计字数和阅读时间。

6. 写入 article_versions。

7. 更新 published_at。

8. 写审计日志。

9. 投递 RSS 重建任务。

10. 投递搜索索引任务。

11. 清理缓存。

### 8.2 评论嵌套

采用邻接表 + path。

限制：

- 最多两层。
- depth=0 一级评论。
- depth=1 回复。
- parent.depth >= 1 时禁止继续回复。

查询：

```sql
select *
from comments
where target_type = ?
  and target_id = ?
  and status = 'approved'
  and deleted_at is null
order by path asc;
```

### 8.3 点赞防刷

身份：

- 登录用户：user_id
- 匿名：fingerprint_hash + ip_hash

流程：

1. Redis 检查 IP 频率。

2. Redis 检查 fingerprint 对目标是否已赞。

3. MySQL 唯一索引防并发。

4. 成功后更新 like_count。

5. 失败返回已点赞。

Redis Key：

```text
rate:like:ip:{ipHash}
like:{targetType}:{targetId}:{fingerprintHash}
```

### 8.4 RSS 生成

触发：

- 文章发布
- 文章更新
- 文章下线
- 后台手动重建

流程：

```text
ArticlePublishedEvent
→ RssBuildJob
→ 查询最近 50 篇文章
→ 生成 feed.xml / atom.xml / sitemap.xml
→ 上传对象存储或写入静态目录
→ 刷新 CDN
```

缓存：

```http
Cache-Control: public, max-age=300, stale-while-revalidate=86400
```

### 8.5 GitHub 同步

同步字段：

- description
- stars
- forks
- language
- topics
- license
- homepage
- pushed_at
- open_issues

策略：

- 每天定时同步。
- 后台支持手动同步。
- API 失败时保留旧数据。
- 记录 github_sync_logs。
- 达到 rate limit 时标记 stale。

### 8.6 代码片段高亮

保存流程：

```text
提交代码
→ 校验 language
→ 生成 highlighted_html
→ 保存 code 与 highlighted_html
→ 搜索索引更新
```

前台：

- 直接展示 highlighted_html。
- 复制按钮客户端挂载。
- raw 接口返回 text/plain。

### 8.7 友链审核

状态：

```text
pending → approved
pending → rejected
approved → hidden
```

申请字段：

- 站点名
- URL
- 图标
- 描述
- 邮箱

审核通过后才前台展示。

### 8.8 Newsletter

流程：

```text
用户提交邮箱
→ 生成确认 token
→ 发送确认邮件
→ 点击确认
→ 状态变为 confirmed
```

退订：

```text
邮件中的 unsubscribe_token
→ GET /subscriptions/unsubscribe
→ 状态变为 unsubscribed
```

### 8.9 附件上传

流程：

```text
上传
→ 文件大小校验
→ MIME 校验
→ sha256 去重
→ 病毒扫描
→ 图片重采样
→ 生成缩略图
→ 上传 MinIO/OSS
→ 写 attachments
```

### 8.10 搜索索引

内容变更触发：

- article published
- article updated
- snippet updated
- note published
- project synced

更新 search_documents，后期同步外部搜索引擎。

## docs/09-SECURITY.md

project: 渐构 JianGou

version: 1.0

frontend: Vue 3 + Nuxt 4 + TypeScript

backend: Java 1.8 + Spring Boot 2

database: MySQL

purpose: 给 Codex / AI Coding Agent 使用的完整工程蓝图

## 09. 安全设计

### 9.1 安全目标

- 防 XSS
- 防 CSRF
- 防 SQL 注入
- 防暴力破解
- 防评论垃圾
- 防上传恶意文件
- 后台权限隔离
- 敏感信息不明文存储

### 9.2 认证

后台登录：

- 用户名 + 密码
- BCrypt
- JWT access token
- refresh token
- refresh token 可撤销

评论登录：

- GitHub OAuth
- 匿名评论

### 9.3 授权

使用 Spring Security。

权限注解：

```java
@PreAuthorize("hasAuthority('article:publish')")
```

### 9.4 XSS 防护

Markdown 渲染后必须 sanitize。

禁止：

- script
- iframe
- javascript:
- onerror/onload
- style 中危险内容

评论区默认不允许原始 HTML。

### 9.5 CSP

建议响应头：

```http
Content-Security-Policy:
  default-src 'self';
  script-src 'self';
  style-src 'self' 'unsafe-inline';
  img-src 'self' data: https:;
  font-src 'self' https:;
  connect-src 'self';
  frame-ancestors 'none';
```

### 9.6 上传安全

限制：

- 图片最大 10MB
- 附件最大 50MB
- 允许 jpg/png/webp/gif/pdf/zip
- SVG 默认禁止
- MIME 与文件头双重检测
- ClamAV 扫描
- EXIF 清理

### 9.7 评论风控

- Akismet
- IP 限流
- 关键词黑名单
- 链接数量限制
- 新访客评论进入 pending
- 短时间重复内容拦截

### 9.8 SQL 注入

- 使用 MyBatis 参数绑定
- 禁止字符串拼 SQL
- 排序字段白名单
- 搜索关键词转义

### 9.9 日志脱敏

不得记录：

- 密码
- token
- 完整邮箱
- 完整 IP
- OAuth secret

使用 hash：

- ip_hash
- email_hash
- user_agent_hash

### 9.10 后台防护

- 管理后台路由鉴权
- API 权限鉴权
- 登录失败限流
- 关键操作审计
- CSRF 防护
- Token 自动刷新

## docs/10-SEARCH.md

project: 渐构 JianGou

version: 1.0

frontend: Vue 3 + Nuxt 4 + TypeScript

backend: Java 1.8 + Spring Boot 2

database: MySQL

purpose: 给 Codex / AI Coding Agent 使用的完整工程蓝图

## 10. 搜索设计

### 10.1 搜索目标

搜索范围：

- 文章标题
- 文章摘要
- 文章正文
- 标签
- 分类
- 代码片段标题
- 代码内容
- 碎碎念
- 项目描述

### 10.2 阶段一：MySQL ILIKE

适合数据很少时。

```sql
where title ilike '%' || :q || '%'
   or content_text ilike '%' || :q || '%'
```

### 10.3 阶段二：MySQL FULLTEXT

使用 search_documents 表统一索引。

```sql
create index idx_search_documents_fts
on search_documents
WITH PARSER ngram;  -- MySQL FULLTEXT 中文分词，需 ngram_token_size=2
```

查询：

```sql
select *
from search_documents
WHERE MATCH(title, content) AGAINST (:q IN BOOLEAN MODE)
order by updated_at desc
limit :limit offset :offset;
```

### 10.4 阶段三：Meilisearch

适合个人博客中期。

优点：

- 部署简单
- 搜索体验好
- 支持 typo
- 支持高亮

### 10.5 阶段四：Elasticsearch

适合复杂需求：

- 字段权重
- 聚合
- 高亮
- 同义词
- 技术词典
- 拼音

### 10.6 索引更新

事件：

```text
ArticlePublishedEvent
SnippetUpdatedEvent
NotePublishedEvent
ProjectSyncedEvent
```

流程：

```text
业务更新
→ 写数据库
→ 发布 SearchIndexEvent
→ 异步更新 search_documents
→ 后期同步外部搜索引擎
```

### 10.7 搜索 API

```text
GET /api/v1/search?q=redis&type=article&page=1&pageSize=20
```

返回：

```json
{
  "items": [
    {
      "type": "article",
      "title": "Redis 缓存设计",
      "url": "/posts/redis-cache-design",
      "snippet": "本文讨论 Redis 缓存穿透、击穿和雪崩...",
      "score": 0.98
    }
  ]
}
```

## docs/11-DEPLOY.md

project: 渐构 JianGou

version: 1.0

frontend: Vue 3 + Nuxt 4 + TypeScript

backend: Java 1.8 + Spring Boot 2

database: MySQL

purpose: 给 Codex / AI Coding Agent 使用的完整工程蓝图

## 11. 部署设计

### 11.1 本地开发

依赖：

- MySQL
- Redis
- MinIO
- Mailhog

启动：

```bash
cd docker
docker compose up -d
```

### 11.2 应用部署

推荐目录：

```text
/opt/jiangou/
  frontend/
  admin/
  backend/
  nginx/
  data/
```

### 11.3 Nginx

职责：

- TLS
- 反向代理
- gzip / brotli
- 静态资源缓存
- API 转发
- 上传大小限制

示例：

```nginx
server {
  listen 80;
  server_name example.com;
  location / {
    proxy_pass http://frontend:3000;
  }
  location /admin/ {
    root /usr/share/nginx/html;
    try_files $uri $uri/ /admin/index.html;
  }
  location /api/ {
    proxy_pass http://backend:8080;
  }
}
```

### 11.4 环境变量

后端：

```text
SPRING_PROFILES_ACTIVE=prod
MYSQL_HOST=
MYSQL_PORT=
MYSQL_DATABASE=
MYSQL_USER=
MYSQL_PASSWORD=
REDIS_HOST=
MINIO_ENDPOINT=
GITHUB_CLIENT_ID=
GITHUB_CLIENT_SECRET=
JWT_SECRET=
```

前端：

```text
NUXT_PUBLIC_API_BASE=/api/v1
NUXT_PUBLIC_SITE_URL=https://example.com
```

### 11.5 CI/CD

流程：

```text
push main
→ lint
→ test
→ build frontend
→ build admin
→ build backend jar
→ docker build
→ deploy
→ health check
```

### 11.6 备份

MySQL：

```bash
mysqldump -u root -p jiangou > backup.sql
```

MinIO：

- 每日同步到远程对象存储
- 保留 30 天

Redis：

- 不作为核心持久化来源
- 可开启 AOF

### 11.7 监控

- Spring Actuator
- Prometheus
- Grafana
- Sentry
- Nginx access log

## docs/12-ADMIN.md

project: 渐构 JianGou

version: 1.0

frontend: Vue 3 + Nuxt 4 + TypeScript

backend: Java 1.8 + Spring Boot 2

database: MySQL

purpose: 给 Codex / AI Coding Agent 使用的完整工程蓝图

## 12. 后台 CMS 设计

### 12.1 后台目标

后台用于管理所有内容和系统配置。

技术：

- Vue 3
- Vite
- TypeScript
- Naive UI / Element Plus
- Pinia
- Monaco Editor / Markdown Editor

### 12.2 菜单

```text
仪表盘
内容管理
  文章
  分类
  标签
  代码片段
  碎碎念
互动管理
  评论审核
  友链审核
  留言管理
项目管理
  GitHub 项目
媒体库
系统
  用户
  角色
  权限
  系统设置
  审计日志
```

### 12.3 文章编辑器

功能：

- Markdown 编辑
- 实时预览
- LaTeX
- Mermaid
- 代码高亮
- 标签选择
- 分类选择
- slug 自动生成
- 摘要生成
- GitHub repo / commit 关联
- 保存草稿
- 发布
- 版本历史

### 12.4 评论审核

列表字段：

- 内容
- 作者
- 目标文章
- IP hash
- 状态
- 反垃圾评分
- 创建时间

操作：

- 通过
- 拒绝
- 标记垃圾
- 删除
- 拉黑关键词

### 12.5 媒体库

功能：

- 上传图片
- 文件夹筛选
- 图片预览
- 复制 URL
- 删除
- 查看大小和尺寸
- 查看使用位置

### 12.6 项目管理

功能：

- 添加 GitHub 仓库
- 手动同步
- 置顶
- 排序
- 本地描述覆盖
- 查看同步日志

### 12.7 系统设置

设置项：

- 站点标题
- 站点描述
- Logo
- 默认主题
- GitHub owner
- 评论开关
- Akismet key
- 邮件配置
- RSS 开关

## docs/13-CODE_STYLE.md

project: 渐构 JianGou

version: 1.0

frontend: Vue 3 + Nuxt 4 + TypeScript

backend: Java 1.8 + Spring Boot 2

database: MySQL

purpose: 给 Codex / AI Coding Agent 使用的完整工程蓝图

## 13. 编码规范

### 13.1 Java 规范

- Java 1.8
- Spring Boot 2
- 使用 Lombok 简化样板代码
- DTO / VO / Entity 分离
- Controller 不写业务逻辑
- Service 控制事务
- Mapper 只做数据访问
- 复杂 SQL 放 XML
- 所有 public 方法写必要注释

### 13.2 包命名

```text
com.jiangou.article.controller
com.jiangou.article.service
com.jiangou.article.mapper
com.jiangou.article.entity
com.jiangou.article.dto
com.jiangou.article.vo
```

### 13.3 命名

| 类型 | 规范 |
|---|---|
| Controller | ArticleController |
| Service | ArticleService |
| ServiceImpl | ArticleServiceImpl |
| Mapper | ArticleMapper |
| Entity | ArticleEntity |
| DTO | CreateArticleDTO |
| VO | ArticleDetailVO |

### 13.4 REST 规范

- GET 查询
- POST 创建
- PATCH 局部更新
- DELETE 删除
- 路径使用复数

### 13.5 Vue 规范

- 使用 Composition API
- 组件名 PascalCase
- composable 使用 use 前缀
- API 文件以 .api.ts 结尾
- Store 使用 Pinia

### 13.6 Git 提交

格式：

```text
feat(article): add article publish api
fix(comment): prevent nested depth overflow
docs(api): update article endpoint
```

类型：

- feat
- fix
- docs
- refactor
- test
- chore
- perf

## docs/14-CODEX_RULES.md

project: 渐构 JianGou

version: 1.0

frontend: Vue 3 + Nuxt 4 + TypeScript

backend: Java 1.8 + Spring Boot 2

database: MySQL

purpose: 给 Codex / AI Coding Agent 使用的完整工程蓝图

## 14. Codex 开发规则

### 14.1 总规则

Codex 必须遵守：

1. 不擅自更换技术栈。

2. 前端使用 Vue 3 / Nuxt 4。

3. 后端使用 Java 1.8 / Spring Boot 2。

4. 数据库使用 MySQL 8.0+（InnoDB 引擎，字符集 utf8mb4，排序规则 utf8mb4_unicode_ci）。

5. ORM 使用 MyBatis-Plus。

6. 迁移使用 Flyway。

7. 所有 API 有 OpenAPI 注解。

8. 所有接口统一返回 ApiResult。

9. 所有异常走 GlobalExceptionHandler。

10. 所有表支持软删除。

11. 所有后台接口必须鉴权。

12. 所有用户输入必须校验。

### 14.2 项目初始化 Prompt

```text
请根据 docs 文档创建渐构项目骨架。
生成 frontend、admin、backend、sql、docker 目录。
frontend 使用 Nuxt 4 + Vue 3 + TypeScript。
admin 使用 Vue 3 + Vite + TypeScript。
backend 使用 Java 1.8 + Spring Boot 2 + MyBatis-Plus + MySQL + Redis + Flyway。
先实现最小可启动项目，不要一次实现全部业务。
```

### 14.3 后端模块生成 Prompt

```text
请实现 article 模块。
要求：
Entity/DTO/VO/Mapper/Service/Controller 分层
支持创建、更新、发布、下线、删除、查询
使用 ApiResult
使用 PageResult
使用 OpenAPI 注解
使用 @PreAuthorize
写 Flyway migration
写基础单元测试
```

### 14.4 前端模块生成 Prompt

```text
请实现文章前台页面。
要求：
Nuxt 页面 /posts 和 /posts/[slug]
ArticleCard、ArticleMeta、ArticleContent、ArticleTOC 组件
调用 services/article.api.ts
支持加载态、错误态、空态
使用 CSS variables 支持明暗主题
```

### 14.5 禁止事项

Codex 不得：

- 把密码写入代码。
- 使用本地磁盘代替对象存储抽象。
- 跳过权限校验。
- 直接拼接 SQL。
- 删除文档约束。
- 把评论 HTML 原样输出。
- 把所有业务写在 Controller。

## docs/15-ROADMAP.md

project: 渐构 JianGou

version: 1.0

frontend: Vue 3 + Nuxt 4 + TypeScript

backend: Java 1.8 + Spring Boot 2

database: MySQL

purpose: 给 Codex / AI Coding Agent 使用的完整工程蓝图

## 15. 开发路线

### 15.1 阶段 0：项目骨架

目标：

- monorepo 目录
- frontend 可启动
- admin 可启动
- backend 可启动
- Docker Compose 依赖可启动
- Flyway 初始化

验收：

```text
pnpm dev
pnpm admin:dev
./mvnw spring-boot:run
docker compose up -d
```

### 15.2 阶段 1：MVP

功能：

- 登录
- 文章 CRUD
- 文章发布
- Markdown 渲染
- 分类标签
- 前台文章列表
- 前台文章详情
- 评论提交与审核
- RSS
- 基础搜索

### 15.3 阶段 2：内容增强

功能：

- 代码片段
- 碎碎念
- 项目陈列
- GitHub 同步
- 媒体库
- 友链申请
- 留言板

### 15.4 阶段 3：工程化

功能：

- OpenAPI 完善
- 单元测试
- CI/CD
- 监控
- 审计日志
- 数据备份
- CDN 缓存刷新

### 15.5 阶段 4：体验升级

功能：

- Meilisearch / Elasticsearch
- Newsletter
- 文章版本 Diff
- 主题系统
- 统计看板
- Webmention 可选

### 15.6 优先级

P0：

- 文章
- Markdown
- 登录
- 后台
- 数据库
- 部署

P1：

- 评论
- 搜索
- RSS
- 代码片段

P2：

- GitHub 同步
- 友链
- Newsletter
- 媒体库

P3：

- Elasticsearch
- Webmention
- 统计分析

## docs/SOURCES.md

project: 渐构 JianGou

version: 1.0

frontend: Vue 3 + Nuxt 4 + TypeScript

backend: Java 1.8 + Spring Boot 2

database: MySQL

purpose: 给 Codex / AI Coding Agent 使用的完整工程蓝图

## 来源与依据

本文档包基于以下输入整理：

### 用户明确需求

- 博客名：「渐构」
- 主题：计算机基础、编程语言、系统架构、算法、开源项目笔记、技术随笔
- 目标用户：开发者、计算机学生、技术爱好者
- 体验：沉浸阅读、代码优先、检索便捷、无打扰社交
- 前端尽可能使用 Vue
- 后端使用 Java
- 需要可交给 Codex 搭建

### 参考站点公开页面特征

#### blog.canmoe.com

公开页面搜索结果显示该站具备：

- 主页
- 归档
- GitHub
- 站点监控
- 友链
- 留言
- 关于
- RSS / Sitemap
- 亮色 / 暗色 / 跟随系统
- 公告
- 分类
- 标签
- 文章字数
- 阅读时间
- 文章目录
- 评论区
- 站点统计

参考 URL：

- https://blog.canmoe.com/
- https://blog.canmoe.com/posts/jumper-n100/

#### imsuk.cn

公开页面搜索结果显示该站具备：

- 时光机
- 友链申请
- 分类
- 归档
- 留言板
- 隐私政策
- 教程、服务搭建、Web 随笔等分类
- 站点信息展示

参考 URL：

- https://imsuk.cn/
- https://imsuk.cn/cross.html
- https://imsuk.cn/author/1/

### 设计边界声明

本文档不是复制参考站点，而是提炼其公开可见的信息架构与体验模式，并结合用户要求重新设计一个偏技术知识管理的个人博客系统。

## 16. 架构与数据库优化方案

### 16.1 当前架构问题分析

#### 16.1.1 计数器字段并发隐患

articles 表中的 view_count、like_count、comment_count，categories 的 post_count，tags 的 usage_count，均为直接写 MySQL 的冗余计数字段。高并发写入时容易产生竞争条件，且频繁 UPDATE 单行会造成锁等待。

优化方案：使用 Redis INCR 维护热计数，定期（每 5 分钟）通过定时任务将 Redis 计数同步回 MySQL。Redis Key 规范：

- counter:article:view:{id}
- counter:article:like:{id}
- counter:article:comment:{id}
- counter:category:count:{id}
- counter:tag:count:{id}

#### 16.1.2 content_html 冗余存储

articles 表同时存储 content_md 和 content_html，每次文章更新都需同步维护两份内容，易产生不一致，且 HTML 体积通常远大于 Markdown 原文，占用大量磁盘空间。

优化方案：仅持久化 content_md，发布时渲染 HTML 并缓存到 Redis（key: article:html:{id}，TTL 24h）。增加 rendered_at DATETIME 字段记录渲染时间戳，渲染版本过期时触发重建。

#### 16.1.3 异步任务缺乏消息队列

当前 RSS 生成、搜索索引更新、邮件通知等均以同步或简单定时任务方式处理，事件丢失无法重试，且任务耦合在主业务流中影响接口响应延迟。

优化方案：引入 RabbitMQ 作为消息总线，将 ArticlePublishedEvent、CommentCreatedEvent、ProjectSyncedEvent 等解耦为异步消费，消费失败自动进入死信队列（DLQ）并触发告警。

#### 16.1.4 全文搜索改用 MySQL FULLTEXT INDEX

MySQL 迁移后，MySQL 阶段一的 LIKE 查询性能差，推荐使用 MySQL FULLTEXT INDEX 配合 ngram 分词插件支持中文搜索，替代原 MySQL FTS 方案。

优化方案：增加 fulltext_index FULLTEXT vector GENERATED ALWAYS AS (to_FULLTEXT vector('simple', coalesce(title,'') || ' ' || coalesce(content,''))) STORED 列（MySQL 5.7+），并在该列建立 GIN 索引。

### 16.2 数据库表结构优化

#### 16.2.1 articles 表优化

| 变更项 | 原设计 | 优化后 | 原因 |
|---|---|---|---|
| content_html | text 持久化 | 移除，改 Redis 缓存 | 避免冗余，节省磁盘 |
| rendered_at | 无 | DATETIME nullable | 追踪 HTML 渲染时间 |
| view_count | bigint 直写 DB | Redis 计数，定期同步 | 避免高频写锁 |
| like_count | bigint 直写 DB | Redis 计数，定期同步 | 防并发冲突 |
| comment_count | bigint 直写 DB | Redis 计数，定期同步 | 防并发冲突 |
| fulltext_idx | 无 | FULLTEXT INDEX (title, content_text) WITH PARSER ngram | 替代 FULLTEXT vector，支持中文 |

新增索引：

```sql
CREATE FULLTEXT INDEX idx_articles_fts ON articles(title, content_text) WITH PARSER ngram;
-- MySQL 不支持 Partial Index，用 covering index 代替：
CREATE INDEX idx_articles_pinned ON articles(pinned, published_at, deleted_at);
```

#### 16.2.2 likes 表优化

原设计缺少唯一约束，无法从数据库层面防止重复点赞，仅依赖 Redis 防重，重启 Redis 后可能数据错乱。

优化后增加以下唯一约束：

```sql
-- MySQL 不支持条件唯一索引（Partial Index），采用拆表方案
-- user_likes 表（登录用户）
CREATE UNIQUE INDEX idx_user_likes ON user_likes(target_type, target_id, user_id);
-- anon_likes 表（匿名用户）
CREATE UNIQUE INDEX idx_anon_likes ON anon_likes(target_type, target_id, fingerprint_hash);
```

#### 16.2.3 comments 表优化

增加复合索引以加速评论列表查询和状态过滤：

```sql
CREATE INDEX idx_comments_target ON comments(target_type, target_id, status, deleted_at, created_at DESC);
CREATE INDEX idx_comments_parent ON comments(parent_id);  -- MySQL：NULL 值不加入索引，性能等效
```

增加 reply_count int DEFAULT 0 字段，记录每条一级评论的回复数，避免统计时全表扫描。

#### 16.2.4 search_documents 表优化

| 变更项 | 原设计 | 优化后 |
|---|---|---|
| fulltext_index | 无 | CREATE FULLTEXT INDEX ... WITH PARSER ngram |
| FULLTEXT 查询 | LIKE（慢）| MATCH...AGAINST IN BOOLEAN MODE（快，支持中文）|
| boost | 无 | 新增 boost float DEFAULT 1.0 用于 FULLTEXT 相关性加权排序 |

MySQL FULLTEXT 用法：

```sql
-- 建立 FULLTEXT 索引（支持中文 ngram 分词）
CREATE FULLTEXT INDEX idx_search_docs_fts
ON search_documents(title, content)
WITH PARSER ngram;  -- my.cnf: ngram_token_size=2

-- 查询示例
SELECT * FROM search_documents
WHERE MATCH(title, content) AGAINST (:q IN BOOLEAN MODE)
ORDER BY MATCH(title, content) AGAINST (:q IN BOOLEAN MODE) DESC;
```

#### 16.2.5 article_versions 表优化

增加 is_significant boolean DEFAULT false 字段，标记重要版本（如发布版、手动标记版），定时清理任务保留最近 20 个普通版本 + 全部重要版本，避免无限增长。

#### 16.2.6 缺失索引补全

| 表 | 索引 | 用途 |
|---|---|---|
| audit_logs | (actor_id, created_at DESC) | 用户操作历史查询 |
| audit_logs | (target_type, target_id, created_at DESC) | 资源审计查询 |
| subscriptions | UNIQUE (email, status) + 应用层逻辑 | 防重复订阅（MySQL 无条件唯一索引）|
| snippets | (language, deleted_at) | 按语言筛选 |
| notes | (status, published_at DESC) WHERE deleted_at IS NULL | 时间线查询 |
| friend_links | (status, sort_order) WHERE deleted_at IS NULL | 前台展示 |
| github_sync_logs | (project_id, created_at DESC) | 同步历史查询 |

#### 16.2.7 新增辅助表

page_view_logs — 详细访问日志（与 articles.view_count 解耦）：

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | 日志 ID |
| target_type | varchar(32) | article/snippet/note |
| target_id | bigint unsigned | 目标 |
| session_hash | varchar(128) | 会话 Hash |
| ip_hash | varchar(128) | IP Hash |
| referrer | text | 来源 URL |
| user_agent_hash | varchar(128) | UA Hash |
| created_at | DATETIME | 访问时间 |

blacklist — 黑名单（IP、关键词、域名）：

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | ID |
| type | varchar(32) | ip/keyword/domain |
| value | text | 黑名单内容 |
| reason | text | 封禁原因 |
| expires_at | DATETIME | 到期时间，null 表示永久 |
| created_by | bigint unsigned | 操作人 |
| created_at | DATETIME | 创建时间 |

notification_logs — 通知发送记录：

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint unsigned | pk, auto_increment | ID |
| type | varchar(32) | email/webhook |
| recipient | varchar(255) | 接收方（脱敏）|
| subject | varchar(255) | 主题 |
| status | varchar(32) | sent/failed/bounced |
| retry_count | int | 重试次数 |
| error_message | text | 错误信息 |
| created_at | DATETIME | 发送时间 |

## 17. 单体架构方案

### 17.1 架构目标

单体架构将所有业务模块打包为单一 Spring Boot 可执行 JAR，适合个人博客的早期到中期阶段。目标：

- 部署简单，一台云服务器即可完整运行
- 开发效率高，模块间调用无网络开销
- 数据一致性强，事务边界清晰
- 资源消耗低，最低 2 vCPU + 2 GB RAM 可运行
- 适合日 UV 10 万以下的个人技术博客

### 17.2 单体架构图

```
┌──────────────────────────────────────────────────────┐
│                      Browser                          │
└──────────────────────┬───────────────────────────────┘
                       │ HTTPS
┌──────────────────────▼───────────────────────────────┐
│            Nginx  （TLS / Gzip / 限流 / 反代）        │
└──────┬──────────────┬───────────────┬────────────────┘
       │              │               │
┌──────▼──────┐ ┌────▼────┐  ┌───────▼──────────────┐
│  Nuxt SSR   │ │  /admin │  │     /api/v1/*        │
│  :3000      │ │  静态   │  │   Spring Boot :8080  │
│  SSR / SSG  │ │  Vue SPA│  │                      │
└──────┬──────┘ └─────────┘  │ ┌────────────────┐   │
       │ REST                 │ │  article       │   │
       └──────────────────────► │  comment       │   │
                              │ │  snippet/note  │   │
                              │ │  project/sync  │   │
                              │ │  search/upload │   │
                              │ │  auth/security │   │
                              │ │  subscription  │   │
                              │ │  friendlink    │   │
                              │ └────────────────┘   │
                              │ ┌──────────────────┐ │
                              │ │  Spring Scheduler│ │
                              │ │  RSS / 索引重建  │ │
                              │ │  GitHub 同步     │ │
                              │ └──────────────────┘ │
                              └───┬──────┬───────────┘
                                  │      │
              ┌───────────────────┘      └─────────────┐
              │                                         │
  ┌───────────▼────┐  ┌────────────┐  ┌───────────────▼──┐
  │  MySQL    │  │  Redis     │  │  MinIO / OSS     │
  │  单实例        │  │  单实例    │  │  对象存储        │
  │  主数据库      │  │  缓存/限流 │  │  图片/附件       │
  └────────────────┘  └────────────┘  └──────────────────┘
```

### 17.3 应用划分

| 组件 | 技术 | 说明 |
|---|---|---|
| frontend | Nuxt 4 + Vue 3 | 访客前台，SSR/SSG |
| admin | Vue 3 + Vite | 管理后台，SPA 静态部署 |
| backend | Spring Boot 1.8 单体 JAR | 全部 REST API |
| mysql | MySQL 5.7+ | 单实例主库 |
| redis | Redis 6+ | 缓存 / 限流 / 计数器 |
| minio | MinIO | 对象存储 |
| nginx | Nginx 1.20+ | 网关反代 |

### 17.4 包结构（单体）

backend/src/main/java/com/jiangou/

```
  JiangouApplication.java
  common/           # 通用：结果封装、异常、分页、工具
  config/           # 配置：Redis、MinIO、Quartz、Swagger
  security/         # 安全：JWT、Spring Security、Filter
  auth/             # 认证：登录、登出、OAuth
  user/             # 用户：CRUD、角色、权限
  article/          # 文章：发布、版本、状态机
  category/tag/     # 分类 & 标签
  snippet/          # 代码片段
  note/             # 碎碎念
  comment/          # 评论：树形、审核、点赞
  project/          # 项目陈列 & GitHub 同步
  upload/           # 文件上传 & 媒体库
  search/           # 搜索：MySQL FULLTEXT → Meilisearch 适配
  subscription/     # 订阅 & Newsletter
  friendlink/       # 友链
  system/           # 系统设置 & 审计日志
  schedule/         # 定时任务：RSS / 索引 / 同步
  event/            # 应用事件（Spring ApplicationEvent）
  counter/          # 计数器服务（Redis 同步 DB）
```

### 17.5 事件驱动（进程内）

单体架构使用 Spring ApplicationEvent 实现进程内异步事件，替代消息队列：

- ArticlePublishedEvent → RssRebuildListener, SearchIndexListener, CacheEvictListener
- CommentCreatedEvent → NotificationListener, CounterSyncListener
- ProjectSyncedEvent → SearchIndexListener
- CounterSyncEvent → 每 5 分钟将 Redis 计数同步回 MySQL

### 17.6 部署方案

最小化部署（单台云服务器，2C4G）：

docker-compose.yml 包含：

- jiangou-backend  （Spring Boot JAR，8080）
- jiangou-frontend （Nuxt Node Server，3000）
- mysql:8.0      （3306）
- redis:7          （6379）
- minio            （9000/9001）
- nginx            （80/443）

一键启动：

```
docker compose up -d
./mvnw clean package -DskipTests && java -jar target/jiangou.jar
```

### 17.7 适用阶段与优缺点

适用阶段：MVP → 阶段 2（个人博客 + 内容增强），预计 3~5 年。

优点：

- 部署最简单：一个 JAR + docker compose
- 事务边界清晰，数据一致性最强
- 调试方便，无跨服务网络问题
- 开发效率高，不需要服务发现、网关等基础设施
- 资源占用低，适合个人服务器

缺点：

- 单点故障，JAR 崩溃影响所有功能
- 无法单独扩容某个模块（如搜索、上传）
- 随代码量增长，编译时间变长
- 技术栈升级（如 Java 版本）需整体升级

## 18. 分布式架构方案

### 18.1 架构目标

分布式架构将系统拆分为独立可部署的微服务，适合流量增长、需要高可用和弹性伸缩的阶段。目标：

- 各服务独立部署、独立扩容、独立发布
- 故障隔离：评论服务异常不影响文章阅读
- 技术异构：不同服务可使用最适合的技术栈
- 消息驱动：通过 RabbitMQ 解耦同步依赖
- 可观测：分布式链路追踪、统一日志、指标监控
- 适合日 UV 超过 10 万或对可用性要求 99.9% 以上的场景

### 18.2 分布式架构图

```
┌─────────────────────────────────────────────────────────┐
│                       Browser                            │
└──────────────────────┬──────────────────────────────────┘
                       │
              ┌────────▼────────┐
              │   CDN / Nginx   │ TLS / Gzip / 静态缓存
              └────────┬────────┘
                       │
   ┌───────────────────▼──────────────────────────────┐
   │           API Gateway（Spring Cloud Gateway）      │
   │  路由 / JWT 认证 / 全局限流 / 灰度发布 :9000      │
   └──┬───────┬──────────┬──────────┬────────┬────────┘
      │       │          │          │        │
 ┌────▼──┐ ┌──▼─────┐ ┌─▼──────┐ ┌▼──────┐ ┌▼───────┐
 │content│ │comment │ │ user   │ │project│ │upload  │
 │service│ │service │ │service │ │service│ │service │
 │ :8001 │ │ :8003  │ │ :8002  │ │ :8004 │ │ :8007  │
 └───┬───┘ └──┬─────┘ └─┬──────┘ └┬──────┘ └┬───────┘
     │        │          │         │          │
 ┌───▼──┐ ┌──▼───┐ ┌────▼──┐ ┌───▼────┐ ┌───▼───┐
 │PG    │ │PG    │ │PG     │ │PG      │ │MinIO  │
 │内容库│ │评论库│ │用户库 │ │项目库  │ │对象库 │
 └──────┘ └──────┘ └───────┘ └────────┘ └───────┘

  ┌───────────────────────────────────────────────────┐
  │              RabbitMQ 消息总线                     │
  │  article.events / comment.events / sync.events    │
  └──────┬──────────────┬───────────────┬─────────────┘
         │              │               │
   ┌─────▼──────┐ ┌────▼───────┐ ┌────▼──────────┐
   │search      │ │notification│ │ sync          │
   │service     │ │service     │ │ service       │
   │ :8005      │ │ :8006      │ │ :8008         │
   └─────┬──────┘ └────────────┘ └────┬──────────┘
         │                              │
   ┌─────▼──────────┐          ┌───────▼──────────┐
   │ Elasticsearch  │          │  Redis Cluster   │
   │ 全文搜索       │          │  缓存 / 限流     │
   └────────────────┘          └──────────────────┘

  ┌───────────────────────────────────────────────────┐
  │   Nacos：服务注册 & 配置中心                       │
  │   SkyWalking：分布式链路追踪                       │
  │   Prometheus + Grafana：指标与告警                  │
  │   ELK：集中日志（可选 Loki + Grafana）             │
  └───────────────────────────────────────────────────┘
```

### 18.3 服务划分

| 服务 | 端口 | 职责 | 独立数据库 |
|---|---|---|---|
| gateway-service | 9000 | 路由、JWT、限流、灰度 | 无 |
| content-service | 8001 | 文章、片段、碎碎念、分类、标签 | MySQL: jiangou_content |
| user-service | 8002 | 用户、角色、权限、审计 | MySQL: jiangou_user |
| comment-service | 8003 | 评论、点赞、友链、留言 | MySQL: jiangou_comment |
| project-service | 8004 | GitHub 项目元数据管理 | MySQL: jiangou_project |
| search-service | 8005 | 全文搜索、索引管理 | Elasticsearch（替代 MySQL FTS）|
| notification-service | 8006 | 邮件、RSS、Newsletter | 无（消费 MQ）|
| upload-service | 8007 | 文件上传、媒体库管理 | MinIO |
| sync-service | 8008 | GitHub API 同步、定时任务 | 共用 jiangou_project |

### 18.4 消息驱动设计

#### 18.4.1 交换机与队列规划

| Exchange | 类型 | 路由 Key | 消费队列 |
|---|---|---|---|
| article.events | topic | article.published | search.index, notification.rss |
| article.events | topic | article.updated | search.index, cache.evict |
| article.events | topic | article.deleted | search.delete, cache.evict |
| comment.events | topic | comment.created | notification.comment, counter.incr |
| sync.events | topic | sync.github | sync.worker |
| dlq.exchange | direct | # | dlq.queue（告警+人工处理）|

#### 18.4.2 消息失败策略

- 消费失败自动重试 3 次，间隔指数退避（1s / 5s / 30s）
- 超过重试次数进入 DLQ（Dead Letter Queue）
- DLQ 消息触发 Sentry 告警，人工确认后可重放或丢弃
- 消息体含 idempotencyKey，消费端实现幂等

### 18.5 数据隔离策略

各服务严格遵守数据所有权原则：

- 禁止跨服务直接查询数据库，只允许通过 REST API 或 MQ 获取其他服务数据
- 跨服务引用使用 ID 冗余（如 content-service 保存 author_id，用户信息通过 user-service 查询后缓存）
- 分布式事务采用 Saga 模式：文章发布 = content-service 写 DB → MQ 发布事件 → 各消费方本地事务，失败补偿
- 共享的枚举、常量通过共用 common-api 模块（Maven 依赖）同步，不通过接口传递

### 18.6 技术选型（分布式增量）

| 组件 | 技术 | 说明 |
|---|---|---|
| 服务注册 | Nacos 2.x | 服务注册与健康检查 |
| 配置中心 | Nacos Config | 动态配置，替换本地 application.yml |
| API 网关 | Spring Cloud Gateway | 路由、认证、限流 |
| 服务调用 | OpenFeign + Resilience4j | 同步调用 + 熔断降级 |
| 消息总线 | RabbitMQ 3.x | 异步事件驱动 |
| 全文搜索 | Elasticsearch 8.x | 替换 MySQL FULLTEXT |
| 链路追踪 | SkyWalking 9.x | 分布式 Trace、拓扑图 |
| 日志聚合 | Loki + Grafana | 轻量日志方案，替代 ELK |
| 指标监控 | Prometheus + Grafana | 服务指标、告警 |
| 容器编排 | Docker Compose（小规模）/ K8s（大规模）| 按需选择 |

### 18.7 部署方案

小规模（3 台服务器 / K8s 3 节点）：

节点 1：gateway + content-service + user-service

节点 2：comment-service + project-service + search-service + notification-service

节点 3：MySQL（主从）+ Redis Cluster + RabbitMQ + Elasticsearch

大规模（K8s + 云服务）：

- 各服务 Deployment 独立，HPA 按 CPU/QPS 自动扩容
- MySQL → 云数据库（如 RDS），不自建
- Redis → 云 Redis Cluster
- Elasticsearch → 云 ES 或 Serverless
- RabbitMQ → 云消息队列（AliMQ / AWS SQS）
- MinIO → OSS / S3

### 18.8 适用阶段与优缺点

适用阶段：阶段 3 工程化以后，或需要 SLA 99.9% 以上时。

优点：

- 故障隔离：单服务崩溃不影响其他功能
- 弹性扩容：高流量模块（内容服务）可独立横向扩展
- 技术灵活：不同服务可选最适合的 JDK 版本和框架
- 团队协作：多团队并行开发，服务边界即组织边界

缺点：

- 运维复杂度大幅提升，需要服务发现、链路追踪、统一日志等基础设施
- 开发调试困难，本地需要启动多个服务
- 跨服务事务复杂，需要 Saga / 事件溯源等模式
- 网络延迟增加，服务间 REST 调用替代进程内调用
- 初期资源成本高，至少需要 3~5 台独立节点

### 18.9 架构演进路径

推荐渐进式演进，不要一步到位分布式：

阶段 0→2（MVP 到内容增强）：

- 使用单体架构（第 17 章方案）
- 进程内事件驱动，Spring ApplicationEvent

阶段 3（工程化）：

- 抽取 search-service（搜索流量独立，接 Elasticsearch）
- 引入 RabbitMQ，将 RSS 生成、邮件通知、搜索索引异步化
- 单体仍然一个 JAR，只是事件从进程内改为 MQ

阶段 4（体验升级）：

- 抽取 notification-service（邮件、RSS、Newsletter）
- 抽取 sync-service（GitHub 同步独立部署）
- 引入 API Gateway，统一限流和认证

阶段 5（完全分布式）：

- 全量拆分为第 18 章所有微服务
- 引入 Nacos、SkyWalking、K8s
- 数据库按服务隔离
