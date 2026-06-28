import { ComputedRef, Ref } from 'vue'
export type LayoutKey = string
declare module "C:/Users/Lenovo/Documents/博客网站平台/frontend/node_modules/nuxt/dist/pages/runtime/composables" {
  interface PageMeta {
    layout?: false | LayoutKey | Ref<LayoutKey> | ComputedRef<LayoutKey>
  }
}