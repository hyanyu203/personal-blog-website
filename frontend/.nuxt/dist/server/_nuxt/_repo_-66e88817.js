import { defineComponent, withAsyncContext, computed, unref, useSSRContext } from "vue";
import "hookable";
import { u as useSeoMeta } from "./index-95b6f593.js";
import { e as useRoute, b as useAsyncData, _ as _export_sfc } from "../server.mjs";
import "destr";
import "devalue";
import "klona";
import { ssrRenderAttrs, ssrInterpolate, ssrRenderAttr } from "vue/server-renderer";
import { f as fetchProjectDetail } from "./project.api-3f8c30f7.js";
import { s as safeExternalHref } from "./safeUrl-0be01e9a.js";
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
const _sfc_main = /* @__PURE__ */ defineComponent({
  __name: "[repo]",
  __ssrInlineRender: true,
  async setup(__props) {
    let __temp, __restore;
    const route = useRoute();
    const owner = route.params.owner;
    const repo = route.params.repo;
    const { data: project, pending } = ([__temp, __restore] = withAsyncContext(() => useAsyncData(
      `project-${owner}-${repo}`,
      () => fetchProjectDetail(owner, repo)
    )), __temp = await __temp, __restore(), __temp);
    const githubHref = computed(() => {
      var _a;
      return safeExternalHref((_a = project.value) == null ? void 0 : _a.githubUrl);
    });
    useSeoMeta({
      title: () => project.value ? `${project.value.name} - 渐构` : "渐构"
    });
    return (_ctx, _push, _parent, _attrs) => {
      if (unref(project)) {
        _push(`<article${ssrRenderAttrs(_attrs)} data-v-f8da6f88><h1 data-v-f8da6f88>${ssrInterpolate(unref(project).name)}</h1><p class="desc" data-v-f8da6f88>${ssrInterpolate(unref(project).description)}</p><dl class="meta" data-v-f8da6f88>`);
        if (unref(project).language) {
          _push(`<div data-v-f8da6f88><dt data-v-f8da6f88>语言</dt><dd data-v-f8da6f88>${ssrInterpolate(unref(project).language)}</dd></div>`);
        } else {
          _push(`<!---->`);
        }
        _push(`<div data-v-f8da6f88><dt data-v-f8da6f88>Stars</dt><dd data-v-f8da6f88>${ssrInterpolate(unref(project).stars)}</dd></div><div data-v-f8da6f88><dt data-v-f8da6f88>Forks</dt><dd data-v-f8da6f88>${ssrInterpolate(unref(project).forks)}</dd></div>`);
        if (unref(project).license) {
          _push(`<div data-v-f8da6f88><dt data-v-f8da6f88>License</dt><dd data-v-f8da6f88>${ssrInterpolate(unref(project).license)}</dd></div>`);
        } else {
          _push(`<!---->`);
        }
        _push(`</dl>`);
        if (unref(githubHref)) {
          _push(`<a${ssrRenderAttr("href", unref(githubHref))} target="_blank" rel="noopener noreferrer" class="btn" data-v-f8da6f88>在 GitHub 查看</a>`);
        } else {
          _push(`<!---->`);
        }
        _push(`</article>`);
      } else if (unref(pending)) {
        _push(`<div${ssrRenderAttrs(_attrs)} data-v-f8da6f88>加载中…</div>`);
      } else {
        _push(`<div${ssrRenderAttrs(_attrs)} data-v-f8da6f88>项目不存在</div>`);
      }
    };
  }
});
const _repo__vue_vue_type_style_index_0_scoped_f8da6f88_lang = "";
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/projects/[owner]/[repo].vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const _repo_ = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-f8da6f88"]]);
export {
  _repo_ as default
};
//# sourceMappingURL=_repo_-66e88817.js.map
