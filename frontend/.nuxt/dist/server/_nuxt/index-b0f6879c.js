import { u as usePageSeo } from "./usePageSeo-74338983.js";
import { defineComponent, ref, useSSRContext } from "vue";
import { ssrRenderAttrs, ssrRenderAttr, ssrInterpolate } from "vue/server-renderer";
import "hookable";
import "destr";
import "devalue";
import "klona";
import { _ as _export_sfc } from "../server.mjs";
import "./index-95b6f593.js";
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
  __name: "index",
  __ssrInlineRender: true,
  setup(__props) {
    const email = ref("");
    const done = ref(false);
    const message = ref("");
    const error = ref("");
    usePageSeo({ title: "订阅", description: "邮件与 RSS 订阅渐构", path: "/subscribe" });
    return (_ctx, _push, _parent, _attrs) => {
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-dddc23e0><h1 data-v-dddc23e0>订阅</h1><p class="desc" data-v-dddc23e0>通过邮件接收新文章通知，或通过 RSS 阅读器订阅。</p><p class="rss-link" data-v-dddc23e0><a href="/api/v1/rss/feed.xml" target="_blank" rel="noopener" data-v-dddc23e0>RSS Feed →</a></p>`);
      if (!done.value) {
        _push(`<form class="form" data-v-dddc23e0><input${ssrRenderAttr("value", email.value)} type="email" placeholder="your@email.com" required data-v-dddc23e0><button type="submit" data-v-dddc23e0>订阅</button>`);
        if (error.value) {
          _push(`<p class="error" data-v-dddc23e0>${ssrInterpolate(error.value)}</p>`);
        } else {
          _push(`<!---->`);
        }
        _push(`</form>`);
      } else {
        _push(`<p class="success" data-v-dddc23e0>${ssrInterpolate(message.value)}</p>`);
      }
      _push(`</div>`);
    };
  }
});
const index_vue_vue_type_style_index_0_scoped_dddc23e0_lang = "";
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/subscribe/index.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const index = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-dddc23e0"]]);
export {
  index as default
};
//# sourceMappingURL=index-b0f6879c.js.map
