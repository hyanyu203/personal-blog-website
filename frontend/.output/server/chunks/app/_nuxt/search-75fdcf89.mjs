import { _ as _export_sfc, d as __nuxt_component_0$1 } from '../server.mjs';
import { useSSRContext, defineComponent, ref, unref, withCtx, createTextVNode, toDisplayString } from 'vue';
import { u as useSeoMeta } from './index-95b6f593.mjs';
import { ssrRenderAttrs, ssrRenderAttr, ssrRenderList, ssrRenderClass, ssrInterpolate, ssrRenderComponent } from 'vue/server-renderer';
import { a as safeInternalPath } from './safeUrl-0be01e9a.mjs';
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

const _sfc_main = /* @__PURE__ */ defineComponent({
  __name: "search",
  __ssrInlineRender: true,
  setup(__props) {
    const types = [
      { value: "all", label: "\u5168\u90E8" },
      { value: "article", label: "\u6587\u7AE0" },
      { value: "snippet", label: "\u4EE3\u7801" },
      { value: "note", label: "\u788E\u788E\u5FF5" },
      { value: "project", label: "\u9879\u76EE" }
    ];
    const q = ref("");
    const type = ref("all");
    const results = ref([]);
    const total = ref(null);
    const searched = ref(false);
    const loading = ref(false);
    const searchError = ref("");
    const suggestions = ref([]);
    function typeLabel(t) {
      const found = types.find((x) => x.value === t);
      return found ? found.label : t;
    }
    useSeoMeta({ title: "\u641C\u7D22 - \u6E10\u6784" });
    return (_ctx, _push, _parent, _attrs) => {
      const _component_NuxtLink = __nuxt_component_0$1;
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-db5dd264><h1 data-v-db5dd264>\u641C\u7D22</h1><form data-v-db5dd264><input${ssrRenderAttr("value", unref(q))} type="search" placeholder="\u8F93\u5165\u5173\u952E\u8BCD\u2026" class="input" list="suggest-list" data-v-db5dd264><datalist id="suggest-list" data-v-db5dd264><!--[-->`);
      ssrRenderList(unref(suggestions), (s) => {
        _push(`<option${ssrRenderAttr("value", s)} data-v-db5dd264></option>`);
      });
      _push(`<!--]--></datalist><button type="submit" data-v-db5dd264>\u641C\u7D22</button></form><div class="types" data-v-db5dd264><!--[-->`);
      ssrRenderList(types, (t) => {
        _push(`<button class="${ssrRenderClass({ active: unref(type) === t.value })}" type="button" data-v-db5dd264>${ssrInterpolate(t.label)}</button>`);
      });
      _push(`<!--]--></div>`);
      if (unref(loading)) {
        _push(`<p class="summary" data-v-db5dd264>\u641C\u7D22\u4E2D\u2026</p>`);
      } else {
        _push(`<!---->`);
      }
      if (unref(searchError)) {
        _push(`<p class="error" data-v-db5dd264>${ssrInterpolate(unref(searchError))}</p>`);
      } else {
        _push(`<!---->`);
      }
      if (unref(total) !== null && !unref(loading)) {
        _push(`<p class="summary" data-v-db5dd264>\u5171 ${ssrInterpolate(unref(total))} \u6761\u7ED3\u679C</p>`);
      } else {
        _push(`<!---->`);
      }
      if (unref(results).length) {
        _push(`<ul class="results" data-v-db5dd264><!--[-->`);
        ssrRenderList(unref(results), (item) => {
          _push(`<li data-v-db5dd264><span class="badge" data-v-db5dd264>${ssrInterpolate(typeLabel(item.type))}</span>`);
          if (unref(safeInternalPath)(item.url)) {
            _push(ssrRenderComponent(_component_NuxtLink, {
              to: unref(safeInternalPath)(item.url)
            }, {
              default: withCtx((_, _push2, _parent2, _scopeId) => {
                if (_push2) {
                  _push2(`${ssrInterpolate(item.title)}`);
                } else {
                  return [
                    createTextVNode(toDisplayString(item.title), 1)
                  ];
                }
              }),
              _: 2
            }, _parent));
          } else {
            _push(`<span data-v-db5dd264>${ssrInterpolate(item.title)}</span>`);
          }
          _push(`<p data-v-db5dd264>${ssrInterpolate(item.snippet)}</p></li>`);
        });
        _push(`<!--]--></ul>`);
      } else if (unref(searched)) {
        _push(`<p class="empty" data-v-db5dd264>\u65E0\u5339\u914D\u7ED3\u679C</p>`);
      } else {
        _push(`<!---->`);
      }
      _push(`</div>`);
    };
  }
});
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/search.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const search = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-db5dd264"]]);

export { search as default };
//# sourceMappingURL=search-75fdcf89.mjs.map
