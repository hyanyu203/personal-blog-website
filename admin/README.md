# 渐构后台 CMS（Admin）

面向管理员的内容管理系统，基于 **Vue 3 + Vite + TypeScript + Pinia** 单页应用。

## 技术栈

| 类别 | 选型 |
|---|---|
| 框架 | Vue 3 Composition API |
| 构建 | Vite 4 |
| 状态 | Pinia |
| HTTP | Axios（`withCredentials` + CSRF 双提交） |
| 共享库 | `@jiangou/shared`（类型、权限、XSS 工具） |

## 目录结构

```text
admin/
├── index.html
├── vite.config.ts
├── src/
│   ├── main.ts
│   ├── router/
│   ├── layouts/AdminLayout.vue
│   ├── views/                 # 17 个管理页面
│   ├── stores/auth.ts
│   ├── services/*.api.ts
│   └── utils/                 # 薄封装，核心逻辑在 @jiangou/shared
└── package.json
```

## 开发与构建

```bash
# 首次或 shared 变更后
npm install --prefix ../packages/shared
npm install
npm run dev       # http://localhost:5173/admin/
npm run typecheck # vue-tsc 静态检查
npm run build     # 输出 dist/，由 Nginx 托管于 /admin/
```

本地 API 通过 Vite 代理转发至 `http://localhost:8080/api/v1`（见 `vite.config.ts`）。

## 路由

| 路由 | 说明 |
|---|---|
| `/admin/login` | 登录 |
| `/admin/` | 仪表盘 |
| `/admin/articles` | 文章管理（含新建/编辑） |
| `/admin/snippets` | 代码片段 |
| `/admin/notes` | 碎碎念 |
| `/admin/projects` | GitHub 项目 |
| `/admin/comments` | 评论审核 |
| `/admin/users` | 用户管理 |
| `/admin/settings` | 系统设置 |
| … | 分类、标签、媒体库、友链、订阅、Webmention |

## 认证

1. `POST /api/v1/auth/login` — HttpOnly Cookie 会话
2. Axios 拦截器在 `40101` 时自动 `POST /auth/refresh`
3. `/auth/me` 返回 `roles` 与 `permissions`

## 权限模型

与后端 `AdminAccessEvaluator` 对齐：

| 角色/权限 | 后台访问 |
|---|---|
| `ADMIN` 角色 | 全部菜单与 API |
| 细粒度权限（如 `comment:review`、`article:create`） | 对应菜单 + 后端 `@PreAuthorize` 控制的 API |

侧栏与路由守卫使用 `@jiangou/shared` 中的 `ADMIN_NAV_ITEMS`、`canAccessAdmin`、`canAccessAdminRoute`。

当前实现的角色代码：`USER`、`ADMIN`（无 `EDITOR` / `SUPER_ADMIN`）。

## 部署

生产构建为静态资源，由 Nginx 挂载：

```nginx
location /admin/ {
  root /usr/share/nginx/html;
  try_files $uri $uri/ /admin/index.html;
}
```

完整栈见 [docker/README.md](../docker/README.md)。

## 相关文档

- [docs/API.md](../docs/API.md) — REST API
- [docs/PRODUCTION_READINESS.md](../docs/PRODUCTION_READINESS.md) — 上线清单
- [packages/shared](../packages/shared/) — 前后台共享 TypeScript 模块
