import { _ as _export_sfc, g as useAuth, b as useAsyncData, a as apiFetch } from '../server.mjs';
import { useSSRContext, defineComponent, withAsyncContext, ref, unref } from 'vue';
import { u as useSeoMeta } from './index-95b6f593.mjs';
import { ssrRenderAttrs, ssrRenderList, ssrRenderAttr, ssrInterpolate } from 'vue/server-renderer';
import { s as safeExternalHref } from './safeUrl-0be01e9a.mjs';
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

function fetchFriendLinks() {
  return apiFetch("/friend-links");
}
const _sfc_main = /* @__PURE__ */ defineComponent({
  __name: "friends",
  __ssrInlineRender: true,
  async setup(__props) {
    let __temp, __restore;
    useAuth();
    const { data: links } = ([__temp, __restore] = withAsyncContext(() => useAsyncData("friends", () => fetchFriendLinks())), __temp = await __temp, __restore(), __temp);
    const form = ref({ name: "", url: "", ownerEmail: "", description: "" });
    const msg = ref("");
    const error = ref("");
    useSeoMeta({ title: "\u53CB\u94FE - \u6E10\u6784" });
    return (_ctx, _push, _parent, _attrs) => {
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-5873d55e><h1 data-v-5873d55e>\u53CB\u94FE</h1><ul class="links" data-v-5873d55e><!--[-->`);
      ssrRenderList(unref(links), (l) => {
        _push(`<li data-v-5873d55e>`);
        if (unref(safeExternalHref)(l.url)) {
          _push(`<a${ssrRenderAttr("href", unref(safeExternalHref)(l.url))} target="_blank" rel="noopener noreferrer" data-v-5873d55e>${ssrInterpolate(l.name)}</a>`);
        } else {
          _push(`<span data-v-5873d55e>${ssrInterpolate(l.name)}</span>`);
        }
        if (l.description) {
          _push(`<p data-v-5873d55e>${ssrInterpolate(l.description)}</p>`);
        } else {
          _push(`<!---->`);
        }
        _push(`</li>`);
      });
      _push(`<!--]--></ul><section class="apply card" data-v-5873d55e><h2 data-v-5873d55e>\u53CB\u94FE\u7533\u8BF7</h2><form data-v-5873d55e><input${ssrRenderAttr("value", form.value.name)} placeholder="\u7AD9\u70B9\u540D" required data-v-5873d55e><input${ssrRenderAttr("value", form.value.url)} placeholder="URL" required data-v-5873d55e><input${ssrRenderAttr("value", form.value.ownerEmail)} placeholder="\u90AE\u7BB1" required data-v-5873d55e><textarea placeholder="\u63CF\u8FF0" rows="3" data-v-5873d55e>${ssrInterpolate(form.value.description)}</textarea><button type="submit" data-v-5873d55e>\u63D0\u4EA4\u7533\u8BF7</button>`);
      if (msg.value) {
        _push(`<p class="msg" data-v-5873d55e>${ssrInterpolate(msg.value)}</p>`);
      } else {
        _push(`<!---->`);
      }
      if (error.value) {
        _push(`<p class="error" data-v-5873d55e>${ssrInterpolate(error.value)}</p>`);
      } else {
        _push(`<!---->`);
      }
      _push(`</form></section></div>`);
    };
  }
});
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/friends.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const friends = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-5873d55e"]]);

export { friends as default };
//# sourceMappingURL=friends-d1c705da.mjs.map
