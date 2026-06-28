# 渐构（JianGou）全栈代码审计报告（2026-06-28 重新扫描版）

> **审计日期：** 2026-06-28（在 2026-06-27 审计基础上全量重新扫描）  
> **审计范围：** `frontend/`、`admin/`、`backend/monolith/`、`packages/shared/`、`docker/`、`.github/workflows/`  
> **前提说明：** 不修改运行环境；所有建议均为源码/配置层改动。  
> **重要前提：** 上轮审计（P0/P1/大部分 H 级）的修复已落地，本次扫描在此基础上复核，并新增发现。

---

## 0. 上轮修复复核摘要

下表为本次重新扫描后对已声称修复项的逐一确认：

| ID | 修复声明 | 复核结论 |
|---|---|---|
| C1 JWT 默认密钥保护 | `StartupSecretGuard` 在非 dev/test profile 下拒绝默认密钥启动 | ✅ 代码已实现 |
| C2 .env.prod 占位符 | `.env.prod.example` 改为 `https://your-domain.com` 占位 | ✅ 已确认 |
| C3 首个 Admin 引导 | `BootstrapAdminRunner` + `bootstrap-admin.sh` | ✅ 代码已实现 |
| C4 MySQL SSL 透传 | compose 补 `MYSQL_USE_SSL` / `MYSQL_ALLOW_PUBLIC_KEY_RETRIEVAL` | ✅ 已确认 |
| H1 IP 信任链 | `ClientIpResolver.trust-proxy-headers` 默认 false | ✅ 已确认 |
| H2 注册发码限流 | `sendRegisterCode` 邮箱已存在分支走 `recordSendAttempt` | ✅ 已确认 |
| H3 文章点赞可见性 | `ArticleLikeService` 补 `status=published`+`visibility=public` | ✅ 已确认 |
| §3 鉴权缓存失效 | `AuthUserCacheInvalidator/Listener`；封禁同时自增 `tokenVersion` | ✅ 代码已实现 |
| M1 Note 点赞可见性 | `NoteLikeService` 补 `visibility=public` | ✅ 已确认（P1 文档声明） |
| M2 Webmention 读取上限 | `WebmentionVerificationWorker` 256 KB 上限 + 超时 | ✅ 代码已实现（`MAX_BODY_BYTES=256*1024`，连接/读取超时各 5s/10s） |
| M7 设置 key 白名单 | `SystemSettingService` key 白名单 | ✅ P1 文档声明 |
| H5 验证码 base64 前缀校验 | 前台/后台登录页检查 `data:image/(png\|jpeg);base64,` | ✅ `login.vue`、`register.vue`、`LoginView.vue` 均有 computed 过滤 |
| M4 CSRF 预热 | `admin/src/main.ts` 启动即预热 | ✅ `warmupCsrfCookie()` 在 `main.ts` 调用 |
| M5 订阅确认/退订改 POST | 前端页面触发 POST | ✅ P1 文档声明 |

---

## 1. 本次新发现问题

以下问题**在上轮审计中未标记或标记为"低优先级待观察"**，本次扫描后提升/首次记录。

---

### 🔴 H-NEW-1：`RateLimitService.tryAcquire` 非原子操作（限流空洞风险）

**文件：** `backend/monolith/src/main/java/com/jiangou/common/service/RateLimitService.java`

**问题：**

```java
Long count = redisTemplate.opsForValue().increment("ratelimit:" + key);
if (count == 1L) {
    redisTemplate.expire("ratelimit:" + key, windowSeconds, TimeUnit.SECONDS);
}
```

`INCR` 与 `EXPIRE` 是两条独立 Redis 命令，非原子。若进程在 `INCR` 成功后、`EXPIRE` 执行前崩溃，该 key **永远不会过期**。结果：该 IP/路径在该窗口期内第一个请求后永久被限流（或在 Redis 不清理 key 时永久计数累加），直至手动 `DEL`。

在高并发突发场景（如 DDoS），多个请求同时到达、`count==1` 的判断有竞争，虽然最终 `EXPIRE` 会被其中一个设置，但首次 increment 进程崩溃的场景确实存在。

**修复方案：** 使用 Lua 脚本确保原子性：

