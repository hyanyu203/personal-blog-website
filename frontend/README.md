# 渐构前台（Frontend）

访客-facing 站点，基于 **Nuxt 3.5.3 + Vue 3 + TypeScript**，支持 SSR 以优化 SEO 与首屏性能。

## 技术栈

| 类别 | 选型 |
|---|---|
| 框架 | Nuxt 3.5.3、Vue 3 Composition API |
| 语言 | TypeScript（strict） |
| 样式 | 全局 CSS Variables（`assets/css/main.css`） |
| 状态 | Nuxt `useState` + `useAsyncData` + composables |
| HTTP | 原生 `fetch`（`utils/http.ts`），HttpOnly Cookie 会话 |
| 安全 | isomorphic-dompurify（`SafeHtml` 组件）、CSRF 双提交 |

## 目录结构

```text
frontend/
├── app.vue
├── nuxt.config.ts
├── pages/                     # 文件路由
├── components/
├── composables/               # useAuth、useTheme 等
├── services/                  # *.api.ts
├── utils/                     # http、csrf、sanitize、safeUrl
├── plugins/                   # auth.client.ts、csrf.client.ts
├── assets/
└── public/
```

## 环境变量

```env
NUXT_PUBLIC_API_BASE=/api/v1
NUXT_PUBLIC_SITE_URL=http://localhost:3000
NUXT_API_BASE_INTERNAL=http://localhost:8080/api/v1   # SSR 生产必填
```

## 开发

```bash
npm install
npm run dev       # http://localhost:3000
npm run build
npm run preview
npm run typecheck # 需先 nuxt prepare
```

## API 调用

所有请求通过 `utils/http.ts` 的 `apiFetch` 封装，Base URL 为 `NUXT_PUBLIC_API_BASE`。  
接口定义见 [docs/API.md](../docs/API.md)。

## 后端对接

- **单体**：`backend/monolith`，API `http://localhost:8080/api/v1`
- 开发时在 `nuxt.config.ts` 配置 proxy 转发 `/api/v1`
