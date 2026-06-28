import { u as usePageSeo } from './usePageSeo-74338983.mjs';
import { useSSRContext, defineComponent, ref } from 'vue';
import { ssrRenderAttrs, ssrRenderAttr, ssrInterpolate } from 'vue/server-renderer';
import { _ as _export_sfc } from '../server.mjs';
import './index-95b6f593.mjs';
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
  __name: "index",
  __ssrInlineRender: true,
  setup(__props) {
    const email = ref("");
    const done = ref(false);
    const message = ref("");
    const error = ref("");
    usePageSeo({ title: "\u8BA2\u9605", description: "\u90AE\u4EF6\u4E0E RSS \u8BA2\u9605\u6E10\u6784", path: "/subscribe" });
    return (_ctx, _push, _parent, _attrs) => {
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-dddc23e0><h1 data-v-dddc23e0>\u8BA2\u9605</h1><p class="desc" data-v-dddc23e0>\u901A\u8FC7\u90AE\u4EF6\u63A5\u6536\u65B0\u6587\u7AE0\u901A\u77E5\uFF0C\u6216\u901A\u8FC7 RSS \u9605\u8BFB\u5668\u8BA2\u9605\u3002</p><p class="rss-link" data-v-dddc23e0><a href="/api/v1/rss/feed.xml" target="_blank" rel="noopener" data-v-dddc23e0>RSS Feed \u2192</a></p>`);
      if (!done.value) {
        _push(`<form class="form" data-v-dddc23e0><input${ssrRenderAttr("value", email.value)} type="email" placeholder="your@email.com" required data-v-dddc23e0><button type="submit" data-v-dddc23e0>\u8BA2\u9605</button>`);
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
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/subscribe/index.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const index = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-dddc23e0"]]);

export { index as default };
//# sourceMappingURL=index-b0f6879c.mjs.map
