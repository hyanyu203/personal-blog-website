import { useSSRContext, defineComponent, ref } from 'vue';
import { u as useSeoMeta } from './index-95b6f593.mjs';
import { _ as _export_sfc, e as useRoute } from '../server.mjs';
import { ssrRenderAttrs, ssrInterpolate } from 'vue/server-renderer';
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
  __name: "confirm",
  __ssrInlineRender: true,
  setup(__props) {
    const route = useRoute();
    route.query.token;
    const message = ref("");
    const error = ref("");
    const pending = ref(true);
    useSeoMeta({ title: "\u8BA2\u9605\u786E\u8BA4 - \u6E10\u6784" });
    return (_ctx, _push, _parent, _attrs) => {
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-27cacbd6><h1 data-v-27cacbd6>\u8BA2\u9605\u786E\u8BA4</h1>`);
      if (pending.value) {
        _push(`<p data-v-27cacbd6>\u786E\u8BA4\u4E2D\u2026</p>`);
      } else if (error.value) {
        _push(`<p data-v-27cacbd6>${ssrInterpolate(error.value)}</p>`);
      } else {
        _push(`<p class="success" data-v-27cacbd6>${ssrInterpolate(message.value)}</p>`);
      }
      _push(`</div>`);
    };
  }
});
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/subscribe/confirm.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const confirm = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-27cacbd6"]]);

export { confirm as default };
//# sourceMappingURL=confirm-ef89cbb6.mjs.map
