import { h as useRuntimeConfig } from "../server.mjs";
import { u as useSeoMeta, a as useHead } from "./index-95b6f593.js";
import "vue";
import "destr";
import "devalue";
import "klona";
function usePageSeo(options) {
  const config = /* @__PURE__ */ useRuntimeConfig();
  const siteUrl = config.public.siteUrl.replace(/\/$/, "");
  const path = options.path || "";
  const url = `${siteUrl}${path.startsWith("/") ? path : `/${path}`}`;
  const title = options.title ? `${options.title} - 渐构` : "渐构";
  const description = options.description || "渐次构建，理解计算机世界";
  useSeoMeta({
    title,
    description,
    ogTitle: title,
    ogDescription: description,
    ogUrl: url,
    ogType: options.type || "website",
    twitterCard: "summary",
    twitterTitle: title,
    twitterDescription: description
  });
  useHead({
    link: [{ rel: "canonical", href: url }]
  });
}
export {
  usePageSeo as u
};
//# sourceMappingURL=usePageSeo-74338983.js.map
