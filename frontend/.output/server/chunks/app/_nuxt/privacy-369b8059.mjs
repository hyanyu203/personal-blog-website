import { u as usePageSeo } from './usePageSeo-74338983.mjs';
import { useSSRContext, defineComponent } from 'vue';
import { ssrRenderAttrs } from 'vue/server-renderer';
import { _ as _export_sfc } from '../server.mjs';
import './index-95b6f593.mjs';
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
  __name: "privacy",
  __ssrInlineRender: true,
  setup(__props) {
    usePageSeo({
      title: "\u9690\u79C1\u653F\u7B56",
      description: "\u6E10\u6784\u9690\u79C1\u653F\u7B56\u8BF4\u660E",
      path: "/privacy"
    });
    return (_ctx, _push, _parent, _attrs) => {
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-2f21e760><h1 data-v-2f21e760>\u9690\u79C1\u653F\u7B56</h1><p data-v-2f21e760>\u6E10\u6784\uFF08JianGou\uFF09\u5C0A\u91CD\u5E76\u4FDD\u62A4\u8BBF\u5BA2\u7684\u9690\u79C1\u3002</p><h2 data-v-2f21e760>\u6536\u96C6\u7684\u4FE1\u606F</h2><ul data-v-2f21e760><li data-v-2f21e760>\u8BC4\u8BBA\u4E0E\u8BA2\u9605\u65F6\u60A8\u4E3B\u52A8\u63D0\u4F9B\u7684\u90AE\u7BB1\u7B49\u4FE1\u606F</li><li data-v-2f21e760>\u4E3A\u9632\u6EE5\u7528\u800C\u8BB0\u5F55\u7684\u5FC5\u8981\u8BBF\u95EE\u65E5\u5FD7\uFF08\u5982 IP\u3001User-Agent\uFF09</li></ul><h2 data-v-2f21e760>\u4FE1\u606F\u7528\u9014</h2><p data-v-2f21e760>\u4EC5\u7528\u4E8E\u63D0\u4F9B\u8BC4\u8BBA\u3001\u90AE\u4EF6\u8BA2\u9605\u3001\u7AD9\u70B9\u7EDF\u8BA1\u4E0E\u5B89\u5168\u9632\u62A4\uFF0C\u4E0D\u4F1A\u51FA\u552E\u7ED9\u7B2C\u4E09\u65B9\u3002</p><h2 data-v-2f21e760>Cookie</h2><p data-v-2f21e760>\u767B\u5F55\u7528\u6237\u4F7F\u7528 HttpOnly Cookie \u7EF4\u6301\u4F1A\u8BDD\uFF08\u542B\u524D\u53F0\u8BC4\u8BBA/\u70B9\u8D5E\u4E0E\u540E\u53F0 CMS\uFF09\uFF1B\u4E3B\u9898\u504F\u597D\u5B58\u50A8\u5728\u6D4F\u89C8\u5668 localStorage\u3002</p><h2 data-v-2f21e760>\u8054\u7CFB\u6211\u4EEC</h2><p data-v-2f21e760>\u5982\u6709\u7591\u95EE\uFF0C\u8BF7\u901A\u8FC7<a href="/guestbook" data-v-2f21e760>\u7559\u8A00\u677F</a>\u8054\u7CFB\u3002</p></div>`);
    };
  }
});
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/privacy.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const privacy = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-2f21e760"]]);

export { privacy as default };
//# sourceMappingURL=privacy-369b8059.mjs.map
