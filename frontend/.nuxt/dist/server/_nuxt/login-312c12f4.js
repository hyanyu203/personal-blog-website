import { e as useRoute, g as useAuth, d as __nuxt_component_0, f as useRouter, _ as _export_sfc } from "../server.mjs";
import { defineComponent, ref, computed, mergeProps, unref, withCtx, createTextVNode, useSSRContext } from "vue";
import "vue-router";
import "hookable";
import { u as useSeoMeta } from "./index-95b6f593.js";
import "destr";
import "devalue";
import "klona";
import { ssrRenderAttrs, ssrRenderAttr, ssrIncludeBooleanAttr, ssrInterpolate, ssrRenderComponent } from "vue/server-renderer";
import "ofetch";
import "#internal/nitro";
import "unctx";
import "h3";
import "@unhead/ssr";
import "unhead";
import "@unhead/shared";
import "ufo";
import "defu";
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
    useSeoMeta({ title: "登录 - 渐构" });
    return (_ctx, _push, _parent, _attrs) => {
      const _component_NuxtLink = __nuxt_component_0;
      _push(`<div${ssrRenderAttrs(mergeProps({ class: "auth-page" }, _attrs))} data-v-9c065650><h1 data-v-9c065650>登录</h1><form class="card" data-v-9c065650><label data-v-9c065650> 用户名 <input${ssrRenderAttr("value", unref(username))} autocomplete="username" required data-v-9c065650></label><label data-v-9c065650> 密码 <input${ssrRenderAttr("value", unref(password))} type="password" autocomplete="current-password" required data-v-9c065650></label><div class="captcha-row" data-v-9c065650><label class="grow" data-v-9c065650> 图形验证码 <input${ssrRenderAttr("value", unref(captchaCode))} required data-v-9c065650></label><button type="button" class="captcha-btn" data-v-9c065650>`);
      if (unref(captchaSrc)) {
        _push(`<img${ssrRenderAttr("src", unref(captchaSrc))} alt="验证码" data-v-9c065650>`);
      } else {
        _push(`<span data-v-9c065650>加载</span>`);
      }
      _push(`</button></div><button type="submit"${ssrIncludeBooleanAttr(unref(loading)) ? " disabled" : ""} data-v-9c065650>${ssrInterpolate(unref(loading) ? "登录中…" : "登录")}</button>`);
      if (unref(error)) {
        _push(`<p class="error" data-v-9c065650>${ssrInterpolate(unref(error))}</p>`);
      } else {
        _push(`<!---->`);
      }
      _push(`<p class="links" data-v-9c065650>`);
      _push(ssrRenderComponent(_component_NuxtLink, { to: "/register" }, {
        default: withCtx((_, _push2, _parent2, _scopeId) => {
          if (_push2) {
            _push2(`注册账号`);
          } else {
            return [
              createTextVNode("注册账号")
            ];
          }
        }),
        _: 1
      }, _parent));
      _push(` · `);
      _push(ssrRenderComponent(_component_NuxtLink, { to: "/forgot-password" }, {
        default: withCtx((_, _push2, _parent2, _scopeId) => {
          if (_push2) {
            _push2(`忘记密码`);
          } else {
            return [
              createTextVNode("忘记密码")
            ];
          }
        }),
        _: 1
      }, _parent));
      _push(`</p></form></div>`);
    };
  }
});
const login_vue_vue_type_style_index_0_scoped_9c065650_lang = "";
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/login.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const login = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-9c065650"]]);
export {
  login as default
};
//# sourceMappingURL=login-312c12f4.js.map
