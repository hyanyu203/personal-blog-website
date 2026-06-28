import { u as usePageSeo } from "./usePageSeo-74338983.js";
import { defineComponent, useSSRContext } from "vue";
import { ssrRenderAttrs } from "vue/server-renderer";
import { _ as _export_sfc } from "../server.mjs";
import "./index-95b6f593.js";
import "@unhead/shared";
import "destr";
import "devalue";
import "klona";
import "ofetch";
import "#internal/nitro";
import "hookable";
import "unctx";
import "h3";
import "@unhead/ssr";
import "unhead";
import "vue-router";
import "ufo";
import "defu";
const _sfc_main = /* @__PURE__ */ defineComponent({
  __name: "privacy",
  __ssrInlineRender: true,
  setup(__props) {
    usePageSeo({
      title: "隐私政策",
      description: "渐构隐私政策说明",
      path: "/privacy"
    });
    return (_ctx, _push, _parent, _attrs) => {
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-2f21e760><h1 data-v-2f21e760>隐私政策</h1><p data-v-2f21e760>渐构（JianGou）尊重并保护访客的隐私。</p><h2 data-v-2f21e760>收集的信息</h2><ul data-v-2f21e760><li data-v-2f21e760>评论与订阅时您主动提供的邮箱等信息</li><li data-v-2f21e760>为防滥用而记录的必要访问日志（如 IP、User-Agent）</li></ul><h2 data-v-2f21e760>信息用途</h2><p data-v-2f21e760>仅用于提供评论、邮件订阅、站点统计与安全防护，不会出售给第三方。</p><h2 data-v-2f21e760>Cookie</h2><p data-v-2f21e760>登录用户使用 HttpOnly Cookie 维持会话（含前台评论/点赞与后台 CMS）；主题偏好存储在浏览器 localStorage。</p><h2 data-v-2f21e760>联系我们</h2><p data-v-2f21e760>如有疑问，请通过<a href="/guestbook" data-v-2f21e760>留言板</a>联系。</p></div>`);
    };
  }
});
const privacy_vue_vue_type_style_index_0_scoped_2f21e760_lang = "";
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/privacy.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const privacy = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-2f21e760"]]);
export {
  privacy as default
};
//# sourceMappingURL=privacy-369b8059.js.map
