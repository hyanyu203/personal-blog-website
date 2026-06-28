import { defineEventHandler, setHeader } from 'h3';
import { u as useRuntimeConfig } from './nitro/node-server.mjs';
import 'node-fetch-native/polyfill';
import 'node:http';
import 'node:https';
import 'destr';
import 'ofetch';
import 'unenv/runtime/fetch/index';
import 'hookable';
import 'scule';
import 'klona';
import 'defu';
import 'ohash';
import 'ufo';
import 'unstorage';
import 'radix3';
import 'node:fs';
import 'node:url';
import 'pathe';

const STATIC_PATHS = [
  "/",
  "/posts",
  "/categories",
  "/tags",
  "/archives",
  "/snippets",
  "/notes",
  "/projects",
  "/friends",
  "/about",
  "/subscribe",
  "/privacy"
];
const sitemap_xml = defineEventHandler(async (event) => {
  var _a;
  const config = useRuntimeConfig();
  const siteUrl = String(config.public.siteUrl || "").replace(/\/$/, "");
  const apiBase = config.apiBaseInternal || config.public.apiBase;
  let articlePaths = [];
  try {
    const res = await $fetch(
      `${apiBase}/articles?page=1&pageSize=100`
    );
    if ((res == null ? void 0 : res.code) === 0 && ((_a = res.data) == null ? void 0 : _a.items)) {
      articlePaths = res.data.items.filter((item) => item.slug).map((item) => `/posts/${item.slug}`);
    }
  } catch {
    articlePaths = [];
  }
  const urls = [...STATIC_PATHS, ...articlePaths];
  const lastmod = (/* @__PURE__ */ new Date()).toISOString().slice(0, 10);
  const body = urls.map((path) => `
  <url>
    <loc>${siteUrl}${path}</loc>
    <lastmod>${lastmod}</lastmod>
  </url>`).join("");
  setHeader(event, "Content-Type", "application/xml; charset=utf-8");
  return `<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">${body}
</urlset>`;
});

export { sitemap_xml as default };
//# sourceMappingURL=sitemap.xml.mjs.map
