import { _ as __nuxt_component_0 } from './ArticleCard-a7d9553a.mjs';
import { useSSRContext, defineComponent, withAsyncContext, unref } from 'vue';
import { u as useSeoMeta } from './index-95b6f593.mjs';
import { _ as _export_sfc, e as useRoute, b as useAsyncData } from '../server.mjs';
import { ssrRenderAttrs, ssrInterpolate, ssrRenderList, ssrRenderComponent } from 'vue/server-renderer';
import { a as fetchCategory, b as fetchArticlesByCategory } from './taxonomy.api-f709c8c5.mjs';
import { u as userFacingError } from './userErrorMessage-f95eddb5.mjs';
import '@unhead/shared';
import 'ofetch';
import 'hookable';
import 'unctx';
import 'h3';
import '@unhead/ssr';
import 'unhead';
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
import './article.api-3cf8cb60.mjs';

const _sfc_main = /* @__PURE__ */ defineComponent({
  __name: "[slug]",
  __ssrInlineRender: true,
  async setup(__props) {
    let __temp, __restore;
    const route = useRoute();
    const slug = route.params.slug;
    const { data: category, pending, error } = ([__temp, __restore] = withAsyncContext(() => useAsyncData(
      `cat-${slug}`,
      () => fetchCategory(slug)
    )), __temp = await __temp, __restore(), __temp);
    const { data: articles } = ([__temp, __restore] = withAsyncContext(() => useAsyncData(
      `cat-posts-${slug}`,
      () => fetchArticlesByCategory(slug),
      { watch: [() => {
        var _a;
        return (_a = category.value) == null ? void 0 : _a.slug;
      }] }
    )), __temp = await __temp, __restore(), __temp);
    useSeoMeta({ title: () => category.value ? `${category.value.name} - \u6E10\u6784` : "\u6E10\u6784" });
    return (_ctx, _push, _parent, _attrs) => {
      var _a, _b, _c;
      const _component_ArticleCard = __nuxt_component_0;
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-346bd03b><h1 data-v-346bd03b>\u5206\u7C7B\uFF1A${ssrInterpolate(((_a = unref(category)) == null ? void 0 : _a.name) || unref(slug))}</h1>`);
      if (unref(pending)) {
        _push(`<div data-v-346bd03b>\u52A0\u8F7D\u4E2D\u2026</div>`);
      } else if (unref(error)) {
        _push(`<div data-v-346bd03b>${ssrInterpolate(unref(userFacingError)(unref(error)))}</div>`);
      } else {
        _push(`<!--[-->`);
        if ((_b = unref(category)) == null ? void 0 : _b.description) {
          _push(`<p class="desc" data-v-346bd03b>${ssrInterpolate(unref(category).description)}</p>`);
        } else {
          _push(`<!---->`);
        }
        if (!unref(category)) {
          _push(`<p data-v-346bd03b>\u5206\u7C7B\u4E0D\u5B58\u5728</p>`);
        } else {
          _push(`<!---->`);
        }
        _push(`<!--[-->`);
        ssrRenderList((_c = unref(articles)) == null ? void 0 : _c.items, (item) => {
          _push(ssrRenderComponent(_component_ArticleCard, {
            key: item.id,
            article: item
          }, null, _parent));
        });
        _push(`<!--]--><!--]-->`);
      }
      _push(`</div>`);
    };
  }
});
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/categories/[slug].vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const _slug_ = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-346bd03b"]]);

export { _slug_ as default };
//# sourceMappingURL=_slug_-5c062ecf.mjs.map
