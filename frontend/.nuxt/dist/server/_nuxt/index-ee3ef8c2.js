import { _ as __nuxt_component_0 } from "./ArticleCard-a7d9553a.js";
import { a as apiFetch, b as useAsyncData, d as __nuxt_component_0$1, _ as _export_sfc } from "../server.mjs";
import { defineComponent, withAsyncContext, computed, mergeProps, unref, withCtx, createTextVNode, toDisplayString, useSSRContext } from "vue";
import "hookable";
import "destr";
import "devalue";
import "klona";
import { u as usePageSeo } from "./usePageSeo-74338983.js";
import { ssrRenderAttrs, ssrInterpolate, ssrRenderList, ssrRenderComponent } from "vue/server-renderer";
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
import "./index-95b6f593.js";
function fetchHome() {
  return apiFetch("/home");
}
const _sfc_main = /* @__PURE__ */ defineComponent({
  __name: "index",
  __ssrInlineRender: true,
  async setup(__props) {
    var _a, _b;
    let __temp, __restore;
    const { data: home, pending, error } = ([__temp, __restore] = withAsyncContext(() => useAsyncData("home", () => fetchHome())), __temp = await __temp, __restore(), __temp);
    const articles = computed(() => {
      var _a2, _b2;
      return ((_b2 = (_a2 = home.value) == null ? void 0 : _a2.articles) == null ? void 0 : _b2.items) ?? [];
    });
    const stats = computed(() => {
      var _a2;
      return (_a2 = home.value) == null ? void 0 : _a2.stats;
    });
    const settings = computed(() => {
      var _a2;
      return (_a2 = home.value) == null ? void 0 : _a2.settings;
    });
    const categories = computed(() => {
      var _a2;
      return ((_a2 = home.value) == null ? void 0 : _a2.categories) ?? [];
    });
    const tags = computed(() => {
      var _a2;
      return ((_a2 = home.value) == null ? void 0 : _a2.tags) ?? [];
    });
    const loadErrorMessage = computed(() => userFacingError(error.value));
    usePageSeo({
      title: ((_a = settings.value) == null ? void 0 : _a.siteTitle) || "渐构",
      description: ((_b = settings.value) == null ? void 0 : _b.siteDescription) || "渐次构建，理解计算机世界",
      path: "/"
    });
    return (_ctx, _push, _parent, _attrs) => {
      var _a2, _b2, _c, _d;
      const _component_ArticleCard = __nuxt_component_0;
      const _component_NuxtLink = __nuxt_component_0$1;
      _push(`<div${ssrRenderAttrs(mergeProps({ class: "home" }, _attrs))} data-v-9e75a158><section class="hero" data-v-9e75a158><h1 data-v-9e75a158>渐构</h1><p class="tagline" data-v-9e75a158>${ssrInterpolate(((_a2 = unref(settings)) == null ? void 0 : _a2.siteSubtitle) || "渐次构建，理解计算机世界")}</p>`);
      if (unref(stats)) {
        _push(`<p class="stats" data-v-9e75a158>${ssrInterpolate(unref(stats).articleCount)} 篇文章 · ${ssrInterpolate(unref(stats).snippetCount)} 代码片段 · ${ssrInterpolate(unref(stats).noteCount)} 碎碎念 · 运行 ${ssrInterpolate(unref(stats).runningDays)} 天 </p>`);
      } else {
        _push(`<!---->`);
      }
      _push(`</section><div class="grid" data-v-9e75a158><section class="main-col" data-v-9e75a158><h2 data-v-9e75a158>最近文章</h2>`);
      if (unref(pending)) {
        _push(`<div data-v-9e75a158>加载中…</div>`);
      } else if (unref(error)) {
        _push(`<div data-v-9e75a158>${ssrInterpolate(unref(loadErrorMessage))}</div>`);
      } else {
        _push(`<!--[--><!--[-->`);
        ssrRenderList(unref(articles), (item) => {
          _push(ssrRenderComponent(_component_ArticleCard, {
            key: item.id,
            article: item
          }, null, _parent));
        });
        _push(`<!--]-->`);
        if (!((_b2 = unref(articles)) == null ? void 0 : _b2.length)) {
          _push(`<p class="empty" data-v-9e75a158>暂无文章</p>`);
        } else {
          _push(`<!---->`);
        }
        _push(`<!--]-->`);
      }
      _push(`</section><aside class="sidebar" data-v-9e75a158>`);
      if ((_c = unref(categories)) == null ? void 0 : _c.length) {
        _push(`<div class="side-block" data-v-9e75a158><h3 data-v-9e75a158>分类</h3><ul data-v-9e75a158><!--[-->`);
        ssrRenderList(unref(categories), (c) => {
          _push(`<li data-v-9e75a158>`);
          _push(ssrRenderComponent(_component_NuxtLink, {
            to: `/categories/${c.slug}`
          }, {
            default: withCtx((_, _push2, _parent2, _scopeId) => {
              if (_push2) {
                _push2(`${ssrInterpolate(c.name)}`);
              } else {
                return [
                  createTextVNode(toDisplayString(c.name), 1)
                ];
              }
            }),
            _: 2
          }, _parent));
          _push(`</li>`);
        });
        _push(`<!--]--></ul></div>`);
      } else {
        _push(`<!---->`);
      }
      if ((_d = unref(tags)) == null ? void 0 : _d.length) {
        _push(`<div class="side-block" data-v-9e75a158><h3 data-v-9e75a158>标签</h3><div class="tag-cloud" data-v-9e75a158><!--[-->`);
        ssrRenderList(unref(tags).slice(0, 20), (t) => {
          _push(ssrRenderComponent(_component_NuxtLink, {
            key: t.id,
            to: `/tags/${t.slug}`,
            class: "tag"
          }, {
            default: withCtx((_, _push2, _parent2, _scopeId) => {
              if (_push2) {
                _push2(`${ssrInterpolate(t.name)}`);
              } else {
                return [
                  createTextVNode(toDisplayString(t.name), 1)
                ];
              }
            }),
            _: 2
          }, _parent));
        });
        _push(`<!--]--></div></div>`);
      } else {
        _push(`<!---->`);
      }
      _push(`<div class="side-block" data-v-9e75a158><h3 data-v-9e75a158>订阅</h3><p data-v-9e75a158>`);
      _push(ssrRenderComponent(_component_NuxtLink, { to: "/subscribe" }, {
        default: withCtx((_, _push2, _parent2, _scopeId) => {
          if (_push2) {
            _push2(`邮件订阅`);
          } else {
            return [
              createTextVNode("邮件订阅")
            ];
          }
        }),
        _: 1
      }, _parent));
      _push(`</p><p data-v-9e75a158><a href="/api/v1/rss/feed.xml" target="_blank" rel="noopener" data-v-9e75a158>RSS Feed</a></p></div></aside></div></div>`);
    };
  }
});
const index_vue_vue_type_style_index_0_scoped_9e75a158_lang = "";
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/index.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const index = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-9e75a158"]]);
export {
  index as default
};
//# sourceMappingURL=index-ee3ef8c2.js.map
