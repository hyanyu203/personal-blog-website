import { _ as _sfc_main$1 } from "./SafeHtml-8927f1df.js";
import { e as useRoute, b as useAsyncData, g as useAuth, h as useRuntimeConfig, d as __nuxt_component_0, _ as _export_sfc } from "../server.mjs";
import { _ as __nuxt_component_2 } from "./CommentSection-2fd33290.js";
import { u as useSeoMeta, a as useHead } from "./index-95b6f593.js";
import { defineComponent, withAsyncContext, computed, ref, watch, unref, mergeProps, withCtx, createTextVNode, toDisplayString, useSSRContext } from "vue";
import "destr";
import "devalue";
import "klona";
import { u as usePageSeo } from "./usePageSeo-74338983.js";
import { ssrRenderAttrs, ssrInterpolate, ssrIncludeBooleanAttr, ssrRenderComponent, ssrRenderList, ssrRenderAttr, ssrRenderStyle } from "vue/server-renderer";
import { a as fetchArticleBySlug, b as fetchArticleToc, c as fetchRelatedArticles } from "./article.api-3cf8cb60.js";
import "isomorphic-dompurify";
import "ofetch";
import "#internal/nitro";
import "hookable";
import "unctx";
import "h3";
import "@unhead/ssr";
import "unhead";
import "@unhead/shared";
import "vue-router";
import "ufo";
import "defu";
const _sfc_main = /* @__PURE__ */ defineComponent({
  __name: "[slug]",
  __ssrInlineRender: true,
  async setup(__props) {
    let __temp, __restore;
    const route = useRoute();
    const slug = route.params.slug;
    const { data: articleBundle, pending } = ([__temp, __restore] = withAsyncContext(async () => useAsyncData(
      `article-${slug}`,
      async () => {
        const article2 = await fetchArticleBySlug(slug);
        if (!(article2 == null ? void 0 : article2.id)) {
          return { article: null, toc: [], related: [] };
        }
        const [toc2, related2] = await Promise.all([
          fetchArticleToc(article2.id),
          fetchRelatedArticles(article2.id)
        ]);
        return { article: article2, toc: toc2, related: related2 };
      }
    )), __temp = await __temp, __restore(), __temp);
    const article = computed(() => {
      var _a;
      return ((_a = articleBundle.value) == null ? void 0 : _a.article) ?? null;
    });
    const toc = computed(() => {
      var _a;
      return ((_a = articleBundle.value) == null ? void 0 : _a.toc) ?? [];
    });
    const related = computed(() => {
      var _a;
      return ((_a = articleBundle.value) == null ? void 0 : _a.related) ?? [];
    });
    const liked = ref(false);
    const likeCount = ref(0);
    watch(article, (a) => {
      if (a) {
        likeCount.value = a.likeCount ?? 0;
      }
    }, { immediate: true });
    useAuth();
    useSeoMeta({
      title: () => article.value ? `${article.value.title} - 渐构` : "渐构",
      description: () => {
        var _a;
        return ((_a = article.value) == null ? void 0 : _a.summary) || "";
      }
    });
    watch(article, (value) => {
      if (!value)
        return;
      usePageSeo({
        title: value.title,
        description: value.summary || "",
        path: `/posts/${value.slug}`,
        type: "article"
      });
    }, { immediate: true });
    const config = /* @__PURE__ */ useRuntimeConfig();
    useHead({
      link: computed(() => article.value ? [{
        rel: "webmention",
        href: `${config.public.apiBase}/webmention`
      }] : [])
    });
    return (_ctx, _push, _parent, _attrs) => {
      const _component_SafeHtml = _sfc_main$1;
      const _component_NuxtLink = __nuxt_component_0;
      const _component_CommentSection = __nuxt_component_2;
      if (unref(article)) {
        _push(`<div${ssrRenderAttrs(mergeProps({ class: "article-layout" }, _attrs))} data-v-224ab30f><article class="main" data-v-224ab30f><header class="header" data-v-224ab30f><h1 data-v-224ab30f>${ssrInterpolate(unref(article).title)}</h1><p class="meta" data-v-224ab30f>${ssrInterpolate(unref(article).readingMinutes)} 分钟 · ${ssrInterpolate(unref(article).wordCount)} 字 · ${ssrInterpolate(unref(article).viewCount)} 次阅读 <button type="button" class="like-btn"${ssrIncludeBooleanAttr(unref(liked)) ? " disabled" : ""} data-v-224ab30f>${ssrInterpolate(unref(liked) ? "已赞" : "点赞")} ${ssrInterpolate(unref(likeCount))}</button></p>`);
        if (unref(article).summary) {
          _push(`<p class="summary" data-v-224ab30f>${ssrInterpolate(unref(article).summary)}</p>`);
        } else {
          _push(`<!---->`);
        }
        _push(`</header>`);
        _push(ssrRenderComponent(_component_SafeHtml, {
          class: "article-content",
          html: unref(article).contentHtml
        }, null, _parent));
        if (unref(related).length) {
          _push(`<section class="related" data-v-224ab30f><h2 data-v-224ab30f>相关文章</h2><ul data-v-224ab30f><!--[-->`);
          ssrRenderList(unref(related), (item) => {
            _push(`<li data-v-224ab30f>`);
            _push(ssrRenderComponent(_component_NuxtLink, {
              to: `/posts/${item.slug}`
            }, {
              default: withCtx((_, _push2, _parent2, _scopeId) => {
                if (_push2) {
                  _push2(`${ssrInterpolate(item.title)}`);
                } else {
                  return [
                    createTextVNode(toDisplayString(item.title), 1)
                  ];
                }
              }),
              _: 2
            }, _parent));
            _push(`</li>`);
          });
          _push(`<!--]--></ul></section>`);
        } else {
          _push(`<!---->`);
        }
        if (unref(article).id) {
          _push(ssrRenderComponent(_component_CommentSection, {
            "target-type": "article",
            "target-id": unref(article).id
          }, null, _parent));
        } else {
          _push(`<!---->`);
        }
        _push(`</article>`);
        if (unref(toc).length) {
          _push(`<aside class="toc" data-v-224ab30f><h2 data-v-224ab30f>目录</h2><nav data-v-224ab30f><!--[-->`);
          ssrRenderList(unref(toc), (item) => {
            _push(`<a${ssrRenderAttr("href", `#${item.id}`)} style="${ssrRenderStyle({ paddingLeft: `${(item.level - 1) * 0.75}rem` })}" data-v-224ab30f>${ssrInterpolate(item.text)}</a>`);
          });
          _push(`<!--]--></nav></aside>`);
        } else {
          _push(`<!---->`);
        }
        _push(`</div>`);
      } else if (unref(pending)) {
        _push(`<div${ssrRenderAttrs(_attrs)} data-v-224ab30f>加载中…</div>`);
      } else {
        _push(`<div${ssrRenderAttrs(_attrs)} data-v-224ab30f>文章不存在</div>`);
      }
    };
  }
});
const _slug__vue_vue_type_style_index_0_scoped_224ab30f_lang = "";
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/posts/[slug].vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
const _slug_ = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-224ab30f"]]);
export {
  _slug_ as default
};
//# sourceMappingURL=_slug_-0c0b1fe5.js.map
