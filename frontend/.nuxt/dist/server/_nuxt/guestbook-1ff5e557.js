import { _ as __nuxt_component_2 } from "./CommentSection-2fd33290.js";
import { u as useSeoMeta } from "./index-95b6f593.js";
import { defineComponent, computed, unref, useSSRContext } from "vue";
import { ssrRenderAttrs, ssrRenderComponent } from "vue/server-renderer";
import { u as useSiteSettings, _ as _export_sfc } from "../server.mjs";
import "./SafeHtml-8927f1df.js";
import "isomorphic-dompurify";
import "hookable";
import "destr";
import "devalue";
import "klona";
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
  __name: "guestbook",
  __ssrInlineRender: true,
  setup(__props) {
    useSeoMeta({ title: "留言板 - 渐构" });
    const { data: settings, pending } = useSiteSettings();
    const targetId = computed(() => {
      var _a;
      const raw = (_a = settings.value) == null ? void 0 : _a.guestbookTargetId;
      const id = raw != null ? Number(raw) : NaN;
      return Number.isFinite(id) && id > 0 ? id : null;
    });
    return (_ctx, _push, _parent, _attrs) => {
      const _component_CommentSection = __nuxt_component_2;
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-5baafa62><h1 data-v-5baafa62>留言板</h1>`);
      if (unref(targetId)) {
        _push(ssrRenderComponent(_component_CommentSection, {
          "target-type": "guestbook",
          "target-id": unref(targetId)
        }, null, _parent));
      } else if (unref(pending)) {
        _push(`<p data-v-5baafa62>加载中…</p>`);
      } else {
        _push(`<p class="error" data-v-5baafa62>留言板未配置，请在后台设置 guestbookTargetId。</p>`);
      }
      _push(`</div>`);
    };
  }
});
const guestbook_vue_vue_type_style_index_0_scoped_5baafa62_lang = "";
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/guestbook.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const guestbook = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-5baafa62"]]);
export {
  guestbook as default
};
//# sourceMappingURL=guestbook-1ff5e557.js.map
