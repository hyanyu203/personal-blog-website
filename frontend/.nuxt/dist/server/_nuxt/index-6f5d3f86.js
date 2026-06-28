import { _ as _sfc_main$1 } from "./SafeHtml-8927f1df.js";
import { a as apiFetch, g as useAuth, b as useAsyncData, _ as _export_sfc } from "../server.mjs";
import { defineComponent, withAsyncContext, reactive, unref, useSSRContext } from "vue";
import "hookable";
import { u as useSeoMeta } from "./index-95b6f593.js";
import "destr";
import "devalue";
import "klona";
import { ssrRenderAttrs, ssrRenderList, ssrRenderAttr, ssrInterpolate, ssrRenderComponent } from "vue/server-renderer";
import "isomorphic-dompurify";
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
function fetchNotes(page = 1) {
  return apiFetch(`/notes?page=${page}&pageSize=30`);
}
const _sfc_main = /* @__PURE__ */ defineComponent({
  __name: "index",
  __ssrInlineRender: true,
  async setup(__props) {
    let __temp, __restore;
    useAuth();
    const { data, pending } = ([__temp, __restore] = withAsyncContext(() => useAsyncData("notes", () => fetchNotes())), __temp = await __temp, __restore(), __temp);
    const likes = reactive({});
    useSeoMeta({ title: "碎碎念 - 渐构" });
    return (_ctx, _push, _parent, _attrs) => {
      var _a;
      const _component_SafeHtml = _sfc_main$1;
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-dfbd3719><h1 data-v-dfbd3719>碎碎念</h1><p class="subtitle" data-v-dfbd3719>时光机</p>`);
      if (unref(pending)) {
        _push(`<div data-v-dfbd3719>加载中…</div>`);
      } else {
        _push(`<div class="timeline" data-v-dfbd3719><!--[-->`);
        ssrRenderList((_a = unref(data)) == null ? void 0 : _a.items, (n) => {
          _push(`<article${ssrRenderAttr("id", String(n.id))} class="note" data-v-dfbd3719><time data-v-dfbd3719>${ssrInterpolate(n.publishedAt)}</time>`);
          _push(ssrRenderComponent(_component_SafeHtml, {
            html: n.contentHtml
          }, null, _parent));
          _push(`<button type="button" class="like-btn" data-v-dfbd3719> ♥ ${ssrInterpolate(likes[n.id] ?? n.likeCount)}</button></article>`);
        });
        _push(`<!--]--></div>`);
      }
      _push(`</div>`);
    };
  }
});
const index_vue_vue_type_style_index_0_scoped_dfbd3719_lang = "";
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/notes/index.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const index = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-dfbd3719"]]);
export {
  index as default
};
//# sourceMappingURL=index-6f5d3f86.js.map
