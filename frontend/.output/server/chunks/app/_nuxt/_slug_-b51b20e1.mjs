import { _ as _sfc_main$1 } from './SafeHtml-8927f1df.mjs';
import { _ as _export_sfc, e as useRoute, b as useAsyncData, g as useAuth, h as useRuntimeConfig } from '../server.mjs';
import { u as useSeoMeta } from './index-95b6f593.mjs';
import { useSSRContext, defineComponent, computed, withAsyncContext, ref, watch, unref } from 'vue';
import { ssrRenderAttrs, ssrInterpolate, ssrRenderComponent, ssrIncludeBooleanAttr, ssrRenderAttr } from 'vue/server-renderer';
import { f as fetchSnippetBySlug } from './snippet.api-40fc8cb7.mjs';
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

const _sfc_main = /* @__PURE__ */ defineComponent({
  __name: "[slug]",
  __ssrInlineRender: true,
  async setup(__props) {
    var _a2, _b2;
    var _a, _b;
    let __temp, __restore;
    const route = useRoute();
    const config = /* @__PURE__ */ useRuntimeConfig();
    const slug = route.params.slug;
    const rawUrl = computed(() => `${config.public.apiBase}/snippets/${slug}/raw`);
    const { data: snippet } = ([__temp, __restore] = withAsyncContext(() => useAsyncData(`snippet-${slug}`, () => fetchSnippetBySlug(slug))), __temp = await __temp, __restore(), __temp);
    const liked = ref(false);
    const likeCount = ref((_a2 = (_a = snippet.value) == null ? void 0 : _a.likeCount) != null ? _a2 : 0);
    const copyCount = ref((_b2 = (_b = snippet.value) == null ? void 0 : _b.copyCount) != null ? _b2 : 0);
    const msg = ref("");
    watch(snippet, (s) => {
      var _a3, _b3;
      if (s) {
        likeCount.value = (_a3 = s.likeCount) != null ? _a3 : 0;
        copyCount.value = (_b3 = s.copyCount) != null ? _b3 : 0;
      }
    }, { immediate: true });
    useAuth();
    useSeoMeta({ title: () => snippet.value ? `${snippet.value.title} - \u6E10\u6784` : "\u6E10\u6784" });
    return (_ctx, _push, _parent, _attrs) => {
      const _component_SafeHtml = _sfc_main$1;
      if (unref(snippet)) {
        _push(`<article${ssrRenderAttrs(_attrs)} data-v-fb4bc6e7><h1 data-v-fb4bc6e7>${ssrInterpolate(unref(snippet).title)}</h1><p class="meta" data-v-fb4bc6e7>${ssrInterpolate(unref(snippet).language)} \xB7 ${ssrInterpolate(unref(snippet).viewCount)} \u6B21\u6D4F\u89C8 `);
        if (unref(copyCount)) {
          _push(`<span data-v-fb4bc6e7> \xB7 ${ssrInterpolate(unref(copyCount))} \u6B21\u590D\u5236</span>`);
        } else {
          _push(`<!---->`);
        }
        _push(`</p>`);
        if (unref(snippet).descriptionHtml) {
          _push(ssrRenderComponent(_component_SafeHtml, {
            html: unref(snippet).descriptionHtml
          }, null, _parent));
        } else {
          _push(`<!---->`);
        }
        _push(ssrRenderComponent(_component_SafeHtml, {
          class: "code",
          html: unref(snippet).highlightedHtml
        }, null, _parent));
        _push(`<div class="actions" data-v-fb4bc6e7><button type="button" data-v-fb4bc6e7>\u590D\u5236\u4EE3\u7801</button><button type="button"${ssrIncludeBooleanAttr(unref(liked)) ? " disabled" : ""} data-v-fb4bc6e7>${ssrInterpolate(unref(liked) ? "\u5DF2\u8D5E" : "\u70B9\u8D5E")} ${ssrInterpolate(unref(likeCount))}</button><a${ssrRenderAttr("href", unref(rawUrl))} target="_blank" rel="noopener" data-v-fb4bc6e7>Raw</a></div>`);
        if (unref(msg)) {
          _push(`<p class="msg" data-v-fb4bc6e7>${ssrInterpolate(unref(msg))}</p>`);
        } else {
          _push(`<!---->`);
        }
        _push(`</article>`);
      } else {
        _push(`<!---->`);
      }
    };
  }
});
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/snippets/[slug].vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const _slug_ = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-fb4bc6e7"]]);

export { _slug_ as default };
//# sourceMappingURL=_slug_-b51b20e1.mjs.map