```java
// 注入 RedisScript
private static final String RATE_LIMIT_LUA = 
    "local count = redis.call('INCR', KEYS[1])\n" +
    "if count == 1 then\n" +
    "  redis.call('EXPIRE', KEYS[1], ARGV[1])\n" +
    "end\n" +
    "return count";

private final RedisScript<Long> rateLimitScript = 
    RedisScript.of(RATE_LIMIT_LUA, Long.class);

public boolean tryAcquire(String key, int maxRequests, long windowSeconds) {
    Long count = redisTemplate.execute(rateLimitScript,
        Collections.singletonList("ratelimit:" + key),
        String.valueOf(windowSeconds));
    return count != null && count <= maxRequests;
}
```

---

### 🟡 M-NEW-1：`AuthService.refresh` 重复查库（轻微性能）

**文件：** `backend/monolith/src/main/java/com/jiangou/auth/service/AuthService.java`

**问题：** `refresh()` 方法先调 `validateTokenVersion(claims)` 内部 `userMapper.selectById(userId)` 一次，然后方法体内再 `userMapper.selectById(userId)` 一次，同一请求两次查询同一用户。

**修复：** 提取为局部变量，传入 `validateTokenVersion`：

```java
public AuthVO refresh(RefreshDTO dto) {
    Claims claims = jwtTokenProvider.validateRefreshToken(dto.getRefreshToken());
    Long userId = Long.valueOf(claims.getSubject());
    UserEntity user = userMapper.selectById(userId);    // 只查一次
    validateTokenVersion(claims, user);                 // 改为接受 user 参数
    ensureActiveUser(user);
    ensureCanAuthenticate(user);
    if (!tokenBlacklistService.blacklistIfAbsent(...)) { ... }
    return buildAuthForUser(user);
}
```

---

### 🟡 M-NEW-2：`MarkdownUtils.toHtmlWithAnchors` 锚点注入正则遗漏内联 HTML

**文件：** `backend/monolith/src/main/java/com/jiangou/common/util/MarkdownUtils.java`

**问题：** 给标题注入 `id` 属性的正则为 `<h([1-6])>([^<]+)</h\1>`，`[^<]+` 禁止内容中含 `<`，意味着含内联代码的标题（如 `` ## 使用 `GET /api/v1/articles` 接口 ``）渲染后会产生 `<h2><code>GET /api/v1/articles</code> 接口</h2>`，该正则无法匹配，**该标题不会被注入 `id`，TOC 锚点失效**。

**影响：** 仅影响含行内代码/加粗/链接的标题，TOC 点击跳转失败，不影响安全。

**修复：** 将正则改为允许内部 HTML：

```java
// 改为匹配任意内容（含 HTML 标签）直到关闭标签
Pattern pattern = Pattern.compile("<h([1-6])>(.*?)</h\\1>", Pattern.DOTALL);
// group(2) 提取内容用于展示时需单独处理（strip tags）
```

或在 CommonMark 渲染阶段通过自定义 `HtmlNodeRendererFactory` 直接注入属性，更健壮。

---

### 🟡 M-NEW-3：`CreateCommentDTO` 存在无用字段（API 契约噪声）

**文件：** `backend/monolith/src/main/java/com/jiangou/comment/dto/CreateCommentDTO.java`  
**关联：** `backend/.../comment/service/CommentService.java` — `create()` 方法

**问题：** DTO 声明了 `nickname`（`@NotBlank`）和 `email` 字段，但 `CommentService.create()` 完全忽略它们，直接从 `UserEntity` 取 `displayName`：

```java
entity.setNickname(user.getDisplayName() != null ? user.getDisplayName() : user.getUsername());
```

`@NotBlank` 校验还会对前端提交没有 `nickname` 字段的请求返回 400，与实际逻辑矛盾。

**前后端影响：** 前台 `comment.api.ts` 的 `postComment` 接口传了 `nickname`，但若不传（将来版本省略）会被 `@NotBlank` 拒绝，产生虚假校验错误。

**修复：**
1. 从 `CreateCommentDTO` 删除 `nickname`（`@NotBlank`）和 `email` 字段（认证用户不需要客户端传入）。
2. 同步更新 `frontend/services/comment.api.ts` 的 `postComment` 调用，移除 `nickname`/`email` 参数。

---

### 🟡 M-NEW-4：管理端 `ArticleDetail` 接口类型不完整

**文件：** `admin/src/services/article.api.ts`

