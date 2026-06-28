import { b as useAsyncData, d as __nuxt_component_0, _ as _export_sfc } from "../server.mjs";
import { defineComponent, withAsyncContext, unref, withCtx, createVNode, toDisplayString, createTextVNode, useSSRContext } from "vue";
import "hookable";
import { u as useSeoMeta } from "./index-95b6f593.js";
import "destr";
import "devalue";
import "klona";
import { ssrRenderAttrs, ssrInterpolate, ssrRenderList, ssrRenderComponent } from "vue/server-renderer";
import { a as fetchSnippets } from "./snippet.api-40fc8cb7.js";
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
    const { data, pending, error } = ([__temp, __restore] = withAsyncContext(() => useAsyncData("snippets", () => fetchSnippets())), __temp = await __temp, __restore(), __temp);
    useSeoMeta({ title: "代码片段 - 渐构" });
    return (_ctx, _push, _parent, _attrs) => {
      var _a;
      const _component_NuxtLink = __nuxt_component_0;
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-5329172f><h1 data-v-5329172f>代码片段</h1>`);
      if (unref(pending)) {
        _push(`<div data-v-5329172f>加载中…</div>`);
      } else if (unref(error)) {
        _push(`<div data-v-5329172f>${ssrInterpolate(unref(userFacingError)(unref(error)))}</div>`);
      } else {
        _push(`<ul class="list" data-v-5329172f><!--[-->`);
        ssrRenderList((_a = unref(data)) == null ? void 0 : _a.items, (s) => {
          _push(`<li data-v-5329172f>`);
          _push(ssrRenderComponent(_component_NuxtLink, {
            to: `/snippets/${s.slug}`
          }, {
            default: withCtx((_, _push2, _parent2, _scopeId) => {
              if (_push2) {
                _push2(`<span class="lang" data-v-5329172f${_scopeId}>${ssrInterpolate(s.language)}</span> ${ssrInterpolate(s.title)}`);
              } else {
                return [
                  createVNode("span", { class: "lang" }, toDisplayString(s.language), 1),
                  createTextVNode(" " + toDisplayString(s.title), 1)
                ];
              }
            }),
            _: 2
          }, _parent));
          _push(`</li>`);
        });
        _push(`<!--]--></ul>`);
      }
      _push(`</div>`);
    };
  }
});
const index_vue_vue_type_style_index_0_scoped_5329172f_lang = "";
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/snippets/index.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const index = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-5329172f"]]);
export {
  index as default
};
//# sourceMappingURL=index-c6768223.js.map
