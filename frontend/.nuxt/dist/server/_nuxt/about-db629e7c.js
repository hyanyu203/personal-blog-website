import { defineComponent, withAsyncContext, unref, useSSRContext } from "vue";
import "hookable";
import { u as useSeoMeta } from "./index-95b6f593.js";
import { a as apiFetch, u as useSiteSettings, b as useAsyncData, _ as _export_sfc } from "../server.mjs";
import "destr";
import "devalue";
import "klona";
import { ssrRenderAttrs, ssrInterpolate } from "vue/server-renderer";
import "@unhead/shared";
import "ofetch";
import "#internal/nitro";
import "unctx";
import "h3";
import "@unhead/ssr";
import "unhead";
import "vue-router";
import "ufo";
import "defu";
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
    useSeoMeta({ title: "关于 - 渐构" });
    return (_ctx, _push, _parent, _attrs) => {
      var _a;
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-87882b19><h1 data-v-87882b19>关于</h1>`);
      if ((_a = unref(settings)) == null ? void 0 : _a.siteDescription) {
        _push(`<p data-v-87882b19>${ssrInterpolate(unref(settings).siteDescription)}</p>`);
      } else {
        _push(`<p data-v-87882b19>渐构是个人技术知识沉淀平台，融合 Blog、Wiki、代码片段与项目陈列。</p>`);
      }
      if (unref(stats)) {
        _push(`<p class="stats" data-v-87882b19>${ssrInterpolate(unref(stats).articleCount)} 篇文章 · ${ssrInterpolate(unref(stats).snippetCount)} 个代码片段 · ${ssrInterpolate(unref(stats).noteCount)} 条碎碎念 · 运行 ${ssrInterpolate(unref(stats).runningDays)} 天 </p>`);
      } else {
        _push(`<!---->`);
      }
      _push(`</div>`);
    };
  }
});
const about_vue_vue_type_style_index_0_scoped_87882b19_lang = "";
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/about.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const about = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-87882b19"]]);
export {
  about as default
};
//# sourceMappingURL=about-db629e7c.js.map
