import { b as useAsyncData, d as __nuxt_component_0, _ as _export_sfc } from "../server.mjs";
import { defineComponent, withAsyncContext, unref, withCtx, createTextVNode, toDisplayString, useSSRContext } from "vue";
import "hookable";
import { u as useSeoMeta } from "./index-95b6f593.js";
import "destr";
import "devalue";
import "klona";
import { ssrRenderAttrs, ssrRenderList, ssrRenderComponent, ssrInterpolate } from "vue/server-renderer";
import { a as fetchProjects } from "./project.api-3f8c30f7.js";
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
    const { data: projects, pending } = ([__temp, __restore] = withAsyncContext(() => useAsyncData("projects", () => fetchProjects())), __temp = await __temp, __restore(), __temp);
    useSeoMeta({ title: "项目 - 渐构" });
    return (_ctx, _push, _parent, _attrs) => {
      const _component_NuxtLink = __nuxt_component_0;
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-4ece21f5><h1 data-v-4ece21f5>开源项目</h1>`);
      if (unref(pending)) {
        _push(`<div data-v-4ece21f5>加载中…</div>`);
      } else {
        _push(`<div class="grid" data-v-4ece21f5><!--[-->`);
        ssrRenderList(unref(projects), (p) => {
          _push(`<article class="card" data-v-4ece21f5><h2 data-v-4ece21f5>`);
          _push(ssrRenderComponent(_component_NuxtLink, {
            to: `/projects/${p.owner}/${p.repo}`
          }, {
            default: withCtx((_, _push2, _parent2, _scopeId) => {
              if (_push2) {
                _push2(`${ssrInterpolate(p.name)}`);
              } else {
                return [
                  createTextVNode(toDisplayString(p.name), 1)
                ];
              }
            }),
            _: 2
          }, _parent));
          if (p.pinned) {
            _push(`<span class="pin" data-v-4ece21f5>置顶</span>`);
          } else {
            _push(`<!---->`);
          }
          _push(`</h2><p data-v-4ece21f5>${ssrInterpolate(p.description)}</p><div class="meta" data-v-4ece21f5>`);
          if (p.language) {
            _push(`<span data-v-4ece21f5>${ssrInterpolate(p.language)}</span>`);
          } else {
            _push(`<!---->`);
          }
          _push(`<span data-v-4ece21f5>★ ${ssrInterpolate(p.stars)}</span><span data-v-4ece21f5>Fork ${ssrInterpolate(p.forks)}</span></div></article>`);
        });
        _push(`<!--]--></div>`);
      }
      _push(`</div>`);
    };
  }
});
const index_vue_vue_type_style_index_0_scoped_4ece21f5_lang = "";
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/projects/index.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const index = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-4ece21f5"]]);
export {
  index as default
};
//# sourceMappingURL=index-798727ce.js.map
