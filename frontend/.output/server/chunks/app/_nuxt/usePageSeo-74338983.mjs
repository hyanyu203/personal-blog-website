import { h as useRuntimeConfig } from '../server.mjs';
import { u as useSeoMeta, a as useHead } from './index-95b6f593.mjs';

function usePageSeo(options) {
  const config = /* @__PURE__ */ useRuntimeConfig();
  const siteUrl = config.public.siteUrl.replace(/\/$/, "");
  const path = options.path || "";
  const url = `${siteUrl}${path.startsWith("/") ? path : `/${path}`}`;
  const title = options.title ? `${options.title} - \u6E10\u6784` : "\u6E10\u6784";
  const description = options.description || "\u6E10\u6B21\u6784\u5EFA\uFF0C\u7406\u89E3\u8BA1\u7B97\u673A\u4E16\u754C";
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

export { usePageSeo as u };
//# sourceMappingURL=usePageSeo-74338983.mjs.map
