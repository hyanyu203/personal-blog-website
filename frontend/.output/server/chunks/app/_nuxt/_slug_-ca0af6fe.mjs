import { _ as __nuxt_component_0 } from './ArticleCard-a7d9553a.mjs';
import { defineComponent, withAsyncContext, unref, useSSRContext } from 'vue';
import { u as useSeoMeta } from './index-95b6f593.mjs';
import { e as useRoute, b as useAsyncData } from '../server.mjs';
import { ssrRenderAttrs, ssrInterpolate, ssrRenderList, ssrRenderComponent } from 'vue/server-renderer';
import { c as fetchTag, d as fetchArticlesByTag } from './taxonomy.api-f709c8c5.mjs';
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
    const { data: tag, pending, error } = ([__temp, __restore] = withAsyncContext(() => useAsyncData(`tag-${slug}`, () => fetchTag(slug))), __temp = await __temp, __restore(), __temp);
    const { data: articles } = ([__temp, __restore] = withAsyncContext(() => useAsyncData(`tag-posts-${slug}`, () => fetchArticlesByTag(slug))), __temp = await __temp, __restore(), __temp);
    useSeoMeta({ title: () => tag.value ? `${tag.value.name} - \u6E10\u6784` : "\u6E10\u6784" });
    return (_ctx, _push, _parent, _attrs) => {
      var _a, _b;
      const _component_ArticleCard = __nuxt_component_0;
      _push(`<div${ssrRenderAttrs(_attrs)}><h1>\u6807\u7B7E\uFF1A${ssrInterpolate(((_a = unref(tag)) == null ? void 0 : _a.name) || unref(slug))}</h1>`);
      if (unref(pending)) {
        _push(`<div>\u52A0\u8F7D\u4E2D\u2026</div>`);
      } else if (unref(error)) {
        _push(`<div>${ssrInterpolate(unref(userFacingError)(unref(error)))}</div>`);
      } else {
        _push(`<!--[-->`);
        if (!unref(tag)) {
          _push(`<p>\u6807\u7B7E\u4E0D\u5B58\u5728</p>`);
        } else {
          _push(`<!---->`);
        }
        _push(`<!--[-->`);
        ssrRenderList((_b = unref(articles)) == null ? void 0 : _b.items, (item) => {
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
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/tags/[slug].vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};

export { _sfc_main as default };
//# sourceMappingURL=_slug_-ca0af6fe.mjs.map
