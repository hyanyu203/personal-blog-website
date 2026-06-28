import { defineComponent, ref, useSSRContext } from "vue";
import "hookable";
import { u as useSeoMeta } from "./index-95b6f593.js";
import { e as useRoute, _ as _export_sfc } from "../server.mjs";
import "destr";
import "devalue";
import "klona";
import { ssrRenderAttrs, ssrInterpolate } from "vue/server-renderer";
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
  __name: "unsubscribe",
  __ssrInlineRender: true,
  setup(__props) {
    const route = useRoute();
    route.query.token;
    const message = ref("");
    const error = ref("");
    const pending = ref(true);
    useSeoMeta({ title: "退订 - 渐构" });
    return (_ctx, _push, _parent, _attrs) => {
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-c6021418><h1 data-v-c6021418>退订</h1>`);
      if (pending.value) {
        _push(`<p data-v-c6021418>处理中…</p>`);
      } else if (error.value) {
        _push(`<p data-v-c6021418>${ssrInterpolate(error.value)}</p>`);
      } else {
        _push(`<p class="success" data-v-c6021418>${ssrInterpolate(message.value)}</p>`);
      }
      _push(`</div>`);
    };
  }
});
const unsubscribe_vue_vue_type_style_index_0_scoped_c6021418_lang = "";
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/subscribe/unsubscribe.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const unsubscribe = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-c6021418"]]);
export {
  unsubscribe as default
};
//# sourceMappingURL=unsubscribe-3fcfd2c1.js.map
