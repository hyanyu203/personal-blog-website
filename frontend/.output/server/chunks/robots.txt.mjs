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

const robots_txt = defineEventHandler((event) => {
  const config = useRuntimeConfig();
  const siteUrl = String(config.public.siteUrl || "").replace(/\/$/, "");
  setHeader(event, "Content-Type", "text/plain; charset=utf-8");
  return [
    "User-agent: *",
    "Allow: /",
    "Disallow: /login",
    "Disallow: /register",
    "Disallow: /forgot-password",
    `Sitemap: ${siteUrl}/sitemap.xml`
  ].join("\n");
});

export { robots_txt as default };
//# sourceMappingURL=robots.txt.mjs.map
