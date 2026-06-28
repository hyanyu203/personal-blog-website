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
  __name: "login",
  __ssrInlineRender: true,
  setup(__props) {
    useRoute();
    useRouter();
    useAuth();
    const username = ref("");
    const password = ref("");
    ref("");
    const captchaCode = ref("");
    const captchaImage = ref("");
    const captchaSrc = computed(
      () => /^data:image\/(png|jpeg);base64,/.test(captchaImage.value) ? captchaImage.value : ""
    );
    const loading = ref(false);
    const error = ref("");
    useSeoMeta({ title: "\u767B\u5F55 - \u6E10\u6784" });
    return (_ctx, _push, _parent, _attrs) => {
      const _component_NuxtLink = __nuxt_component_0$1;
      _push(`<div${ssrRenderAttrs(mergeProps({ class: "auth-page" }, _attrs))} data-v-9c065650><h1 data-v-9c065650>\u767B\u5F55</h1><form class="card" data-v-9c065650><label data-v-9c065650> \u7528\u6237\u540D <input${ssrRenderAttr("value", unref(username))} autocomplete="username" required data-v-9c065650></label><label data-v-9c065650> \u5BC6\u7801 <input${ssrRenderAttr("value", unref(password))} type="password" autocomplete="current-password" required data-v-9c065650></label><div class="captcha-row" data-v-9c065650><label class="grow" data-v-9c065650> \u56FE\u5F62\u9A8C\u8BC1\u7801 <input${ssrRenderAttr("value", unref(captchaCode))} required data-v-9c065650></label><button type="button" class="captcha-btn" data-v-9c065650>`);
      if (unref(captchaSrc)) {
        _push(`<img${ssrRenderAttr("src", unref(captchaSrc))} alt="\u9A8C\u8BC1\u7801" data-v-9c065650>`);
      } else {
        _push(`<span data-v-9c065650>\u52A0\u8F7D</span>`);
      }
      _push(`</button></div><button type="submit"${ssrIncludeBooleanAttr(unref(loading)) ? " disabled" : ""} data-v-9c065650>${ssrInterpolate(unref(loading) ? "\u767B\u5F55\u4E2D\u2026" : "\u767B\u5F55")}</button>`);
      if (unref(error)) {
        _push(`<p class="error" data-v-9c065650>${ssrInterpolate(unref(error))}</p>`);
      } else {
        _push(`<!---->`);
      }
      _push(`<p class="links" data-v-9c065650>`);
      _push(ssrRenderComponent(_component_NuxtLink, { to: "/register" }, {
        default: withCtx((_, _push2, _parent2, _scopeId) => {
          if (_push2) {
            _push2(`\u6CE8\u518C\u8D26\u53F7`);
          } else {
            return [
              createTextVNode("\u6CE8\u518C\u8D26\u53F7")
            ];
          }
        }),
        _: 1
      }, _parent));
      _push(` \xB7 `);
      _push(ssrRenderComponent(_component_NuxtLink, { to: "/forgot-password" }, {
        default: withCtx((_, _push2, _parent2, _scopeId) => {
          if (_push2) {
            _push2(`\u5FD8\u8BB0\u5BC6\u7801`);
          } else {
            return [
              createTextVNode("\u5FD8\u8BB0\u5BC6\u7801")
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
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/login.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const login = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-9c065650"]]);

export { login as default };
//# sourceMappingURL=login-312c12f4.mjs.map
