import { useSSRContext, defineComponent, withAsyncContext, computed, unref } from 'vue';
import { u as useSeoMeta } from './index-95b6f593.mjs';
import { _ as _export_sfc, e as useRoute, b as useAsyncData } from '../server.mjs';
import { ssrRenderAttrs, ssrInterpolate, ssrRenderAttr } from 'vue/server-renderer';
import { f as fetchProjectDetail } from './project.api-3f8c30f7.mjs';
import { s as safeExternalHref } from './safeUrl-0be01e9a.mjs';
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
      title: () => project.value ? `${project.value.name} - \u6E10\u6784` : "\u6E10\u6784"
    });
    return (_ctx, _push, _parent, _attrs) => {
      if (unref(project)) {
        _push(`<article${ssrRenderAttrs(_attrs)} data-v-f8da6f88><h1 data-v-f8da6f88>${ssrInterpolate(unref(project).name)}</h1><p class="desc" data-v-f8da6f88>${ssrInterpolate(unref(project).description)}</p><dl class="meta" data-v-f8da6f88>`);
        if (unref(project).language) {
          _push(`<div data-v-f8da6f88><dt data-v-f8da6f88>\u8BED\u8A00</dt><dd data-v-f8da6f88>${ssrInterpolate(unref(project).language)}</dd></div>`);
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
          _push(`<a${ssrRenderAttr("href", unref(githubHref))} target="_blank" rel="noopener noreferrer" class="btn" data-v-f8da6f88>\u5728 GitHub \u67E5\u770B</a>`);
        } else {
          _push(`<!---->`);
        }
        _push(`</article>`);
      } else if (unref(pending)) {
        _push(`<div${ssrRenderAttrs(_attrs)} data-v-f8da6f88>\u52A0\u8F7D\u4E2D\u2026</div>`);
      } else {
        _push(`<div${ssrRenderAttrs(_attrs)} data-v-f8da6f88>\u9879\u76EE\u4E0D\u5B58\u5728</div>`);
      }
    };
  }
});
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/projects/[owner]/[repo].vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const _repo_ = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-f8da6f88"]]);

export { _repo_ as default };
//# sourceMappingURL=_repo_-66e88817.mjs.map
