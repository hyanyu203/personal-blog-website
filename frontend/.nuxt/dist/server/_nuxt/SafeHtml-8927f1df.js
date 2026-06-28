import { defineComponent, computed, createVNode, resolveDynamicComponent, useSSRContext } from "vue";
import { ssrRenderVNode } from "vue/server-renderer";
import DOMPurify from "isomorphic-dompurify";
const HTML_SANITIZE_CONFIG = {
  ALLOWED_TAGS: [
    "p",
    "br",
    "strong",
    "em",
    "u",
    "s",
    "del",
    "ins",
    "sub",
    "sup",
    "a",
    "ul",
    "ol",
    "li",
    "blockquote",
    "pre",
    "code",
    "hr",
    "h1",
    "h2",
    "h3",
    "h4",
    "h5",
    "h6",
    "img",
    "table",
    "thead",
    "tbody",
    "tr",
    "th",
    "td"
  ],
  ALLOWED_ATTR: ["href", "title", "target", "rel", "class", "src", "alt", "width", "height"],
  ALLOW_DATA_ATTR: false
};
const ALLOWED_URI = /^(https?:|mailto:)/i;
function sanitizeUrlAttr(node, attr) {
  const value = node.getAttribute(attr);
  if (!value) {
    return;
  }
  if (!ALLOWED_URI.test(value.trim())) {
    node.removeAttribute(attr);
  }
}
function applyDomPurifyHooks(DOMPurify2) {
  DOMPurify2.addHook("afterSanitizeAttributes", (node) => {
    if (node.tagName === "A") {
      sanitizeUrlAttr(node, "href");
      if (node.getAttribute("target") === "_blank") {
        node.setAttribute("rel", "noopener noreferrer");
      }
    }
    if (node.tagName === "IMG") {
      sanitizeUrlAttr(node, "src");
    }
  });
}
applyDomPurifyHooks(DOMPurify);
function sanitizeHtml(html) {
  if (!html)
    return "";
  return DOMPurify.sanitize(html, HTML_SANITIZE_CONFIG);
}
const _sfc_main = /* @__PURE__ */ defineComponent({
  __name: "SafeHtml",
  __ssrInlineRender: true,
  props: {
    html: {},
    tag: { default: "div" }
  },
  setup(__props) {
    const props = __props;
    computed(() => sanitizeHtml(props.html));
    return (_ctx, _push, _parent, _attrs) => {
      ssrRenderVNode(_push, createVNode(resolveDynamicComponent(_ctx.tag), _attrs, null), _parent);
    };
  }
});
const _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
  const ssrContext = useSSRContext();
  (ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("components/SafeHtml.vue");
  return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
export {
  _sfc_main as _
};
//# sourceMappingURL=SafeHtml-8927f1df.js.map