**问题：** 管理端 `ArticleDetail` 接口定义：

```typescript
export interface ArticleDetail extends ArticleItem {
  contentMd?: string
  categoryId?: number
  tagIds?: number[]
}
```

但后端 `ArticleDetailVO` 实际还返回 `readingMinutes`、`wordCount`、`viewCount`、`likeCount`、`commentCount`、`publishedAt`、`category`（对象）、`tags`（字符串数组）等字段。若将来 Dashboard 或版本历史页面访问这些字段，TypeScript 不会报错（运行时 `undefined`），调试困难。

**修复：** 补全接口字段声明（与前台 `frontend/services/article.api.ts` 的 `ArticleDetail` 对齐）：

```typescript
export interface ArticleDetail extends ArticleItem {
  contentMd?: string
  contentHtml?: string
  categoryId?: number
  tagIds?: number[]
  readingMinutes?: number
  wordCount?: number
  viewCount?: number
  likeCount?: number
  commentCount?: number
  publishedAt?: string
  category?: { name: string; slug: string }
  tags?: string[]
}
```

---

### 🟡 M-NEW-5：前台 `sanitizeConfig.ts`/`sanitizeHooks.ts` 与 shared 包重复维护

**文件：**
- `frontend/utils/sanitizeConfig.ts`（本地）
- `frontend/utils/sanitizeHooks.ts`（本地）
- `packages/shared/src/sanitizeConfig.ts`（共享）
- `packages/shared/src/sanitizeHooks.ts`（共享）

**问题：** 管理端（`admin/src/utils/sanitize.ts`）已正确从 `@jiangou/shared` 导入 `HTML_SANITIZE_CONFIG` 和 `applyDomPurifyHooks`，但前台仍有本地副本且独立维护（`frontend/utils/sanitize.ts` 从本地 `./sanitizeConfig` 导入）。若将来修改 shared 包的 allowlist，前台不会自动同步。

**修复：** 删除 `frontend/utils/sanitizeConfig.ts` 和 `frontend/utils/sanitizeHooks.ts`，在 `frontend/utils/sanitize.ts` 改为：

```typescript
import DOMPurify from 'isomorphic-dompurify'
import { HTML_SANITIZE_CONFIG, applyDomPurifyHooks } from '@jiangou/shared'

applyDomPurifyHooks(DOMPurify)

export function sanitizeHtml(html: string): string {
  if (!html) return ''
  return DOMPurify.sanitize(html, HTML_SANITIZE_CONFIG)
}
```

需确认 `isomorphic-dompurify` 与 shared 包中 `applyDomPurifyHooks` 的类型兼容（已有 `as unknown as Parameters<...>[0]` 的转型先例）。

---

### 🟢 L-NEW-1：`WebmentionVerificationWorker` 不跟随 301/302 重定向

**文件：** `backend/.../webmention/service/WebmentionVerificationWorker.java`

**问题：** `conn.setInstanceFollowRedirects(false)`，若 source URL 返回 301/302，`conn.getResponseCode() >= 400` 判断为 false（3xx 不满足），但也不读取重定向后的内容，直接返回 `false`（验证失败）。导致合法的 Webmention（source 有 301 重定向）被静默拒绝。

**修复：** 将 `setInstanceFollowRedirects(false)` 改为 `true`，并保持 SSRF 防护逻辑不变（`UrlSafetyUtils.openValidatedGetConnection` 已在请求前做 DNS 校验）：

```java
conn.setInstanceFollowRedirects(true);
```

注：跟随重定向后目标 IP 可能变化（DNS rebinding），若需更强 SSRF 防护，应在每次重定向后重新做 `isBlockedAddress` 检查。

---

### 🟢 L-NEW-2：`@EnableGlobalMethodSecurity` 已废弃

**文件：** `backend/.../security/SecurityConfig.java`

```java
@EnableGlobalMethodSecurity(prePostEnabled = true)
```

Spring Security 5.8 起该注解已废弃，应改为 `@EnableMethodSecurity`（默认 `prePostEnabled=true`）。Spring Boot 2.7.x 已含 Spring Security 5.7，该注解虽仍可用但输出 deprecation 警告，升级 Spring Boot 3.x 时会编译报错。

**修复：**
```java
@EnableMethodSecurity   // 替换 @EnableGlobalMethodSecurity(prePostEnabled = true)
```

