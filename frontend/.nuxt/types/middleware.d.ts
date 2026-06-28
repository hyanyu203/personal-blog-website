import type { NavigationGuard } from 'vue-router'
export type MiddlewareKey = "guest-only"
declare module "C:/Users/Lenovo/Documents/博客网站平台/frontend/node_modules/nuxt/dist/pages/runtime/composables" {
  interface PageMeta {
    middleware?: MiddlewareKey | NavigationGuard | Array<MiddlewareKey | NavigationGuard>
  }
}