import { _ as _export_sfc, f as useRouter, d as __nuxt_component_0$1 } from '../server.mjs';
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
  __name: "forgot-password",
  __ssrInlineRender: true,
  setup(__props) {
    useRouter();
    const email = ref("");
    const emailCode = ref("");
    const newPassword = ref("");
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
    useSeoMeta({ title: "\u5FD8\u8BB0\u5BC6\u7801 - \u6E10\u6784" });
    return (_ctx, _push, _parent, _attrs) => {
      const _component_NuxtLink = __nuxt_component_0$1;
      _push(`<div${ssrRenderAttrs(mergeProps({ class: "auth-page" }, _attrs))} data-v-162487f1><h1 data-v-162487f1>\u5FD8\u8BB0\u5BC6\u7801</h1><form class="card" data-v-162487f1><label data-v-162487f1> \u6CE8\u518C\u90AE\u7BB1 <input${ssrRenderAttr("value", unref(email))} type="email" autocomplete="email" required data-v-162487f1></label><div class="captcha-row" data-v-162487f1><label class="grow" data-v-162487f1> \u56FE\u5F62\u9A8C\u8BC1\u7801 <input${ssrRenderAttr("value", unref(captchaCode))} required data-v-162487f1></label><button type="button" class="captcha-btn" data-v-162487f1>`);
      if (unref(captchaSrc)) {
        _push(`<img${ssrRenderAttr("src", unref(captchaSrc))} alt="\u9A8C\u8BC1\u7801" data-v-162487f1>`);
      } else {
        _push(`<span data-v-162487f1>\u52A0\u8F7D</span>`);
      }
      _push(`</button></div><div class="code-row" data-v-162487f1><label class="grow" data-v-162487f1> \u90AE\u7BB1\u9A8C\u8BC1\u7801 <input${ssrRenderAttr("value", unref(emailCode))} maxlength="6" required data-v-162487f1></label><button type="button"${ssrIncludeBooleanAttr(unref(sendingCode)) ? " disabled" : ""} data-v-162487f1>${ssrInterpolate(unref(sendingCode) ? "\u53D1\u9001\u4E2D\u2026" : "\u53D1\u9001\u9A8C\u8BC1\u7801")}</button></div><label data-v-162487f1> \u65B0\u5BC6\u7801 <input${ssrRenderAttr("value", unref(newPassword))} type="password" autocomplete="new-password" required data-v-162487f1></label><button type="submit"${ssrIncludeBooleanAttr(unref(loading)) ? " disabled" : ""} data-v-162487f1>${ssrInterpolate(unref(loading) ? "\u63D0\u4EA4\u4E2D\u2026" : "\u91CD\u7F6E\u5BC6\u7801")}</button>`);
      if (unref(error)) {
        _push(`<p class="error" data-v-162487f1>${ssrInterpolate(unref(error))}</p>`);
      } else {
        _push(`<!---->`);
      }
      if (unref(message)) {
        _push(`<p class="msg" data-v-162487f1>${ssrInterpolate(unref(message))}</p>`);
      } else {
        _push(`<!---->`);
      }
      _push(`<p class="links" data-v-162487f1>`);
      _push(ssrRenderComponent(_component_NuxtLink, { to: "/login" }, {
        default: withCtx((_, _push2, _parent2, _scopeId) => {
          if (_push2) {
            _push2(`\u8FD4\u56DE\u767B\u5F55`);
          } else {
            return [
              createTextVNode("\u8FD4\u56DE\u767B\u5F55")
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
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/forgot-password.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const forgotPassword = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-162487f1"]]);

export { forgotPassword as default };
//# sourceMappingURL=forgot-password-fb65d6ed.mjs.map
