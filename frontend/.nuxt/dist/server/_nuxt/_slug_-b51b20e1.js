import { _ as _sfc_main$1 } from "./SafeHtml-8927f1df.js";
import { e as useRoute, b as useAsyncData, g as useAuth, h as useRuntimeConfig, _ as _export_sfc } from "../server.mjs";
import { u as useSeoMeta } from "./index-95b6f593.js";
import { defineComponent, computed, withAsyncContext, ref, watch, unref, useSSRContext } from "vue";
import "destr";
import "devalue";
import "klona";
import { ssrRenderAttrs, ssrInterpolate, ssrRenderComponent, ssrIncludeBooleanAttr, ssrRenderAttr } from "vue/server-renderer";
import { f as fetchSnippetBySlug } from "./snippet.api-40fc8cb7.js";
import "isomorphic-dompurify";
import "ofetch";
import "#internal/nitro";
import "hookable";
import "unctx";
import "h3";
import "@unhead/ssr";
import "unhead";
import "@unhead/shared";
import "vue-router";
import "ufo";
import "defu";
const _sfc_main = /* @__PURE__ */ defineComponent({
  __name: "[slug]",
  __ssrInlineRender: true,
  async setup(__props) {
    var _a, _b;
    let __temp, __restore;
    const route = useRoute();
    const config = /* @__PURE__ */ useRuntimeConfig();
    const slug = route.params.slug;
    const rawUrl = computed(() => `${config.public.apiBase}/snippets/${slug}/raw`);
    const { data: snippet } = ([__temp, __restore] = withAsyncContext(() => useAsyncData(`snippet-${slug}`, () => fetchSnippetBySlug(slug))), __temp = await __temp, __restore(), __temp);
    const liked = ref(false);
    const likeCount = ref(((_a = snippet.value) == null ? void 0 : _a.likeCount) ?? 0);
    const copyCount = ref(((_b = snippet.value) == null ? void 0 : _b.copyCount) ?? 0);
    const msg = ref("");
    watch(snippet, (s) => {
      if (s) {
        likeCount.value = s.likeCount ?? 0;
        copyCount.value = s.copyCount ?? 0;
      }
    }, { immediate: true });
    useAuth();
    useSeoMeta({ title: () => snippet.value ? `${snippet.value.title} - 渐构` : "渐构" });
    return (_ctx, _push, _parent, _attrs) => {
      const _component_SafeHtml = _sfc_main$1;
      if (unref(snippet)) {
        _push(`<article${ssrRenderAttrs(_attrs)} data-v-fb4bc6e7><h1 data-v-fb4bc6e7>${ssrInterpolate(unref(snippet).title)}</h1><p class="meta" data-v-fb4bc6e7>${ssrInterpolate(unref(snippet).language)} · ${ssrInterpolate(unref(snippet).viewCount)} 次浏览 `);
        if (unref(copyCount)) {
          _push(`<span data-v-fb4bc6e7> · ${ssrInterpolate(unref(copyCount))} 次复制</span>`);
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
        _push(`<div class="actions" data-v-fb4bc6e7><button type="button" data-v-fb4bc6e7>复制代码</button><button type="button"${ssrIncludeBooleanAttr(unref(liked)) ? " disabled" : ""} data-v-fb4bc6e7>${ssrInterpolate(unref(liked) ? "已赞" : "点赞")} ${ssrInterpolate(unref(likeCount))}</button><a${ssrRenderAttr("href", unref(rawUrl))} target="_blank" rel="noopener" data-v-fb4bc6e7>Raw</a></div>`);
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
const _slug__vue_vue_type_style_index_0_scoped_fb4bc6e7_lang = "";
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/snippets/[slug].vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const _slug_ = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-fb4bc6e7"]]);
export {
  _slug_ as default
};
//# sourceMappingURL=_slug_-b51b20e1.js.map
