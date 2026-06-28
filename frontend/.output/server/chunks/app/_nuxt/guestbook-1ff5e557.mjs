import { _ as __nuxt_component_2 } from './CommentSection-2fd33290.mjs';
import { u as useSeoMeta } from './index-95b6f593.mjs';
import { useSSRContext, defineComponent, computed, unref } from 'vue';
import { ssrRenderAttrs, ssrRenderComponent } from 'vue/server-renderer';
import { _ as _export_sfc, u as useSiteSettings } from '../server.mjs';
import './SafeHtml-8927f1df.mjs';
import 'isomorphic-dompurify';
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
  __name: "guestbook",
  __ssrInlineRender: true,
  setup(__props) {
    useSeoMeta({ title: "\u7559\u8A00\u677F - \u6E10\u6784" });
    const { data: settings, pending } = useSiteSettings();
    const targetId = computed(() => {
      var _a;
      const raw = (_a = settings.value) == null ? void 0 : _a.guestbookTargetId;
      const id = raw != null ? Number(raw) : NaN;
      return Number.isFinite(id) && id > 0 ? id : null;
    });
    return (_ctx, _push, _parent, _attrs) => {
      const _component_CommentSection = __nuxt_component_2;
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-5baafa62><h1 data-v-5baafa62>\u7559\u8A00\u677F</h1>`);
      if (unref(targetId)) {
        _push(ssrRenderComponent(_component_CommentSection, {
          "target-type": "guestbook",
          "target-id": unref(targetId)
        }, null, _parent));
      } else if (unref(pending)) {
        _push(`<p data-v-5baafa62>\u52A0\u8F7D\u4E2D\u2026</p>`);
      } else {
        _push(`<p class="error" data-v-5baafa62>\u7559\u8A00\u677F\u672A\u914D\u7F6E\uFF0C\u8BF7\u5728\u540E\u53F0\u8BBE\u7F6E guestbookTargetId\u3002</p>`);
      }
      _push(`</div>`);
    };
  }
});
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/guestbook.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const guestbook = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-5baafa62"]]);

export { guestbook as default };
//# sourceMappingURL=guestbook-1ff5e557.mjs.map
