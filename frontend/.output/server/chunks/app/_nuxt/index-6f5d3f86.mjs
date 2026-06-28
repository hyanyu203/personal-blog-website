import { _ as _sfc_main$1 } from './SafeHtml-8927f1df.mjs';
import { _ as _export_sfc, g as useAuth, b as useAsyncData, a as apiFetch } from '../server.mjs';
import { useSSRContext, defineComponent, withAsyncContext, reactive, unref } from 'vue';
import { u as useSeoMeta } from './index-95b6f593.mjs';
import { ssrRenderAttrs, ssrRenderList, ssrRenderAttr, ssrInterpolate, ssrRenderComponent } from 'vue/server-renderer';
import 'isomorphic-dompurify';
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
    useSeoMeta({ title: "\u788E\u788E\u5FF5 - \u6E10\u6784" });
    return (_ctx, _push, _parent, _attrs) => {
      var _a;
      const _component_SafeHtml = _sfc_main$1;
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-dfbd3719><h1 data-v-dfbd3719>\u788E\u788E\u5FF5</h1><p class="subtitle" data-v-dfbd3719>\u65F6\u5149\u673A</p>`);
      if (unref(pending)) {
        _push(`<div data-v-dfbd3719>\u52A0\u8F7D\u4E2D\u2026</div>`);
      } else {
        _push(`<div class="timeline" data-v-dfbd3719><!--[-->`);
        ssrRenderList((_a = unref(data)) == null ? void 0 : _a.items, (n) => {
          var _a2;
          _push(`<article${ssrRenderAttr("id", String(n.id))} class="note" data-v-dfbd3719><time data-v-dfbd3719>${ssrInterpolate(n.publishedAt)}</time>`);
          _push(ssrRenderComponent(_component_SafeHtml, {
            html: n.contentHtml
          }, null, _parent));
          _push(`<button type="button" class="like-btn" data-v-dfbd3719> \u2665 ${ssrInterpolate((_a2 = likes[n.id]) != null ? _a2 : n.likeCount)}</button></article>`);
        });
        _push(`<!--]--></div>`);
      }
      _push(`</div>`);
    };
  }
});
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/notes/index.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const index = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-dfbd3719"]]);

export { index as default };
//# sourceMappingURL=index-6f5d3f86.mjs.map
