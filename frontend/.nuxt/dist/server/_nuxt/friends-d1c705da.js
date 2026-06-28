import { a as apiFetch, g as useAuth, b as useAsyncData, _ as _export_sfc } from "../server.mjs";
import { defineComponent, withAsyncContext, ref, unref, useSSRContext } from "vue";
import "hookable";
import { u as useSeoMeta } from "./index-95b6f593.js";
import "destr";
import "devalue";
import "klona";
import { ssrRenderAttrs, ssrRenderList, ssrRenderAttr, ssrInterpolate } from "vue/server-renderer";
import { s as safeExternalHref } from "./safeUrl-0be01e9a.js";
import "ofetch";
import "#internal/nitro";
import "unctx";
import "h3";
import "@unhead/ssr";
import "unhead";
import "@unhead/shared";
import "vue-router";
import "ufo";
import "defu";
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
    useSeoMeta({ title: "友链 - 渐构" });
    return (_ctx, _push, _parent, _attrs) => {
      _push(`<div${ssrRenderAttrs(_attrs)} data-v-5873d55e><h1 data-v-5873d55e>友链</h1><ul class="links" data-v-5873d55e><!--[-->`);
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
      _push(`<!--]--></ul><section class="apply card" data-v-5873d55e><h2 data-v-5873d55e>友链申请</h2><form data-v-5873d55e><input${ssrRenderAttr("value", form.value.name)} placeholder="站点名" required data-v-5873d55e><input${ssrRenderAttr("value", form.value.url)} placeholder="URL" required data-v-5873d55e><input${ssrRenderAttr("value", form.value.ownerEmail)} placeholder="邮箱" required data-v-5873d55e><textarea placeholder="描述" rows="3" data-v-5873d55e>${ssrInterpolate(form.value.description)}</textarea><button type="submit" data-v-5873d55e>提交申请</button>`);
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
const friends_vue_vue_type_style_index_0_scoped_5873d55e_lang = "";
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/friends.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const friends = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-5873d55e"]]);
export {
  friends as default
};
//# sourceMappingURL=friends-d1c705da.js.map