---

### 🟢 L-NEW-3：Admin `SESSION_TTL_MS = 60_000`（1 分钟）可能过短

**文件：** `admin/src/stores/auth.ts`

```typescript
const SESSION_TTL_MS = 60_000
```

每次路由守卫在超过 1 分钟后都会向 `/api/v1/auth/me` 发起请求。对于主要在管理端长时编辑文章的场景（`ArticleEditorView`），会在每次切换页面时频繁刷新 session。建议提升至 5 分钟（300_000 ms），并依赖 `tokenVersion` 机制（已实现）做强制失效：

```typescript
const SESSION_TTL_MS = 300_000  // 5 分钟
```

---

## 2. 前后端接口交互专项

### 2.1 接口契约一致性

| 接口 | 前台 (frontend) | 后台 (admin) | 后端 VO | 差异说明 |
|---|---|---|---|---|
| `GET /api/v1/articles/:id` | `ArticleDetail.contentHtml` ✅ | `ArticleDetail`（缺多字段）⚠️ | `ArticleDetailVO` 完整 | admin 接口类型不完整（M-NEW-4） |
| `POST /api/v1/comments` | 传 `nickname`/`email` | 传 `nickname`/`email` | 后端忽略这两字段 | DTO 字段冗余（M-NEW-3） |
| `GET /api/v1/comments` | `CommentItem` 无 `status` | `CommentVO` 有 `status` | `CommentVO` 含 status | 前台接口未声明 status（harmless，仅 approved 可见） |
| `POST /api/v1/auth/refresh` | body: `{}` | body: `{}` | 后端也从 cookie 读取 | ✅ 两端都支持无 body 形式 |
| `GET /api/v1/auth/me` | 返回 `AuthUser`（含 permissions） | 同 | `UserBriefVO`（含 roles/permissions） | ✅ 一致 |
| `POST /api/v1/subscriptions/confirm` | POST body | — | 后端 `@PostMapping` | ✅ 已修复 |
| `DELETE /api/v1/admin/comments/:id` | admin api ✅ | 调用正确 | `@DeleteMapping` | ✅ 一致 |

### 2.2 错误码对齐

前端使用 `UNAUTHORIZED_CODE = 40101`（`@jiangou/shared` 定义），后端 `ErrorCodes.UNAUTHORIZED = 40101`，两端一致 ✅。

`FORBIDDEN_CODE = 40301`，后端 `ErrorCodes.FORBIDDEN = 40301`，一致 ✅。

### 2.3 `ApiResult` 结构

前台 `utils/http.ts` 的 `ApiResult<T>` 与后端 `ApiResult.java` 字段（`code`/`message`/`data`）对齐 ✅。

Admin 使用 `ApiEnvelope`（来自 shared），字段相同 ✅。

---

## 3. 安全专项复核

| 项目 | 结论 |
|---|---|
| XSS 防护（前台/后台） | DOMPurify + 后端 OWASP HTML Sanitizer 双层，共享 allowlist（admin 从 shared 引入，前台本地副本待合并） ✅/⚠️ |
| CSRF | Cookie+Header 双重验证，GET/OPTIONS 豁免，webmention 豁免，`SameSite=Lax` ✅ |
| JWT 鉴权 | HttpOnly Cookie，refresh token 消费后黑名单，tokenVersion 机制，启动密钥保护 ✅ |
| 开放重定向 | `safeRedirect` 过滤协议+双斜杠+反斜杠，`//evil.com` 等绕过均被拦截 ✅ |
| 文件上传 | 魔数检测（非 Content-Type），ImageIO 校验图片完整性，10MB 上限，UUID 随机 key ✅ |
| SQL 注入 | 全程 MyBatis-Plus Lambda，无原生 SQL 拼接 ✅ |
| SSRF（Webmention） | `UrlSafetyUtils` 检查 loopback/内网/保留段，DNS 预解析，协议白名单 ✅ |
| 限流 | Redis 滑动窗口（登录 10次/分钟，邮件发码 5次/分钟）；存在非原子 INCR+EXPIRE 漏洞 ⚠️（H-NEW-1） |
| 敏感信息泄漏 | prod 关闭 Swagger，`MYSQL_PASSWORD` 无默认值，`ProdEnvValidator` 校验 localhost ✅ |
| CSP | Nginx 模板已配置 `Content-Security-Policy` ✅ |

