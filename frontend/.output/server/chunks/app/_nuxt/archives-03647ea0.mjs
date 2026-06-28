import { _ as _export_sfc, b as useAsyncData, d as __nuxt_component_0$1 } from '../server.mjs';
import { useSSRContext, defineComponent, withAsyncContext, unref, withCtx, createTextVNode, toDisplayString } from 'vue';
import { u as useSeoMeta } from './index-95b6f593.mjs';
import { ssrRenderAttrs, ssrRenderList, ssrInterpolate, ssrRenderComponent } from 'vue/server-renderer';
import { f as fetchArchives } from './taxonomy.api-f709c8c5.mjs';
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
import './article.api-3cf8cb60.mjs';

const _sfc_main = /* @__PURE__ */ defineComponent({
  __name: "archives",
  __ssrInlineRender: true,
  async setup(__props) {
    let __temp, __restore;
    const { data: groups, pending } = ([__temp, __restore] = withAsyncContext(() => useAsyncData("archives", () => fetchArchives())), __temp = await __temp, __restore(), __temp);
    useSeoMeta({ title: "\u5F52\u6863 - \u6E10\u6784" });
    return (_ctx, _push, _parent, _attrs) => {
      const _component_NuxtLink = __nuxt_component_0$1;
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-4e0b91e8><h1 data-v-4e0b91e8>\u5F52\u6863</h1>`);
      if (unref(pending)) {
        _push(`<div data-v-4e0b91e8>\u52A0\u8F7D\u4E2D\u2026</div>`);
      } else {
        _push(`<!---->`);
      }
      _push(`<!--[-->`);
      ssrRenderList(unref(groups), (g) => {
        _push(`<section class="group" data-v-4e0b91e8><h2 data-v-4e0b91e8>${ssrInterpolate(g.year)} \u5E74 ${ssrInterpolate(g.month)} \u6708</h2><ul data-v-4e0b91e8><!--[-->`);
        ssrRenderList(g.articles, (a) => {
          _push(`<li data-v-4e0b91e8>`);
          _push(ssrRenderComponent(_component_NuxtLink, {
            to: `/posts/${a.slug}`
          }, {
            default: withCtx((_, _push2, _parent2, _scopeId) => {
              if (_push2) {
                _push2(`${ssrInterpolate(a.title)}`);
              } else {
                return [
                  createTextVNode(toDisplayString(a.title), 1)
                ];
              }
            }),
            _: 2
          }, _parent));
          _push(`</li>`);
        });
        _push(`<!--]--></ul></section>`);
      });
      _push(`<!--]--></div>`);
    };
  }
});
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/archives.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const archives = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-4e0b91e8"]]);

export { archives as default };
//# sourceMappingURL=archives-03647ea0.mjs.map
