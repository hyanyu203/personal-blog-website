import { _ as _export_sfc, e as useRoute, f as useRouter, g as useAuth, d as __nuxt_component_0$1 } from '../server.mjs';
import { useSSRContext, defineComponent, ref, computed, mergeProps, unref, withCtx, createTextVNode } from 'vue';
import { u as useSeoMeta } from './index-95b6f593.mjs';
import { ssrRenderAttrs, ssrRenderAttr, ssrIncludeBooleanAttr, ssrInterpolate, ssrRenderComponent } from 'vue/server-renderer';
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
  __name: "register",
  __ssrInlineRender: true,
  setup(__props) {
    useRoute();
    useRouter();
    useAuth();
    const form = ref({ username: "", email: "", password: "", emailCode: "" });
    ref("");
    const captchaCode = ref("");
    const captchaImage = ref("");
    const captchaSrc = computed(
      () => /^data:image\/(png|jpeg);base64,/.test(captchaImage.value) ? captchaImage.value : ""
    );
    const loading = ref(false);
    const sendingCode = ref(false);
    const error = ref("");
    const message = ref("");
    useSeoMeta({ title: "\u6CE8\u518C - \u6E10\u6784" });
    return (_ctx, _push, _parent, _attrs) => {
      const _component_NuxtLink = __nuxt_component_0$1;
      _push(`<div${ssrRenderAttrs(mergeProps({ class: "auth-page" }, _attrs))} data-v-835620ed><h1 data-v-835620ed>\u6CE8\u518C</h1><form class="card" data-v-835620ed><label data-v-835620ed> \u7528\u6237\u540D <input${ssrRenderAttr("value", unref(form).username)} autocomplete="username" required data-v-835620ed></label><label data-v-835620ed> \u90AE\u7BB1 <input${ssrRenderAttr("value", unref(form).email)} type="email" autocomplete="email" required data-v-835620ed></label><label data-v-835620ed> \u5BC6\u7801 <input${ssrRenderAttr("value", unref(form).password)} type="password" autocomplete="new-password" required data-v-835620ed></label><div class="captcha-row" data-v-835620ed><label class="grow" data-v-835620ed> \u56FE\u5F62\u9A8C\u8BC1\u7801 <input${ssrRenderAttr("value", unref(captchaCode))} required data-v-835620ed></label><button type="button" class="captcha-btn" data-v-835620ed>`);
      if (unref(captchaSrc)) {
        _push(`<img${ssrRenderAttr("src", unref(captchaSrc))} alt="\u9A8C\u8BC1\u7801" data-v-835620ed>`);
      } else {
        _push(`<span data-v-835620ed>\u52A0\u8F7D</span>`);
      }
      _push(`</button></div><div class="code-row" data-v-835620ed><label class="grow" data-v-835620ed> \u90AE\u7BB1\u9A8C\u8BC1\u7801 <input${ssrRenderAttr("value", unref(form).emailCode)} maxlength="6" required data-v-835620ed></label><button type="button"${ssrIncludeBooleanAttr(unref(sendingCode)) ? " disabled" : ""} data-v-835620ed>${ssrInterpolate(unref(sendingCode) ? "\u53D1\u9001\u4E2D\u2026" : "\u53D1\u9001\u9A8C\u8BC1\u7801")}</button></div><button type="submit"${ssrIncludeBooleanAttr(unref(loading)) ? " disabled" : ""} data-v-835620ed>${ssrInterpolate(unref(loading) ? "\u6CE8\u518C\u4E2D\u2026" : "\u6CE8\u518C")}</button>`);
      if (unref(error)) {
        _push(`<p class="error" data-v-835620ed>${ssrInterpolate(unref(error))}</p>`);
      } else {
        _push(`<!---->`);
      }
      if (unref(message)) {
        _push(`<p class="msg" data-v-835620ed>${ssrInterpolate(unref(message))}</p>`);
      } else {
        _push(`<!---->`);
      }
      _push(`<p class="links" data-v-835620ed>`);
      _push(ssrRenderComponent(_component_NuxtLink, { to: "/login" }, {
        default: withCtx((_, _push2, _parent2, _scopeId) => {
          if (_push2) {
            _push2(`\u5DF2\u6709\u8D26\u53F7\uFF1F\u767B\u5F55`);
          } else {
            return [
              createTextVNode("\u5DF2\u6709\u8D26\u53F7\uFF1F\u767B\u5F55")
            ];
          }
        }),
        _: 1
      }, _parent));
      _push(`</p></form></div>`);
    };
  }
});
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/register.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const register = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-835620ed"]]);

export { register as default };
//# sourceMappingURL=register-b1a322a6.mjs.map