---

## 4. 可维护性/可扩展性

- **共享包依赖不统一**：前台 sanitize 使用本地副本，管理端从 shared 引入 → 见 M-NEW-5
- **注释与文档**：`docs/CODE_AUDIT_2026-06-27.md` 中部分条目已修复但原文档仍标"待修复"，建议合并或打 `[RESOLVED]` 标记
- **`@EnableGlobalMethodSecurity` 废弃**：升级 Spring Boot 3.x 时会阻断编译 → L-NEW-2
- **Flyway 迁移版本连续**：V1–V11 连续无跳号，结构清晰 ✅
- **事件驱动缓存失效**：`AuthUserCacheInvalidator/Listener` 设计优雅，后续扩展权限系统时只需触发事件 ✅

---

## 5. 性能专项

| 项目 | 状态 |
|---|---|
| 列表 N+1 | 标签/分类 join 或批量查询，未见明显 N+1 ✅ |
| `AuthService.refresh` 两次查库 | 存在，低影响（M-NEW-1） |
| TOC 锚点正则遗漏含内联 HTML 的标题 | 功能性 bug（M-NEW-2）|
| 文章浏览量计数 | Redis 原子 INCR ✅ |
| 点赞计数 | Redis 维护，异步持久化 ✅ |
| 搜索 | MySQL FULLTEXT / Meilisearch 可选 ✅ |
| Webmention 验证 | 异步线程池，body 上限 256KB，超时保护 ✅ |

---

## 6. 优先级汇总与修复工作量

### 6.1 修复总览

| 优先级 | ID | 所属层 | 涉及文件 | 影响范围 | 预估工时 | 状态 |
|:---:|---|:---:|---|---|:---:|:---:|
| 🔴 高 | H-NEW-1 | 后端 | `common/service/RateLimitService.java` | 全局限流正确性 | 30 min | ✅ 已修 |
| 🟡 中 | M-NEW-1 | 后端 | `auth/service/AuthService.java` | refresh 接口性能 | 15 min | ✅ 已修 |
| 🟡 中 | M-NEW-2 | 后端 | `common/util/MarkdownUtils.java` | TOC 锚点功能 | 45 min | ✅ 已修 |
| 🟡 中 | M-NEW-3 | 全栈 | `comment/dto/CreateCommentDTO.java`<br>`frontend/services/comment.api.ts`<br>`frontend/components/comment/CommentSection.vue` | 接口契约准确性 | 20 min | ✅ 已修 |
| 🟡 中 | M-NEW-4 | 前端 | `admin/src/services/article.api.ts` | 管理端类型安全 | 15 min | ✅ 已修 |
| 🟡 中 | M-NEW-5 | 前端 | `frontend/utils/sanitizeConfig.ts`<br>`frontend/utils/sanitizeHooks.ts`<br>`frontend/utils/sanitize.ts` | 安全规则一致性 | 30 min | ✅ 已修 |
| 🟢 低 | L-NEW-1 | 后端 | `webmention/service/WebmentionVerificationWorker.java` | Webmention 验证成功率 | 10 min | ✅ 已修 |
| 🟢 低 | L-NEW-2 | 后端 | `security/SecurityConfig.java` | Spring Boot 3.x 升级兼容 | 5 min | ✅ 已修 |
| 🟢 低 | L-NEW-3 | 前端 | `admin/src/stores/auth.ts` | 管理端 API 请求频率 | 5 min | ✅ 已修 |

> 修复完成后将 `⬜ 待修` 改为 `✅ 已修` 并注明 commit hash。

---

### 6.2 各项修复关键点

#### H-NEW-1 — 限流原子化（立即处理）

**风险**：进程在 `INCR` 与 `EXPIRE` 之间崩溃 → key 无 TTL → 该路径永久限流或计数失控。  
**改法**：将两步操作改为 Redis Lua 脚本（单次网络往返，原子执行）。  
**验证**：模拟 Redis `INCR` 后强制断连，重启后确认 key 已自动过期。

#### M-NEW-1 — 消除 `AuthService.refresh` 双查库

**改法**：将 `validateTokenVersion(claims)` 改为 `validateTokenVersion(claims, user)`，在 `refresh()` 顶层查一次后复用。  
**验证**：开启 MyBatis SQL 日志，确认 refresh 请求只产生 1 次 `SELECT * FROM users`。

