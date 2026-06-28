import { useSSRContext, defineComponent, withAsyncContext, unref } from 'vue';
import { u as useSeoMeta } from './index-95b6f593.mjs';
import { _ as _export_sfc, u as useSiteSettings, b as useAsyncData, a as apiFetch } from '../server.mjs';
import { ssrRenderAttrs, ssrInterpolate } from 'vue/server-renderer';
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

function fetchStats() {
  return apiFetch("/stats");
}
const _sfc_main = /* @__PURE__ */ defineComponent({
  __name: "about",
  __ssrInlineRender: true,
  async setup(__props) {
    let __temp, __restore;
    const { data: settings } = ([__temp, __restore] = withAsyncContext(() => useSiteSettings()), __temp = await __temp, __restore(), __temp);
    const { data: stats } = ([__temp, __restore] = withAsyncContext(() => useAsyncData("about-stats", () => fetchStats())), __temp = await __temp, __restore(), __temp);
    useSeoMeta({ title: "\u5173\u4E8E - \u6E10\u6784" });
    return (_ctx, _push, _parent, _attrs) => {
      var _a;
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-87882b19><h1 data-v-87882b19>\u5173\u4E8E</h1>`);
      if ((_a = unref(settings)) == null ? void 0 : _a.siteDescription) {
        _push(`<p data-v-87882b19>${ssrInterpolate(unref(settings).siteDescription)}</p>`);
      } else {
        _push(`<p data-v-87882b19>\u6E10\u6784\u662F\u4E2A\u4EBA\u6280\u672F\u77E5\u8BC6\u6C89\u6DC0\u5E73\u53F0\uFF0C\u878D\u5408 Blog\u3001Wiki\u3001\u4EE3\u7801\u7247\u6BB5\u4E0E\u9879\u76EE\u9648\u5217\u3002</p>`);
      }
      if (unref(stats)) {
        _push(`<p class="stats" data-v-87882b19>${ssrInterpolate(unref(stats).articleCount)} \u7BC7\u6587\u7AE0 \xB7 ${ssrInterpolate(unref(stats).snippetCount)} \u4E2A\u4EE3\u7801\u7247\u6BB5 \xB7 ${ssrInterpolate(unref(stats).noteCount)} \u6761\u788E\u788E\u5FF5 \xB7 \u8FD0\u884C ${ssrInterpolate(unref(stats).runningDays)} \u5929 </p>`);
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
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/about.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const about = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-87882b19"]]);

export { about as default };
//# sourceMappingURL=about-db629e7c.mjs.map
