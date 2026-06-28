import { executeAsync } from 'unctx';
import { j as defineNuxtRouteMiddleware, g as useAuth, n as navigateTo } from '../server.mjs';
import 'vue';
import 'ofetch';
import 'hookable';
import 'h3';
import '@unhead/ssr';
import 'unhead';
import '@unhead/shared';
import 'vue-router';
import 'ufo';
import 'vue/server-renderer';
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

function safeRedirect(value) {
  if (!value || typeof value !== "string") {
    return "/";
  }
  const trimmed = value.trim();
  if (!trimmed.startsWith("/") || trimmed.startsWith("//")) {
    return "/";
  }
  if (trimmed.includes("://") || trimmed.includes("\\")) {
    return "/";
  }
  try {
    const decoded = decodeURIComponent(trimmed);
    if (decoded.startsWith("//") || decoded.includes("://") || decoded.includes("\\")) {
      return "/";
    }
  } catch {
    return "/";
  }
  return trimmed;
}
const guestOnly = /* @__PURE__ */ defineNuxtRouteMiddleware(async (to) => {
  let __temp, __restore;
  const { isAuthenticated, loaded, restoreSession } = useAuth();
  if (!loaded.value) {
    [__temp, __restore] = executeAsync(() => restoreSession()), await __temp, __restore();
  }
  if (isAuthenticated.value) {
    const redirect = typeof to.query.redirect === "string" ? to.query.redirect : void 0;
    return navigateTo(safeRedirect(redirect));
  }
});

export { guestOnly as default };
//# sourceMappingURL=guest-only-c17cd15d.mjs.map