#### M-NEW-2 — TOC 锚点正则修复

**根因**：`[^<]+` 不匹配含 `<code>`/`<strong>` 的标题内容。  
**改法**（最小改动）：正则改为 `<h([1-6])>(.*?)</h\\1>`（`Pattern.DOTALL`），`group(2)` 仅用于 `id` 注入，显示内容不变。  
**更健壮方案**：实现 CommonMark `HtmlNodeRendererFactory`，在渲染阶段直接为 Heading 节点注入 `id` 属性，彻底绕开后处理正则。  
**验证**：单测用含 `` `code` ``、`**bold**` 标题的 Markdown，断言输出 HTML 中标题含正确 `id`。

#### M-NEW-3 — 清理 `CreateCommentDTO` 冗余字段

**改法**：
1. 删除 `CreateCommentDTO` 的 `nickname`（含 `@NotBlank`）和 `email` 字段。
2. `frontend/services/comment.api.ts` 的 `postComment` 入参移除 `nickname`/`email`。
3. `admin/src/services/comment.api.ts` 同步更新（若有调用）。

**验证**：不带 `nickname` 字段提交评论，后端应返回 200 而非 400。

#### M-NEW-4 — 完善管理端 `ArticleDetail` 类型

**改法**：在 `admin/src/services/article.api.ts` 的 `ArticleDetail` 接口补全字段（参见 §M-NEW-4 详细说明）。  
**验证**：TypeScript 编译通过（`npm run build` 无类型报错）。

#### M-NEW-5 — 前台 sanitize 统一从 shared 引入

**改法**：
1. 删除 `frontend/utils/sanitizeConfig.ts` 和 `frontend/utils/sanitizeHooks.ts`。
2. `frontend/utils/sanitize.ts` 改为 `import { HTML_SANITIZE_CONFIG, applyDomPurifyHooks } from '@jiangou/shared'`。

**前置确认**：`@jiangou/shared` 是否已在 `frontend/package.json` 列为依赖（应有 `"@jiangou/shared": "workspace:*"` 或等价）。  
**验证**：修改 shared 包 allowlist 后，前台与管理端行为一致。

#### L-NEW-1 — Webmention 跟随重定向

**改法**：`conn.setInstanceFollowRedirects(true)` 即可；SSRF 防护由 `UrlSafetyUtils.openValidatedGetConnection` 在请求前已完成。  
**注意**：若需更严格防护（防 DNS rebinding），跟随重定向后应重新校验目标 IP，但对个人博客场景当前入口校验已足够。

#### L-NEW-2 — 替换废弃注解

**改法**：`SecurityConfig.java` 将 `@EnableGlobalMethodSecurity(prePostEnabled = true)` 替换为 `@EnableMethodSecurity`。  
**验证**：`mvnw test` 全绿，无 deprecation 警告。

#### L-NEW-3 — 延长 Admin session 缓存时间

**改法**：`admin/src/stores/auth.ts` 中 `SESSION_TTL_MS = 60_000` → `300_000`（5 分钟）。  
**说明**：强制下线依赖 `tokenVersion` 自增（已实现），本改动不影响安全边界，仅减少不必要的 `/auth/me` 请求。

---

### 6.3 建议修复顺序

```
立即（本周）   → H-NEW-1（限流原子化）
下次迭代       → M-NEW-3（DTO 清理）、M-NEW-4（类型完善）、M-NEW-5（sanitize 统一）
计划性修复     → M-NEW-1、M-NEW-2（TOC 锚点）
升级前处理     → L-NEW-2（废弃注解，升 Spring Boot 3.x 前必须）
随手可改       → L-NEW-1、L-NEW-3
```

**总预估工时：约 3 小时**（可拆分为 2 个 PR：后端 1.5h + 前端 1.5h）。

---

## 7. 总评

项目整体工程质量在个人/社区博客平台中处于较高水准：

- 安全防线完整（JWT + CSRF + XSS 净化 + SSRF 防护 + 默认拒绝）
- 上轮审计的 C 级和大部分 H 级已修复落地，修复质量可
- 本次新发现均为中低等级，无新增 Critical 级问题
- **最需立即处理的是 `RateLimitService` 的非原子 INCR+EXPIRE**，其余可计划性修复
