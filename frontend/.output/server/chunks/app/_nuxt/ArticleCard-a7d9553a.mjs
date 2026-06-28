import { _ as _export_sfc, d as __nuxt_component_0$1 } from '../server.mjs';
import { useSSRContext, defineComponent, mergeProps, withCtx, createTextVNode, toDisplayString } from 'vue';
import { ssrRenderAttrs, ssrRenderComponent, ssrInterpolate, ssrRenderList } from 'vue/server-renderer';

const _sfc_main = /* @__PURE__ */ defineComponent({
  __name: "ArticleCard",
  __ssrInlineRender: true,
  props: {
    article: {}
  },
  setup(__props) {
    function formatDate(iso) {
      return new Date(iso).toLocaleDateString("zh-CN");
    }
    return (_ctx, _push, _parent, _attrs) => {
      const _component_NuxtLink = __nuxt_component_0$1;
      _push(`<article${ssrRenderAttrs(mergeProps({ class: "card" }, _attrs))} data-v-3736283c><h2 data-v-3736283c>`);
      if (_ctx.article.pinned) {
        _push(`<span class="pinned" data-v-3736283c>\u7F6E\u9876</span>`);
      } else {
        _push(`<!---->`);
      }
      _push(ssrRenderComponent(_component_NuxtLink, {
        to: `/posts/${_ctx.article.slug}`
      }, {
        default: withCtx((_, _push2, _parent2, _scopeId) => {
          if (_push2) {
            _push2(`${ssrInterpolate(_ctx.article.title)}`);
          } else {
            return [
              createTextVNode(toDisplayString(_ctx.article.title), 1)
            ];
          }
        }),
        _: 1
      }, _parent));
      _push(`</h2><p class="summary" data-v-3736283c>${ssrInterpolate(_ctx.article.summary)}</p><div class="meta" data-v-3736283c>`);
      if (_ctx.article.category) {
        _push(`<span data-v-3736283c>${ssrInterpolate(_ctx.article.category.name)}</span>`);
      } else {
        _push(`<!---->`);
      }
      if (_ctx.article.publishedAt) {
        _push(`<span data-v-3736283c>${ssrInterpolate(formatDate(_ctx.article.publishedAt))}</span>`);
      } else {
        _push(`<!---->`);
      }
      _push(`<span data-v-3736283c>${ssrInterpolate(_ctx.article.readingMinutes)} \u5206\u949F \xB7 ${ssrInterpolate(_ctx.article.viewCount)} \u9605\u8BFB</span><!--[-->`);
      ssrRenderList(_ctx.article.tags, (tag) => {
        _push(`<span class="tag" data-v-3736283c>${ssrInterpolate(tag)}</span>`);
      });
      _push(`<!--]--></div></article>`);
    };
  }
});
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("components/article/ArticleCard.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const __nuxt_component_0 = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-3736283c"]]);

export { __nuxt_component_0 as _ };
//# sourceMappingURL=ArticleCard-a7d9553a.mjs.map
