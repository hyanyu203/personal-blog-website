import { _ as __nuxt_component_0 } from "./ArticleCard-a7d9553a.js";
import { e as useRoute, b as useAsyncData, d as __nuxt_component_0$1, _ as _export_sfc } from "../server.mjs";
import { defineComponent, computed, withAsyncContext, unref, withCtx, createTextVNode, useSSRContext } from "vue";
import "hookable";
import { u as useSeoMeta } from "./index-95b6f593.js";
import "destr";
import "devalue";
import "klona";
import { ssrRenderAttrs, ssrInterpolate, ssrRenderList, ssrRenderComponent } from "vue/server-renderer";
import { f as fetchArticles } from "./article.api-3cf8cb60.js";
import { u as userFacingError } from "./userErrorMessage-f95eddb5.js";
import "ofetch";
import "#internal/nitro";
import "unctx";
import "h3";
import "@unhead/ssr";
import "unhead";
import "@unhead/shared";
import "vue-router";
import "ufo";
import "defu";
const _sfc_main = /* @__PURE__ */ defineComponent({
  __name: "index",
  __ssrInlineRender: true,
  async setup(__props) {
    let __temp, __restore;
    const route = useRoute();
    const page = computed(() => Number(route.query.page || 1));
    const { data, pending, error } = ([__temp, __restore] = withAsyncContext(() => useAsyncData(
      () => `posts-${page.value}`,
      () => fetchArticles(page.value, 20),
      { watch: [page] }
    )), __temp = await __temp, __restore(), __temp);
    useSeoMeta({ title: "文章 - 渐构" });
    return (_ctx, _push, _parent, _attrs) => {
      var _a;
      const _component_ArticleCard = __nuxt_component_0;
      const _component_NuxtLink = __nuxt_component_0$1;
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-d8908ca8><h1 data-v-d8908ca8>文章</h1>`);
      if (unref(pending)) {
        _push(`<div data-v-d8908ca8>加载中…</div>`);
      } else if (unref(error)) {
        _push(`<div data-v-d8908ca8>${ssrInterpolate(unref(userFacingError)(unref(error)))}</div>`);
      } else {
        _push(`<!--[--><!--[-->`);
        ssrRenderList((_a = unref(data)) == null ? void 0 : _a.items, (item) => {
          _push(ssrRenderComponent(_component_ArticleCard, {
            key: item.id,
            article: item
          }, null, _parent));
        });
        _push(`<!--]-->`);
        if (unref(data) && (unref(data).hasMore || unref(page) > 1)) {
          _push(`<nav class="pagination" data-v-d8908ca8>`);
          if (unref(page) > 1) {
            _push(ssrRenderComponent(_component_NuxtLink, {
              to: `/posts?page=${unref(page) - 1}`
            }, {
              default: withCtx((_, _push2, _parent2, _scopeId) => {
                if (_push2) {
                  _push2(`上一页`);
                } else {
                  return [
                    createTextVNode("上一页")
                  ];
                }
              }),
              _: 1
            }, _parent));
          } else {
            _push(`<!---->`);
          }
          _push(`<span data-v-d8908ca8>第 ${ssrInterpolate(unref(page))} 页</span>`);
          if (unref(data).hasMore) {
            _push(ssrRenderComponent(_component_NuxtLink, {
              to: `/posts?page=${unref(page) + 1}`
            }, {
              default: withCtx((_, _push2, _parent2, _scopeId) => {
                if (_push2) {
                  _push2(`下一页`);
                } else {
                  return [
                    createTextVNode("下一页")
                  ];
                }
              }),
              _: 1
            }, _parent));
          } else {
            _push(`<!---->`);
          }
          _push(`</nav>`);
        } else {
          _push(`<!---->`);
        }
        _push(`<!--]-->`);
      }
      _push(`</div>`);
    };
  }
});
const index_vue_vue_type_style_index_0_scoped_d8908ca8_lang = "";
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/posts/index.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const index = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-d8908ca8"]]);
export {
  index as default
};
//# sourceMappingURL=index-645a3321.js.map
