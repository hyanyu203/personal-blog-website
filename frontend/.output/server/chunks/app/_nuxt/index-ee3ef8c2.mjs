import { _ as __nuxt_component_0 } from './ArticleCard-a7d9553a.mjs';
import { _ as _export_sfc, b as useAsyncData, a as apiFetch, d as __nuxt_component_0$1 } from '../server.mjs';
import { useSSRContext, defineComponent, withAsyncContext, computed, mergeProps, unref, withCtx, createTextVNode, toDisplayString } from 'vue';
import { u as usePageSeo } from './usePageSeo-74338983.mjs';
import { ssrRenderAttrs, ssrInterpolate, ssrRenderList, ssrRenderComponent } from 'vue/server-renderer';
import { u as userFacingError } from './userErrorMessage-f95eddb5.mjs';
import 'ofetch';
import 'hookable';
import 'unctx';
import 'h3';
import '@unhead/ssr';
import 'unhead';
import '@unhead/shared';
import 'vue-router';
import 'ufo';
import 'defu';
import '../../nitro/node-server.mjs';
import 'node-fetch-native/polyfill';
import 'node:http';
import 'node:https';
import 'destr';
import 'unenv/runtime/fetch/index';
import 'scule';
import 'klona';
import 'ohash';
import 'unstorage';
import 'radix3';
import 'node:fs';
import 'node:url';
import 'pathe';
import './index-95b6f593.mjs';

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
      var _a3;
      var _a2, _b2;
      return (_a3 = (_b2 = (_a2 = home.value) == null ? void 0 : _a2.articles) == null ? void 0 : _b2.items) != null ? _a3 : [];
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
      var _a3;
      var _a2;
      return (_a3 = (_a2 = home.value) == null ? void 0 : _a2.categories) != null ? _a3 : [];
    });
    const tags = computed(() => {
      var _a3;
      var _a2;
      return (_a3 = (_a2 = home.value) == null ? void 0 : _a2.tags) != null ? _a3 : [];
    });
    const loadErrorMessage = computed(() => userFacingError(error.value));
    usePageSeo({
      title: ((_a = settings.value) == null ? void 0 : _a.siteTitle) || "\u6E10\u6784",
      description: ((_b = settings.value) == null ? void 0 : _b.siteDescription) || "\u6E10\u6B21\u6784\u5EFA\uFF0C\u7406\u89E3\u8BA1\u7B97\u673A\u4E16\u754C",
      path: "/"
    });
    return (_ctx, _push, _parent, _attrs) => {
      var _a2, _b2, _c, _d;
      const _component_ArticleCard = __nuxt_component_0;
      const _component_NuxtLink = __nuxt_component_0$1;
      _push(`<div${ssrRenderAttrs(mergeProps({ class: "home" }, _attrs))} data-v-9e75a158><section class="hero" data-v-9e75a158><h1 data-v-9e75a158>\u6E10\u6784</h1><p class="tagline" data-v-9e75a158>${ssrInterpolate(((_a2 = unref(settings)) == null ? void 0 : _a2.siteSubtitle) || "\u6E10\u6B21\u6784\u5EFA\uFF0C\u7406\u89E3\u8BA1\u7B97\u673A\u4E16\u754C")}</p>`);
      if (unref(stats)) {
        _push(`<p class="stats" data-v-9e75a158>${ssrInterpolate(unref(stats).articleCount)} \u7BC7\u6587\u7AE0 \xB7 ${ssrInterpolate(unref(stats).snippetCount)} \u4EE3\u7801\u7247\u6BB5 \xB7 ${ssrInterpolate(unref(stats).noteCount)} \u788E\u788E\u5FF5 \xB7 \u8FD0\u884C ${ssrInterpolate(unref(stats).runningDays)} \u5929 </p>`);
      } else {
        _push(`<!---->`);
      }
      _push(`</section><div class="grid" data-v-9e75a158><section class="main-col" data-v-9e75a158><h2 data-v-9e75a158>\u6700\u8FD1\u6587\u7AE0</h2>`);
      if (unref(pending)) {
        _push(`<div data-v-9e75a158>\u52A0\u8F7D\u4E2D\u2026</div>`);
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
          _push(`<p class="empty" data-v-9e75a158>\u6682\u65E0\u6587\u7AE0</p>`);
        } else {
          _push(`<!---->`);
        }
        _push(`<!--]-->`);
      }
      _push(`</section><aside class="sidebar" data-v-9e75a158>`);
      if ((_c = unref(categories)) == null ? void 0 : _c.length) {
        _push(`<div class="side-block" data-v-9e75a158><h3 data-v-9e75a158>\u5206\u7C7B</h3><ul data-v-9e75a158><!--[-->`);
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
        _push(`<div class="side-block" data-v-9e75a158><h3 data-v-9e75a158>\u6807\u7B7E</h3><div class="tag-cloud" data-v-9e75a158><!--[-->`);
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
      _push(`<div class="side-block" data-v-9e75a158><h3 data-v-9e75a158>\u8BA2\u9605</h3><p data-v-9e75a158>`);
      _push(ssrRenderComponent(_component_NuxtLink, { to: "/subscribe" }, {
        default: withCtx((_, _push2, _parent2, _scopeId) => {
          if (_push2) {
            _push2(`\u90AE\u4EF6\u8BA2\u9605`);
          } else {
            return [
              createTextVNode("\u90AE\u4EF6\u8BA2\u9605")
            ];
          }
        }),
        _: 1
      }, _parent));
      _push(`</p><p data-v-9e75a158><a href="/api/v1/rss/feed.xml" target="_blank" rel="noopener" data-v-9e75a158>RSS Feed</a></p></div></aside></div></div>`);
    };
  }
});
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/index.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const index = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-9e75a158"]]);

export { index as default };
//# sourceMappingURL=index-ee3ef8c2.mjs.map
