import { _ as _sfc_main$1 } from './SafeHtml-8927f1df.mjs';
import { _ as _export_sfc, g as useAuth, d as __nuxt_component_0$1 } from '../server.mjs';
import { useSSRContext, defineComponent, ref, mergeProps, unref, withCtx, createTextVNode } from 'vue';
import { ssrRenderAttrs, ssrRenderList, ssrInterpolate, ssrRenderComponent, ssrIncludeBooleanAttr } from 'vue/server-renderer';

const _sfc_main = /* @__PURE__ */ defineComponent({
  __name: "CommentSection",
  __ssrInlineRender: true,
  props: {
    targetType: {},
    targetId: {}
  },
  setup(__props) {
    const { isAuthenticated, ensureAuthForAction, withAuth } = useAuth();
    const comments = ref([]);
    const content = ref("");
    const message = ref("");
    const loading = ref(true);
    const likedIds = ref(/* @__PURE__ */ new Set());
    const replyTo = ref(null);
    const replyContent = ref("");
    return (_ctx, _push, _parent, _attrs) => {
      const _component_SafeHtml = _sfc_main$1;
      const _component_NuxtLink = __nuxt_component_0$1;
      _push(`<section${ssrRenderAttrs(mergeProps({ class: "comments" }, _attrs))} data-v-3b61f429><h2 data-v-3b61f429>\u8BC4\u8BBA</h2>`);
      if (loading.value) {
        _push(`<div data-v-3b61f429>\u52A0\u8F7D\u4E2D\u2026</div>`);
      } else {
        _push(`<ul class="list" data-v-3b61f429><!--[-->`);
        ssrRenderList(comments.value, (c) => {
          var _a;
          _push(`<li class="item" data-v-3b61f429><strong data-v-3b61f429>${ssrInterpolate(c.nickname)}</strong><span class="time" data-v-3b61f429>${ssrInterpolate(c.createdAt)}</span>`);
          _push(ssrRenderComponent(_component_SafeHtml, {
            class: "body",
            html: c.contentHtml
          }, null, _parent));
          _push(`<div class="actions" data-v-3b61f429><button type="button" class="like-btn"${ssrIncludeBooleanAttr(likedIds.value.has(c.id)) ? " disabled" : ""} data-v-3b61f429> \u2665 ${ssrInterpolate(c.likeCount)}</button><button type="button" class="reply-btn" data-v-3b61f429>\u56DE\u590D</button></div>`);
          if (replyTo.value === c.id && unref(isAuthenticated)) {
            _push(`<form class="reply-form" data-v-3b61f429><textarea rows="3" placeholder="\u5199\u4E0B\u56DE\u590D\u2026" required data-v-3b61f429>${ssrInterpolate(replyContent.value)}</textarea><button type="submit" data-v-3b61f429>\u63D0\u4EA4\u56DE\u590D</button></form>`);
          } else if (replyTo.value === c.id) {
            _push(`<p class="hint" data-v-3b61f429>`);
            _push(ssrRenderComponent(_component_NuxtLink, { to: "/login" }, {
              default: withCtx((_, _push2, _parent2, _scopeId) => {
                if (_push2) {
                  _push2(`\u767B\u5F55`);
                } else {
                  return [
                    createTextVNode("\u767B\u5F55")
                  ];
                }
              }),
              _: 2
            }, _parent));
            _push(` \u540E\u56DE\u590D </p>`);
          } else {
            _push(`<!---->`);
          }
          if ((_a = c.replies) == null ? void 0 : _a.length) {
            _push(`<ul class="replies" data-v-3b61f429><!--[-->`);
            ssrRenderList(c.replies, (r) => {
              _push(`<li data-v-3b61f429><strong data-v-3b61f429>${ssrInterpolate(r.nickname)}</strong>`);
              _push(ssrRenderComponent(_component_SafeHtml, {
                class: "body",
                html: r.contentHtml
              }, null, _parent));
              _push(`<button type="button" class="like-btn"${ssrIncludeBooleanAttr(likedIds.value.has(r.id)) ? " disabled" : ""} data-v-3b61f429> \u2665 ${ssrInterpolate(r.likeCount)}</button></li>`);
            });
            _push(`<!--]--></ul>`);
          } else {
            _push(`<!---->`);
          }
          _push(`</li>`);
        });
        _push(`<!--]--></ul>`);
      }
      if (!loading.value && !comments.value.length) {
        _push(`<p class="empty" data-v-3b61f429>\u6682\u65E0\u8BC4\u8BBA</p>`);
      } else {
        _push(`<!---->`);
      }
      if (unref(isAuthenticated)) {
        _push(`<form class="form" data-v-3b61f429><textarea rows="4" placeholder="\u5199\u4E0B\u8BC4\u8BBA\u2026" required data-v-3b61f429>${ssrInterpolate(content.value)}</textarea><button type="submit" data-v-3b61f429>\u63D0\u4EA4</button>`);
        if (message.value) {
          _push(`<p class="msg" data-v-3b61f429>${ssrInterpolate(message.value)}</p>`);
        } else {
          _push(`<!---->`);
        }
        _push(`</form>`);
      } else {
        _push(`<p class="login-hint" data-v-3b61f429>`);
        _push(ssrRenderComponent(_component_NuxtLink, { to: "/login" }, {
          default: withCtx((_, _push2, _parent2, _scopeId) => {
            if (_push2) {
              _push2(`\u767B\u5F55`);
            } else {
              return [
                createTextVNode("\u767B\u5F55")
              ];
            }
          }),
          _: 1
        }, _parent));
        _push(` \u540E\u53C2\u4E0E\u8BC4\u8BBA </p>`);
      }
      _push(`</section>`);
    };
  }
});
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("components/comment/CommentSection.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const __nuxt_component_2 = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-3b61f429"]]);

export { __nuxt_component_2 as _ };
//# sourceMappingURL=CommentSection-2fd33290.mjs.map
