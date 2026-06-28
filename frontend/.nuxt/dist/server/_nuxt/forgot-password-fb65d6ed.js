import { d as __nuxt_component_0, f as useRouter, _ as _export_sfc } from "../server.mjs";
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
    useSeoMeta({ title: "忘记密码 - 渐构" });
    return (_ctx, _push, _parent, _attrs) => {
      const _component_NuxtLink = __nuxt_component_0;
      _push(`<div${ssrRenderAttrs(mergeProps({ class: "auth-page" }, _attrs))} data-v-162487f1><h1 data-v-162487f1>忘记密码</h1><form class="card" data-v-162487f1><label data-v-162487f1> 注册邮箱 <input${ssrRenderAttr("value", unref(email))} type="email" autocomplete="email" required data-v-162487f1></label><div class="captcha-row" data-v-162487f1><label class="grow" data-v-162487f1> 图形验证码 <input${ssrRenderAttr("value", unref(captchaCode))} required data-v-162487f1></label><button type="button" class="captcha-btn" data-v-162487f1>`);
      if (unref(captchaSrc)) {
        _push(`<img${ssrRenderAttr("src", unref(captchaSrc))} alt="验证码" data-v-162487f1>`);
      } else {
        _push(`<span data-v-162487f1>加载</span>`);
      }
      _push(`</button></div><div class="code-row" data-v-162487f1><label class="grow" data-v-162487f1> 邮箱验证码 <input${ssrRenderAttr("value", unref(emailCode))} maxlength="6" required data-v-162487f1></label><button type="button"${ssrIncludeBooleanAttr(unref(sendingCode)) ? " disabled" : ""} data-v-162487f1>${ssrInterpolate(unref(sendingCode) ? "发送中…" : "发送验证码")}</button></div><label data-v-162487f1> 新密码 <input${ssrRenderAttr("value", unref(newPassword))} type="password" autocomplete="new-password" required data-v-162487f1></label><button type="submit"${ssrIncludeBooleanAttr(unref(loading)) ? " disabled" : ""} data-v-162487f1>${ssrInterpolate(unref(loading) ? "提交中…" : "重置密码")}</button>`);
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
            _push2(`返回登录`);
          } else {
            return [
              createTextVNode("返回登录")
            ];
          }
        }),
        _: 1
      }, _parent));
      _push(`</p></form></div>`);
    };
  }
});
const forgotPassword_vue_vue_type_style_index_0_scoped_162487f1_lang = "";
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/forgot-password.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const forgotPassword = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-162487f1"]]);
export {
  forgotPassword as default
};
//# sourceMappingURL=forgot-password-fb65d6ed.js.map
