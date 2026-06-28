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
    useSeoMeta({ title: "注册 - 渐构" });
    return (_ctx, _push, _parent, _attrs) => {
      const _component_NuxtLink = __nuxt_component_0;
      _push(`<div${ssrRenderAttrs(mergeProps({ class: "auth-page" }, _attrs))} data-v-835620ed><h1 data-v-835620ed>注册</h1><form class="card" data-v-835620ed><label data-v-835620ed> 用户名 <input${ssrRenderAttr("value", unref(form).username)} autocomplete="username" required data-v-835620ed></label><label data-v-835620ed> 邮箱 <input${ssrRenderAttr("value", unref(form).email)} type="email" autocomplete="email" required data-v-835620ed></label><label data-v-835620ed> 密码 <input${ssrRenderAttr("value", unref(form).password)} type="password" autocomplete="new-password" required data-v-835620ed></label><div class="captcha-row" data-v-835620ed><label class="grow" data-v-835620ed> 图形验证码 <input${ssrRenderAttr("value", unref(captchaCode))} required data-v-835620ed></label><button type="button" class="captcha-btn" data-v-835620ed>`);
      if (unref(captchaSrc)) {
        _push(`<img${ssrRenderAttr("src", unref(captchaSrc))} alt="验证码" data-v-835620ed>`);
      } else {
        _push(`<span data-v-835620ed>加载</span>`);
      }
      _push(`</button></div><div class="code-row" data-v-835620ed><label class="grow" data-v-835620ed> 邮箱验证码 <input${ssrRenderAttr("value", unref(form).emailCode)} maxlength="6" required data-v-835620ed></label><button type="button"${ssrIncludeBooleanAttr(unref(sendingCode)) ? " disabled" : ""} data-v-835620ed>${ssrInterpolate(unref(sendingCode) ? "发送中…" : "发送验证码")}</button></div><button type="submit"${ssrIncludeBooleanAttr(unref(loading)) ? " disabled" : ""} data-v-835620ed>${ssrInterpolate(unref(loading) ? "注册中…" : "注册")}</button>`);
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
            _push2(`已有账号？登录`);
          } else {
            return [
              createTextVNode("已有账号？登录")
            ];
          }
        }),
        _: 1
      }, _parent));
      _push(`</p></form></div>`);
    };
  }
});
const register_vue_vue_type_style_index_0_scoped_835620ed_lang = "";
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/register.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const register = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-835620ed"]]);
export {
  register as default
};
//# sourceMappingURL=register-b1a322a6.js.map
