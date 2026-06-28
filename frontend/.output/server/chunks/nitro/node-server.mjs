globalThis._importMeta_=globalThis._importMeta_||{url:"file:///_entry.js",env:process.env};import 'node-fetch-native/polyfill';
import { Server as Server$1 } from 'node:http';
import { Server } from 'node:https';
import destr from 'destr';
import { defineEventHandler, handleCacheHeaders, createEvent, eventHandler, setHeaders, sendRedirect, proxyRequest, getRequestHeader, setResponseStatus, setResponseHeader, getRequestHeaders, createError, createApp, createRouter as createRouter$1, toNodeListener, fetchWithEvent, lazyEventHandler } from 'h3';
import { createFetch as createFetch$1, Headers } from 'ofetch';
import { createCall, createFetch } from 'unenv/runtime/fetch/index';
import { createHooks } from 'hookable';
import { snakeCase } from 'scule';
import { klona } from 'klona';
import defu, { defuFn } from 'defu';
import { hash } from 'ohash';
import { parseURL, withoutBase, joinURL, getQuery, withQuery, withLeadingSlash, withoutTrailingSlash } from 'ufo';
import { createStorage, prefixStorage } from 'unstorage';
import { toRouteMatcher, createRouter } from 'radix3';
import { promises } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { dirname, resolve } from 'pathe';

const inlineAppConfig = {};



const appConfig = defuFn(inlineAppConfig);

const _inlineRuntimeConfig = {
  "app": {
    "baseURL": "/",
    "buildAssetsDir": "/_nuxt/",
    "cdnURL": ""
  },
  "nitro": {
    "envPrefix": "NUXT_",
    "routeRules": {
      "/__nuxt_error": {
        "cache": false
      },
      "/": {
        "swr": 3600,
        "cache": {
          "swr": true,
          "maxAge": 3600
        }
      },
      "/posts/**": {
        "swr": 3600,
        "cache": {
          "swr": true,
          "maxAge": 3600
        }
      },
      "/categories/**": {
        "swr": 3600,
        "cache": {
          "swr": true,
          "maxAge": 3600
        }
      },
      "/tags/**": {
        "swr": 3600,
        "cache": {
          "swr": true,
          "maxAge": 3600
        }
      },
      "/archives": {
        "swr": 3600,
        "cache": {
          "swr": true,
          "maxAge": 3600
        }
      },
      "/snippets/**": {
        "swr": 3600,
        "cache": {
          "swr": true,
          "maxAge": 3600
        }
      },
      "/notes/**": {
        "swr": 3600,
        "cache": {
          "swr": true,
          "maxAge": 3600
        }
      },
      "/subscribe/confirm": {
        "headers": {
          "X-Frame-Options": "DENY",
          "X-Content-Type-Options": "nosniff",
          "Referrer-Policy": "no-referrer"
        }
      },
      "/subscribe/unsubscribe": {
        "headers": {
          "X-Frame-Options": "DENY",
          "X-Content-Type-Options": "nosniff",
          "Referrer-Policy": "no-referrer"
        }
      },
      "/**": {
        "headers": {
          "X-Frame-Options": "DENY",
          "X-Content-Type-Options": "nosniff",
          "Referrer-Policy": "strict-origin-when-cross-origin"
        }
      },
      "/_nuxt/**": {
        "headers": {
          "cache-control": "public, max-age=31536000, immutable"
        }
      }
    }
  },
  "public": {
    "apiBase": "/api/v1",
    "siteUrl": "http://localhost:3000"
  },
  "apiBaseInternal": ""
};
const ENV_PREFIX = "NITRO_";
const ENV_PREFIX_ALT = _inlineRuntimeConfig.nitro.envPrefix ?? process.env.NITRO_ENV_PREFIX ?? "_";
const _sharedRuntimeConfig = _deepFreeze(
  _applyEnv(klona(_inlineRuntimeConfig))
);
function useRuntimeConfig(event) {
  if (!event) {
    return _sharedRuntimeConfig;
  }
  if (event.context.nitro.runtimeConfig) {
    return event.context.nitro.runtimeConfig;
  }
  const runtimeConfig = klona(_inlineRuntimeConfig);
  _applyEnv(runtimeConfig);
  event.context.nitro.runtimeConfig = runtimeConfig;
  return runtimeConfig;
}
_deepFreeze(klona(appConfig));
function _getEnv(key) {
  const envKey = snakeCase(key).toUpperCase();
  return destr(
    process.env[ENV_PREFIX + envKey] ?? process.env[ENV_PREFIX_ALT + envKey]
  );
}
function _isObject(input) {
  return typeof input === "object" && !Array.isArray(input);
}
function _applyEnv(obj, parentKey = "") {
  for (const key in obj) {
    const subKey = parentKey ? `${parentKey}_${key}` : key;
    const envValue = _getEnv(subKey);
    if (_isObject(obj[key])) {
      if (_isObject(envValue)) {
        obj[key] = { ...obj[key], ...envValue };
      }
      _applyEnv(obj[key], subKey);
    } else {
      obj[key] = envValue ?? obj[key];
    }
  }
  return obj;
}
function _deepFreeze(object) {
  const propNames = Object.getOwnPropertyNames(object);
  for (const name of propNames) {
    const value = object[name];
    if (value && typeof value === "object") {
      _deepFreeze(value);
    }
  }
  return Object.freeze(object);
}
new Proxy(/* @__PURE__ */ Object.create(null), {
  get: (_, prop) => {
    console.warn(
      "Please use `useRuntimeConfig()` instead of accessing config directly."
    );
    const runtimeConfig = useRuntimeConfig();
    if (prop in runtimeConfig) {
      return runtimeConfig[prop];
    }
    return void 0;
  }
});

const _assets = {

};

function normalizeKey(key) {
  if (!key) {
    return "";
  }
  return key.split("?")[0].replace(/[/\\]/g, ":").replace(/:+/g, ":").replace(/^:|:$/g, "");
}

const assets$1 = {
  getKeys() {
    return Promise.resolve(Object.keys(_assets))
  },
  hasItem (id) {
    id = normalizeKey(id);
    return Promise.resolve(id in _assets)
  },
  getItem (id) {
    id = normalizeKey(id);
    return Promise.resolve(_assets[id] ? _assets[id].import() : null)
  },
  getMeta (id) {
    id = normalizeKey(id);
    return Promise.resolve(_assets[id] ? _assets[id].meta : {})
  }
};

const storage = createStorage({});

storage.mount('/assets', assets$1);

function useStorage(base = "") {
  return base ? prefixStorage(storage, base) : storage;
}

const defaultCacheOptions = {
  name: "_",
  base: "/cache",
  swr: true,
  maxAge: 1
};
function defineCachedFunction(fn, opts = {}) {
  opts = { ...defaultCacheOptions, ...opts };
  const pending = {};
  const group = opts.group || "nitro/functions";
  const name = opts.name || fn.name || "_";
  const integrity = hash([opts.integrity, fn, opts]);
  const validate = opts.validate || (() => true);
  async function get(key, resolver, shouldInvalidateCache) {
    const cacheKey = [opts.base, group, name, key + ".json"].filter(Boolean).join(":").replace(/:\/$/, ":index");
    const entry = await useStorage().getItem(cacheKey) || {};
    const ttl = (opts.maxAge ?? opts.maxAge ?? 0) * 1e3;
    if (ttl) {
      entry.expires = Date.now() + ttl;
    }
    const expired = shouldInvalidateCache || entry.integrity !== integrity || ttl && Date.now() - (entry.mtime || 0) > ttl || !validate(entry);
    const _resolve = async () => {
      const isPending = pending[key];
      if (!isPending) {
        if (entry.value !== void 0 && (opts.staleMaxAge || 0) >= 0 && opts.swr === false) {
          entry.value = void 0;
          entry.integrity = void 0;
          entry.mtime = void 0;
          entry.expires = void 0;
        }
        pending[key] = Promise.resolve(resolver());
      }
      try {
        entry.value = await pending[key];
      } catch (error) {
        if (!isPending) {
          delete pending[key];
        }
        throw error;
      }
      if (!isPending) {
        entry.mtime = Date.now();
        entry.integrity = integrity;
        delete pending[key];
        if (validate(entry)) {
          useStorage().setItem(cacheKey, entry).catch((error) => console.error("[nitro] [cache]", error));
        }
      }
    };
    const _resolvePromise = expired ? _resolve() : Promise.resolve();
    if (opts.swr && entry.value) {
      _resolvePromise.catch(console.error);
      return entry;
    }
    return _resolvePromise.then(() => entry);
  }
  return async (...args) => {
    const shouldBypassCache = opts.shouldBypassCache?.(...args);
    if (shouldBypassCache) {
      return fn(...args);
    }
    const key = await (opts.getKey || getKey)(...args);
    const shouldInvalidateCache = opts.shouldInvalidateCache?.(...args);
    const entry = await get(key, () => fn(...args), shouldInvalidateCache);
    let value = entry.value;
    if (opts.transform) {
      value = await opts.transform(entry, ...args) || value;
    }
    return value;
  };
}
const cachedFunction = defineCachedFunction;
function getKey(...args) {
  return args.length > 0 ? hash(args, {}) : "";
}
function escapeKey(key) {
  return key.replace(/[^\dA-Za-z]/g, "");
}
function defineCachedEventHandler(handler, opts = defaultCacheOptions) {
  const _opts = {
    ...opts,
    getKey: async (event) => {
      const key = await opts.getKey?.(event);
      if (key) {
        return escapeKey(key);
      }
      const url = event.node.req.originalUrl || event.node.req.url;
      const friendlyName = escapeKey(decodeURI(parseURL(url).pathname)).slice(
        0,
        16
      );
      const urlHash = hash(url);
      return `${friendlyName}.${urlHash}`;
    },
    validate: (entry) => {
      if (entry.value.code >= 400) {
        return false;
      }
      if (entry.value.body === void 0) {
        return false;
      }
      return true;
    },
    group: opts.group || "nitro/handlers",
    integrity: [opts.integrity, handler]
  };
  const _cachedHandler = cachedFunction(
    async (incomingEvent) => {
      const reqProxy = cloneWithProxy(incomingEvent.node.req, { headers: {} });
      const resHeaders = {};
      let _resSendBody;
      const resProxy = cloneWithProxy(incomingEvent.node.res, {
        statusCode: 200,
        getHeader(name) {
          return resHeaders[name];
        },
        setHeader(name, value) {
          resHeaders[name] = value;
          return this;
        },
        getHeaderNames() {
          return Object.keys(resHeaders);
        },
        hasHeader(name) {
          return name in resHeaders;
        },
        removeHeader(name) {
          delete resHeaders[name];
        },
        getHeaders() {
          return resHeaders;
        },
        end(chunk, arg2, arg3) {
          if (typeof chunk === "string") {
            _resSendBody = chunk;
          }
          if (typeof arg2 === "function") {
            arg2();
          }
          if (typeof arg3 === "function") {
            arg3();
          }
          return this;
        },
        write(chunk, arg2, arg3) {
          if (typeof chunk === "string") {
            _resSendBody = chunk;
          }
          if (typeof arg2 === "function") {
            arg2();
          }
          if (typeof arg3 === "function") {
            arg3();
          }
          return this;
        },
        writeHead(statusCode, headers2) {
          this.statusCode = statusCode;
          if (headers2) {
            for (const header in headers2) {
              this.setHeader(header, headers2[header]);
            }
          }
          return this;
        }
      });
      const event = createEvent(reqProxy, resProxy);
      event.context = incomingEvent.context;
      const body = await handler(event) || _resSendBody;
      const headers = event.node.res.getHeaders();
      headers.etag = headers.Etag || headers.etag || `W/"${hash(body)}"`;
      headers["last-modified"] = headers["Last-Modified"] || headers["last-modified"] || (/* @__PURE__ */ new Date()).toUTCString();
      const cacheControl = [];
      if (opts.swr) {
        if (opts.maxAge) {
          cacheControl.push(`s-maxage=${opts.maxAge}`);
        }
        if (opts.staleMaxAge) {
          cacheControl.push(`stale-while-revalidate=${opts.staleMaxAge}`);
        } else {
          cacheControl.push("stale-while-revalidate");
        }
      } else if (opts.maxAge) {
        cacheControl.push(`max-age=${opts.maxAge}`);
      }
      if (cacheControl.length > 0) {
        headers["cache-control"] = cacheControl.join(", ");
      }
      const cacheEntry = {
        code: event.node.res.statusCode,
        headers,
        body
      };
      return cacheEntry;
    },
    _opts
  );
  return defineEventHandler(async (event) => {
    if (opts.headersOnly) {
      if (handleCacheHeaders(event, { maxAge: opts.maxAge })) {
        return;
      }
      return handler(event);
    }
    const response = await _cachedHandler(event);
    if (event.node.res.headersSent || event.node.res.writableEnded) {
      return response.body;
    }
    if (handleCacheHeaders(event, {
      modifiedTime: new Date(response.headers["last-modified"]),
      etag: response.headers.etag,
      maxAge: opts.maxAge
    })) {
      return;
    }
    event.node.res.statusCode = response.code;
    for (const name in response.headers) {
      event.node.res.setHeader(name, response.headers[name]);
    }
    return response.body;
  });
}
function cloneWithProxy(obj, overrides) {
  return new Proxy(obj, {
    get(target, property, receiver) {
      if (property in overrides) {
        return overrides[property];
      }
      return Reflect.get(target, property, receiver);
    },
    set(target, property, value, receiver) {
      if (property in overrides) {
        overrides[property] = value;
        return true;
      }
      return Reflect.set(target, property, value, receiver);
    }
  });
}
const cachedEventHandler = defineCachedEventHandler;

const config = useRuntimeConfig();
const _routeRulesMatcher = toRouteMatcher(
  createRouter({ routes: config.nitro.routeRules })
);
function createRouteRulesHandler() {
  return eventHandler((event) => {
    const routeRules = getRouteRules(event);
    if (routeRules.headers) {
      setHeaders(event, routeRules.headers);
    }
    if (routeRules.redirect) {
      return sendRedirect(
        event,
        routeRules.redirect.to,
        routeRules.redirect.statusCode
      );
    }
    if (routeRules.proxy) {
      let target = routeRules.proxy.to;
      if (target.endsWith("/**")) {
        let targetPath = event.path;
        const strpBase = routeRules.proxy._proxyStripBase;
        if (strpBase) {
          targetPath = withoutBase(targetPath, strpBase);
        }
        target = joinURL(target.slice(0, -3), targetPath);
      } else if (event.path.includes("?")) {
        const query = getQuery(event.path);
        target = withQuery(target, query);
      }
      return proxyRequest(event, target, {
        fetch: $fetch.raw,
        ...routeRules.proxy
      });
    }
  });
}
function getRouteRules(event) {
  event.context._nitro = event.context._nitro || {};
  if (!event.context._nitro.routeRules) {
    const path = new URL(event.node.req.url, "http://localhost").pathname;
    event.context._nitro.routeRules = getRouteRulesForPath(
      withoutBase(path, useRuntimeConfig().app.baseURL)
    );
  }
  return event.context._nitro.routeRules;
}
function getRouteRulesForPath(path) {
  return defu({}, ..._routeRulesMatcher.matchAll(path).reverse());
}

const plugins = [
  
];

function hasReqHeader(event, name, includes) {
  const value = getRequestHeader(event, name);
  return value && typeof value === "string" && value.toLowerCase().includes(includes);
}
function isJsonRequest(event) {
  return hasReqHeader(event, "accept", "application/json") || hasReqHeader(event, "user-agent", "curl/") || hasReqHeader(event, "user-agent", "httpie/") || hasReqHeader(event, "sec-fetch-mode", "cors") || event.path.startsWith("/api/") || event.path.endsWith(".json");
}
function normalizeError(error) {
  const cwd = typeof process.cwd === "function" ? process.cwd() : "/";
  const stack = (error.stack || "").split("\n").splice(1).filter((line) => line.includes("at ")).map((line) => {
    const text = line.replace(cwd + "/", "./").replace("webpack:/", "").replace("file://", "").trim();
    return {
      text,
      internal: line.includes("node_modules") && !line.includes(".cache") || line.includes("internal") || line.includes("new Promise")
    };
  });
  const statusCode = error.statusCode || 500;
  const statusMessage = error.statusMessage ?? (statusCode === 404 ? "Not Found" : "");
  const message = error.message || error.toString();
  return {
    stack,
    statusCode,
    statusMessage,
    message
  };
}

const errorHandler = (async function errorhandler(error, event) {
  const { stack, statusCode, statusMessage, message } = normalizeError(error);
  const errorObject = {
    url: event.node.req.url,
    statusCode,
    statusMessage,
    message,
    stack: "",
    data: error.data
  };
  setResponseStatus(event, errorObject.statusCode !== 200 && errorObject.statusCode || 500, errorObject.statusMessage);
  if (error.unhandled || error.fatal) {
    const tags = [
      "[nuxt]",
      "[request error]",
      error.unhandled && "[unhandled]",
      error.fatal && "[fatal]",
      Number(errorObject.statusCode) !== 200 && `[${errorObject.statusCode}]`
    ].filter(Boolean).join(" ");
    console.error(tags, errorObject.message + "\n" + stack.map((l) => "  " + l.text).join("  \n"));
  }
  if (isJsonRequest(event)) {
    setResponseHeader(event, "Content-Type", "application/json");
    event.node.res.end(JSON.stringify(errorObject));
    return;
  }
  const isErrorPage = event.node.req.url?.startsWith("/__nuxt_error");
  const res = !isErrorPage ? await useNitroApp().localFetch(withQuery(joinURL(useRuntimeConfig().app.baseURL, "/__nuxt_error"), errorObject), {
    headers: getRequestHeaders(event),
    redirect: "manual"
  }).catch(() => null) : null;
  if (!res) {
    const { template } = await import('../error-500.mjs');
    setResponseHeader(event, "Content-Type", "text/html;charset=UTF-8");
    event.node.res.end(template(errorObject));
    return;
  }
  for (const [header, value] of res.headers.entries()) {
    setResponseHeader(event, header, value);
  }
  setResponseStatus(event, res.status && res.status !== 200 ? res.status : void 0, res.statusText);
  event.node.res.end(await res.text());
});

const assets = {
  "/_nuxt/about.1148fb68.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"54-/xXrDxlpGxQu7TaGmIWhx0mLHkA\"",
    "mtime": "2026-06-28T10:12:50.966Z",
    "size": 84,
    "path": "../public/_nuxt/about.1148fb68.css"
  },
  "/_nuxt/about.ea888ac3.js": {
    "type": "application/javascript",
    "etag": "\"40e-x8Uk7fob9zjNhY6hYpoX7aT/+VU\"",
    "mtime": "2026-06-28T10:12:50.981Z",
    "size": 1038,
    "path": "../public/_nuxt/about.ea888ac3.js"
  },
  "/_nuxt/archives.22a33fd1.js": {
    "type": "application/javascript",
    "etag": "\"40a-UdcwGqs3jGaMUO74cyFpS/qjtdo\"",
    "mtime": "2026-06-28T10:12:50.982Z",
    "size": 1034,
    "path": "../public/_nuxt/archives.22a33fd1.js"
  },
  "/_nuxt/archives.2de74143.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"7f-l7ZbBxAe2CpigoyVGWT8P/4WbgI\"",
    "mtime": "2026-06-28T10:12:50.967Z",
    "size": 127,
    "path": "../public/_nuxt/archives.2de74143.css"
  },
  "/_nuxt/article.api.a270a7da.js": {
    "type": "application/javascript",
    "etag": "\"204-TgQTYgbUzsw3Ufqw17hTUWq2AXw\"",
    "mtime": "2026-06-28T10:12:50.975Z",
    "size": 516,
    "path": "../public/_nuxt/article.api.a270a7da.js"
  },
  "/_nuxt/ArticleCard.6ae86347.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"2e4-UdBVh1c4+K9NuIjOAN1+qZrkkSw\"",
    "mtime": "2026-06-28T10:12:50.975Z",
    "size": 740,
    "path": "../public/_nuxt/ArticleCard.6ae86347.css"
  },
  "/_nuxt/ArticleCard.8978d1f9.js": {
    "type": "application/javascript",
    "etag": "\"3e4-g2Zp8RNQfpPS85yym/ucYAAOPOU\"",
    "mtime": "2026-06-28T10:12:50.982Z",
    "size": 996,
    "path": "../public/_nuxt/ArticleCard.8978d1f9.js"
  },
  "/_nuxt/CommentSection.e3113042.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"41c-4UOt5q6TLtK0Nomf3ZHK/20We+I\"",
    "mtime": "2026-06-28T10:12:50.967Z",
    "size": 1052,
    "path": "../public/_nuxt/CommentSection.e3113042.css"
  },
  "/_nuxt/CommentSection.f7539da6.js": {
    "type": "application/javascript",
    "etag": "\"eb1-TW3sSnbYvi1i5G5ng5h7wuEDw30\"",
    "mtime": "2026-06-28T10:12:50.982Z",
    "size": 3761,
    "path": "../public/_nuxt/CommentSection.f7539da6.js"
  },
  "/_nuxt/confirm.251499b5.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"35-1ks17AGwL6euttIjJgswckX5JrE\"",
    "mtime": "2026-06-28T10:12:50.969Z",
    "size": 53,
    "path": "../public/_nuxt/confirm.251499b5.css"
  },
  "/_nuxt/confirm.d267ed64.js": {
    "type": "application/javascript",
    "etag": "\"396-B9TN+qut8wsPArIIHiYCIeiPEQg\"",
    "mtime": "2026-06-28T10:12:50.981Z",
    "size": 918,
    "path": "../public/_nuxt/confirm.d267ed64.js"
  },
  "/_nuxt/entry.4d6996de.js": {
    "type": "application/javascript",
    "etag": "\"28327-SfWqGDdXDve28i8RUkLpGrHFcGs\"",
    "mtime": "2026-06-28T10:12:50.987Z",
    "size": 164647,
    "path": "../public/_nuxt/entry.4d6996de.js"
  },
  "/_nuxt/entry.7c34133c.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"911-gNg330Hs1WfGHU6XMduBD4P5Ba0\"",
    "mtime": "2026-06-28T10:12:50.967Z",
    "size": 2321,
    "path": "../public/_nuxt/entry.7c34133c.css"
  },
  "/_nuxt/error-404.de833bce.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"dc9-0ZazUCyQvfmF01FSH3FoC5l9yJ0\"",
    "mtime": "2026-06-28T10:12:50.975Z",
    "size": 3529,
    "path": "../public/_nuxt/error-404.de833bce.css"
  },
  "/_nuxt/error-404.e054aa7b.js": {
    "type": "application/javascript",
    "etag": "\"c33-nX0k1XK4QIr4Nt6Ymwz7qG0OSV4\"",
    "mtime": "2026-06-28T10:12:50.985Z",
    "size": 3123,
    "path": "../public/_nuxt/error-404.e054aa7b.js"
  },
  "/_nuxt/error-500.7237675c.js": {
    "type": "application/javascript",
    "etag": "\"ae1-HBwFGD6teejlfxo71CFgexR/YZE\"",
    "mtime": "2026-06-28T10:12:50.986Z",
    "size": 2785,
    "path": "../public/_nuxt/error-500.7237675c.js"
  },
  "/_nuxt/error-500.88db509d.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"75c-Juu+xpvMf6y/oBf0WsXvPEH0ie4\"",
    "mtime": "2026-06-28T10:12:50.975Z",
    "size": 1884,
    "path": "../public/_nuxt/error-500.88db509d.css"
  },
  "/_nuxt/error-component.f167511d.js": {
    "type": "application/javascript",
    "etag": "\"498-hCCQUsUSp21yF5gPb+Fg+c2EHO8\"",
    "mtime": "2026-06-28T10:12:50.975Z",
    "size": 1176,
    "path": "../public/_nuxt/error-component.f167511d.js"
  },
  "/_nuxt/forgot-password.50c540e1.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"52f-42VvP3VaFgVjSyw3F8gZu6Ze7uY\"",
    "mtime": "2026-06-28T10:12:50.967Z",
    "size": 1327,
    "path": "../public/_nuxt/forgot-password.50c540e1.css"
  },
  "/_nuxt/forgot-password.6e4df84b.js": {
    "type": "application/javascript",
    "etag": "\"b6e-eeM4S/mvlmXzS6ZpbrSQsjlacS0\"",
    "mtime": "2026-06-28T10:12:50.982Z",
    "size": 2926,
    "path": "../public/_nuxt/forgot-password.6e4df84b.js"
  },
  "/_nuxt/friends.3a6a4beb.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"1ce-9mRLKnweb+s3Id3eXa155+Ajm9A\"",
    "mtime": "2026-06-28T10:12:50.967Z",
    "size": 462,
    "path": "../public/_nuxt/friends.3a6a4beb.css"
  },
  "/_nuxt/friends.ff231671.js": {
    "type": "application/javascript",
    "etag": "\"902-o7BHeqbwTP5vUem7Fci0H2hvxhQ\"",
    "mtime": "2026-06-28T10:12:50.982Z",
    "size": 2306,
    "path": "../public/_nuxt/friends.ff231671.js"
  },
  "/_nuxt/guest-only.1927a0e3.js": {
    "type": "application/javascript",
    "etag": "\"15d-KUpdU35Q09ynNgGdDX0SP1rybs0\"",
    "mtime": "2026-06-28T10:12:50.975Z",
    "size": 349,
    "path": "../public/_nuxt/guest-only.1927a0e3.js"
  },
  "/_nuxt/guestbook.acc5ca6e.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"32-k21ZOu/XRxCLiL6nq/5TEWvP8I0\"",
    "mtime": "2026-06-28T10:12:50.967Z",
    "size": 50,
    "path": "../public/_nuxt/guestbook.acc5ca6e.css"
  },
  "/_nuxt/guestbook.b45466f7.js": {
    "type": "application/javascript",
    "etag": "\"3aa-dhkdrGt7uYfIhoEe/HC+AFfdT68\"",
    "mtime": "2026-06-28T10:12:50.981Z",
    "size": 938,
    "path": "../public/_nuxt/guestbook.b45466f7.js"
  },
  "/_nuxt/index.035ccddc.js": {
    "type": "application/javascript",
    "etag": "\"583-Q1vmRq812IHNWRKhWt0oc0RfkPU\"",
    "mtime": "2026-06-28T10:12:50.984Z",
    "size": 1411,
    "path": "../public/_nuxt/index.035ccddc.js"
  },
  "/_nuxt/index.37d299bd.js": {
    "type": "application/javascript",
    "etag": "\"b89-cIJe86mHOblNafpGVKQnf5UJN64\"",
    "mtime": "2026-06-28T10:12:50.977Z",
    "size": 2953,
    "path": "../public/_nuxt/index.37d299bd.js"
  },
  "/_nuxt/index.39f1622d.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"125-sQRJvc1UjAGylyK1f9qK3k8Wv/Q\"",
    "mtime": "2026-06-28T10:12:50.968Z",
    "size": 293,
    "path": "../public/_nuxt/index.39f1622d.css"
  },
  "/_nuxt/index.3c80867e.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"1ed-LaTbC7bO/t1Kn8Dohs+tUDQFpUk\"",
    "mtime": "2026-06-28T10:12:50.967Z",
    "size": 493,
    "path": "../public/_nuxt/index.3c80867e.css"
  },
  "/_nuxt/index.4e818ae4.js": {
    "type": "application/javascript",
    "etag": "\"551-pZNPm/yc+DDPrpvN4bKw0V3a36U\"",
    "mtime": "2026-06-28T10:12:50.984Z",
    "size": 1361,
    "path": "../public/_nuxt/index.4e818ae4.js"
  },
  "/_nuxt/index.504eee7b.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"45a-aKsE6hD3m75J02pUFQNWEt3sMxw\"",
    "mtime": "2026-06-28T10:12:50.967Z",
    "size": 1114,
    "path": "../public/_nuxt/index.504eee7b.css"
  },
  "/_nuxt/index.6a511f69.js": {
    "type": "application/javascript",
    "etag": "\"57e-7rrA9cFRhqv0Yv1bZY7bByyQrnk\"",
    "mtime": "2026-06-28T10:12:50.981Z",
    "size": 1406,
    "path": "../public/_nuxt/index.6a511f69.js"
  },
  "/_nuxt/index.7d9b541f.js": {
    "type": "application/javascript",
    "etag": "\"121-I0rGq2B/poZYH8QS/R+BDw2+TqI\"",
    "mtime": "2026-06-28T10:12:50.975Z",
    "size": 289,
    "path": "../public/_nuxt/index.7d9b541f.js"
  },
  "/_nuxt/index.cc73d0ed.js": {
    "type": "application/javascript",
    "etag": "\"49c-t1BgQpGNzP4yXJFbrpUUG/AFwaY\"",
    "mtime": "2026-06-28T10:12:50.986Z",
    "size": 1180,
    "path": "../public/_nuxt/index.cc73d0ed.js"
  },
  "/_nuxt/index.dc891e08.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"108-01qqhDFlVMwaVD7oALjoN2L+bcw\"",
    "mtime": "2026-06-28T10:12:50.975Z",
    "size": 264,
    "path": "../public/_nuxt/index.dc891e08.css"
  },
  "/_nuxt/index.e061644d.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"57-4T/pOJpmvlen04Iix3lJBO93eyw\"",
    "mtime": "2026-06-28T10:12:50.967Z",
    "size": 87,
    "path": "../public/_nuxt/index.e061644d.css"
  },
  "/_nuxt/index.e2dc6570.js": {
    "type": "application/javascript",
    "etag": "\"41e-CAK96M6xDwvtifVRXbuhSMPU8xc\"",
    "mtime": "2026-06-28T10:12:50.981Z",
    "size": 1054,
    "path": "../public/_nuxt/index.e2dc6570.js"
  },
  "/_nuxt/index.eac6d17c.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"1ac-qc5ctpJ7TBceVTPLvjTicfOHy3M\"",
    "mtime": "2026-06-28T10:12:50.967Z",
    "size": 428,
    "path": "../public/_nuxt/index.eac6d17c.css"
  },
  "/_nuxt/login.b4dcff16.js": {
    "type": "application/javascript",
    "etag": "\"924-37MJJFhEcGR/qebROuNlP3GNmIg\"",
    "mtime": "2026-06-28T10:12:50.982Z",
    "size": 2340,
    "path": "../public/_nuxt/login.b4dcff16.js"
  },
  "/_nuxt/login.c449a9aa.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"495-wgePUua/w3YbpzGP8jbz+HEKWKs\"",
    "mtime": "2026-06-28T10:12:50.967Z",
    "size": 1173,
    "path": "../public/_nuxt/login.c449a9aa.css"
  },
  "/_nuxt/privacy.aa812f36.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"2a-GV/gVt4VJtLwqsOo/gbAdWHlnSM\"",
    "mtime": "2026-06-28T10:12:50.967Z",
    "size": 42,
    "path": "../public/_nuxt/privacy.aa812f36.css"
  },
  "/_nuxt/privacy.f5a19132.js": {
    "type": "application/javascript",
    "etag": "\"4e1-eIDvn96D+iRxT+VrQzW2qO2zUvg\"",
    "mtime": "2026-06-28T10:12:50.981Z",
    "size": 1249,
    "path": "../public/_nuxt/privacy.f5a19132.js"
  },
  "/_nuxt/project.api.cd8470ee.js": {
    "type": "application/javascript",
    "etag": "\"92-bAyV7zA3I3aD7bPebXTQvzxxXo8\"",
    "mtime": "2026-06-28T10:12:50.975Z",
    "size": 146,
    "path": "../public/_nuxt/project.api.cd8470ee.js"
  },
  "/_nuxt/register.73c742f7.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"52f-aO8xgnY6VfUFlsSz2a4Uj3CD6Pg\"",
    "mtime": "2026-06-28T10:12:50.967Z",
    "size": 1327,
    "path": "../public/_nuxt/register.73c742f7.css"
  },
  "/_nuxt/register.c0e72bcf.js": {
    "type": "application/javascript",
    "etag": "\"d2b-h0KcGs9QtqOCMXi8elSmNLaGjSE\"",
    "mtime": "2026-06-28T10:12:50.982Z",
    "size": 3371,
    "path": "../public/_nuxt/register.c0e72bcf.js"
  },
  "/_nuxt/SafeHtml.vue.177f6f74.js": {
    "type": "application/javascript",
    "etag": "\"7284-V7e8f5iE6O/F0zlLpyhMwiSjjvY\"",
    "mtime": "2026-06-28T10:12:50.987Z",
    "size": 29316,
    "path": "../public/_nuxt/SafeHtml.vue.177f6f74.js"
  },
  "/_nuxt/safeRedirect.112849c5.js": {
    "type": "application/javascript",
    "etag": "\"12c-PArrUK2CbAJ1RY80fkzG/SwFZng\"",
    "mtime": "2026-06-28T10:12:50.975Z",
    "size": 300,
    "path": "../public/_nuxt/safeRedirect.112849c5.js"
  },
  "/_nuxt/safeUrl.07f6d95d.js": {
    "type": "application/javascript",
    "etag": "\"1dc-mYWB9o9mgeiYh+VMvZ2KsHnLvm0\"",
    "mtime": "2026-06-28T10:12:50.975Z",
    "size": 476,
    "path": "../public/_nuxt/safeUrl.07f6d95d.js"
  },
  "/_nuxt/search.df4784e9.js": {
    "type": "application/javascript",
    "etag": "\"b04-yxTGY7GdpBuym02OxO4EjBpW33M\"",
    "mtime": "2026-06-28T10:12:50.986Z",
    "size": 2820,
    "path": "../public/_nuxt/search.df4784e9.js"
  },
  "/_nuxt/search.ed3023ea.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"47e-apHox6VXLJQ8O42hbg5mB9eXRxQ\"",
    "mtime": "2026-06-28T10:12:50.967Z",
    "size": 1150,
    "path": "../public/_nuxt/search.ed3023ea.css"
  },
  "/_nuxt/snippet.api.52983555.js": {
    "type": "application/javascript",
    "etag": "\"16f-DHEhdGrzM9hp3I9Fru38HSo7xPs\"",
    "mtime": "2026-06-28T10:12:50.975Z",
    "size": 367,
    "path": "../public/_nuxt/snippet.api.52983555.js"
  },
  "/_nuxt/subscribe.api.c3c0bf8b.js": {
    "type": "application/javascript",
    "etag": "\"197-0UPg32c+p1VJcXEreeRiWI6TPrk\"",
    "mtime": "2026-06-28T10:12:50.975Z",
    "size": 407,
    "path": "../public/_nuxt/subscribe.api.c3c0bf8b.js"
  },
  "/_nuxt/taxonomy.api.70f712c0.js": {
    "type": "application/javascript",
    "etag": "\"15b-Haa4/EjH1sNQVEYhRT7uvEGx1mU\"",
    "mtime": "2026-06-28T10:12:50.975Z",
    "size": 347,
    "path": "../public/_nuxt/taxonomy.api.70f712c0.js"
  },
  "/_nuxt/unsubscribe.5dcad876.js": {
    "type": "application/javascript",
    "etag": "\"388-1CYADquWSLRnkndPQWACmFu6nE8\"",
    "mtime": "2026-06-28T10:12:50.982Z",
    "size": 904,
    "path": "../public/_nuxt/unsubscribe.5dcad876.js"
  },
  "/_nuxt/unsubscribe.6157feb0.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"35-Jd3tbrtsW6FRF78Sl/eqIiNXoWk\"",
    "mtime": "2026-06-28T10:12:50.975Z",
    "size": 53,
    "path": "../public/_nuxt/unsubscribe.6157feb0.css"
  },
  "/_nuxt/usePageSeo.e97f2e3a.js": {
    "type": "application/javascript",
    "etag": "\"214-QRbhnG7g0rSQyU4farckryVqJ0k\"",
    "mtime": "2026-06-28T10:12:50.975Z",
    "size": 532,
    "path": "../public/_nuxt/usePageSeo.e97f2e3a.js"
  },
  "/_nuxt/userErrorMessage.4f24c14b.js": {
    "type": "application/javascript",
    "etag": "\"59-vXdJArXhVSZEVSa147pAuNJu3oE\"",
    "mtime": "2026-06-28T10:12:50.975Z",
    "size": 89,
    "path": "../public/_nuxt/userErrorMessage.4f24c14b.js"
  },
  "/_nuxt/vue.-sixQ7xP.26eb4878.js": {
    "type": "application/javascript",
    "etag": "\"18b-H1IC22zpuT70fZjryYLiCPd9ABE\"",
    "mtime": "2026-06-28T10:12:50.976Z",
    "size": 395,
    "path": "../public/_nuxt/vue.-sixQ7xP.26eb4878.js"
  },
  "/_nuxt/_repo_.35d3cfec.js": {
    "type": "application/javascript",
    "etag": "\"5d2-Fiz+HFOy5iwLJiYLUmG3SgHdr+Q\"",
    "mtime": "2026-06-28T10:12:50.982Z",
    "size": 1490,
    "path": "../public/_nuxt/_repo_.35d3cfec.js"
  },
  "/_nuxt/_repo_.76a07f95.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"1fc-2H9ShyPXAvEAgl5N+9833irMG3I\"",
    "mtime": "2026-06-28T10:12:50.967Z",
    "size": 508,
    "path": "../public/_nuxt/_repo_.76a07f95.css"
  },
  "/_nuxt/_slug_.005523b8.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"514-1WmD3Dmel3Lcx4XzjhZOg2iWr98\"",
    "mtime": "2026-06-28T10:12:50.967Z",
    "size": 1300,
    "path": "../public/_nuxt/_slug_.005523b8.css"
  },
  "/_nuxt/_slug_.2b6eab59.js": {
    "type": "application/javascript",
    "etag": "\"44c-TYKxhQ/L5VAyX1jWb0W8tF2bT/I\"",
    "mtime": "2026-06-28T10:12:50.977Z",
    "size": 1100,
    "path": "../public/_nuxt/_slug_.2b6eab59.js"
  },
  "/_nuxt/_slug_.33ab50ad.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"1e3-d6fZSli1nPEGEHfy9dO65T9mMfE\"",
    "mtime": "2026-06-28T10:12:50.967Z",
    "size": 483,
    "path": "../public/_nuxt/_slug_.33ab50ad.css"
  },
  "/_nuxt/_slug_.58b412dc.js": {
    "type": "application/javascript",
    "etag": "\"528-K+pKJVAzC8dXDP0Iy1yXnj2xB1w\"",
    "mtime": "2026-06-28T10:12:50.981Z",
    "size": 1320,
    "path": "../public/_nuxt/_slug_.58b412dc.js"
  },
  "/_nuxt/_slug_.990f7ec2.js": {
    "type": "application/javascript",
    "etag": "\"7b7-csjahqkWL+f40rPJOeIe3gRxgdw\"",
    "mtime": "2026-06-28T10:12:50.982Z",
    "size": 1975,
    "path": "../public/_nuxt/_slug_.990f7ec2.js"
  },
  "/_nuxt/_slug_.9f26bb8f.css": {
    "type": "text/css; charset=utf-8",
    "etag": "\"31-Y2nb1GFH15G4/pNiEnY1z3sWviE\"",
    "mtime": "2026-06-28T10:12:50.959Z",
    "size": 49,
    "path": "../public/_nuxt/_slug_.9f26bb8f.css"
  },
  "/_nuxt/_slug_.e7474df3.js": {
    "type": "application/javascript",
    "etag": "\"c14-12NSShrs/S4vWXXnawyLEKo0sM4\"",
    "mtime": "2026-06-28T10:12:50.986Z",
    "size": 3092,
    "path": "../public/_nuxt/_slug_.e7474df3.js"
  }
};

function readAsset (id) {
  const serverDir = dirname(fileURLToPath(globalThis._importMeta_.url));
  return promises.readFile(resolve(serverDir, assets[id].path))
}

const publicAssetBases = {"/_nuxt":{"maxAge":31536000}};

function isPublicAssetURL(id = '') {
  if (assets[id]) {
    return true
  }
  for (const base in publicAssetBases) {
    if (id.startsWith(base)) { return true }
  }
  return false
}

function getAsset (id) {
  return assets[id]
}

const METHODS = /* @__PURE__ */ new Set(["HEAD", "GET"]);
const EncodingMap = { gzip: ".gz", br: ".br" };
const _f4b49z = eventHandler((event) => {
  if (event.node.req.method && !METHODS.has(event.node.req.method)) {
    return;
  }
  let id = decodeURIComponent(
    withLeadingSlash(
      withoutTrailingSlash(parseURL(event.node.req.url).pathname)
    )
  );
  let asset;
  const encodingHeader = String(
    event.node.req.headers["accept-encoding"] || ""
  );
  const encodings = [
    ...encodingHeader.split(",").map((e) => EncodingMap[e.trim()]).filter(Boolean).sort(),
    ""
  ];
  if (encodings.length > 1) {
    event.node.res.setHeader("Vary", "Accept-Encoding");
  }
  for (const encoding of encodings) {
    for (const _id of [id + encoding, joinURL(id, "index.html" + encoding)]) {
      const _asset = getAsset(_id);
      if (_asset) {
        asset = _asset;
        id = _id;
        break;
      }
    }
  }
  if (!asset) {
    if (isPublicAssetURL(id)) {
      event.node.res.removeHeader("cache-control");
      throw createError({
        statusMessage: "Cannot find static asset " + id,
        statusCode: 404
      });
    }
    return;
  }
  const ifNotMatch = event.node.req.headers["if-none-match"] === asset.etag;
  if (ifNotMatch) {
    event.node.res.statusCode = 304;
    event.node.res.end();
    return;
  }
  const ifModifiedSinceH = event.node.req.headers["if-modified-since"];
  const mtimeDate = new Date(asset.mtime);
  if (ifModifiedSinceH && asset.mtime && new Date(ifModifiedSinceH) >= mtimeDate) {
    event.node.res.statusCode = 304;
    event.node.res.end();
    return;
  }
  if (asset.type && !event.node.res.getHeader("Content-Type")) {
    event.node.res.setHeader("Content-Type", asset.type);
  }
  if (asset.etag && !event.node.res.getHeader("ETag")) {
    event.node.res.setHeader("ETag", asset.etag);
  }
  if (asset.mtime && !event.node.res.getHeader("Last-Modified")) {
    event.node.res.setHeader("Last-Modified", mtimeDate.toUTCString());
  }
  if (asset.encoding && !event.node.res.getHeader("Content-Encoding")) {
    event.node.res.setHeader("Content-Encoding", asset.encoding);
  }
  if (asset.size > 0 && !event.node.res.getHeader("Content-Length")) {
    event.node.res.setHeader("Content-Length", asset.size);
  }
  return readAsset(id);
});

const _lazy_imjBGH = () => import('../robots.txt.mjs');
const _lazy_gWT0jD = () => import('../sitemap.xml.mjs');
const _lazy_qNdp0q = () => import('../handlers/renderer.mjs');

const handlers = [
  { route: '', handler: _f4b49z, lazy: false, middleware: true, method: undefined },
  { route: '/robots.txt', handler: _lazy_imjBGH, lazy: true, middleware: false, method: undefined },
  { route: '/sitemap.xml', handler: _lazy_gWT0jD, lazy: true, middleware: false, method: undefined },
  { route: '/__nuxt_error', handler: _lazy_qNdp0q, lazy: true, middleware: false, method: undefined },
  { route: '/', handler: _lazy_qNdp0q, lazy: true, middleware: false, method: undefined },
  { route: '/posts/**', handler: _lazy_qNdp0q, lazy: true, middleware: false, method: undefined },
  { route: '/categories/**', handler: _lazy_qNdp0q, lazy: true, middleware: false, method: undefined },
  { route: '/tags/**', handler: _lazy_qNdp0q, lazy: true, middleware: false, method: undefined },
  { route: '/archives', handler: _lazy_qNdp0q, lazy: true, middleware: false, method: undefined },
  { route: '/snippets/**', handler: _lazy_qNdp0q, lazy: true, middleware: false, method: undefined },
  { route: '/notes/**', handler: _lazy_qNdp0q, lazy: true, middleware: false, method: undefined },
  { route: '/**', handler: _lazy_qNdp0q, lazy: true, middleware: false, method: undefined }
];

function createNitroApp() {
  const config = useRuntimeConfig();
  const hooks = createHooks();
  const h3App = createApp({
    debug: destr(false),
    onError: errorHandler
  });
  const router = createRouter$1();
  h3App.use(createRouteRulesHandler());
  const localCall = createCall(toNodeListener(h3App));
  const localFetch = createFetch(localCall, globalThis.fetch);
  const $fetch = createFetch$1({
    fetch: localFetch,
    Headers,
    defaults: { baseURL: config.app.baseURL }
  });
  globalThis.$fetch = $fetch;
  h3App.use(
    eventHandler((event) => {
      event.context.nitro = event.context.nitro || {};
      const envContext = event.node.req.__unenv__;
      if (envContext) {
        Object.assign(event.context, envContext);
      }
      event.fetch = (req, init) => fetchWithEvent(event, req, init, { fetch: localFetch });
      event.$fetch = (req, init) => fetchWithEvent(event, req, init, { fetch: $fetch });
    })
  );
  for (const h of handlers) {
    let handler = h.lazy ? lazyEventHandler(h.handler) : h.handler;
    if (h.middleware || !h.route) {
      const middlewareBase = (config.app.baseURL + (h.route || "/")).replace(
        /\/+/g,
        "/"
      );
      h3App.use(middlewareBase, handler);
    } else {
      const routeRules = getRouteRulesForPath(
        h.route.replace(/:\w+|\*\*/g, "_")
      );
      if (routeRules.cache) {
        handler = cachedEventHandler(handler, {
          group: "nitro/routes",
          ...routeRules.cache
        });
      }
      router.use(h.route, handler, h.method);
    }
  }
  h3App.use(config.app.baseURL, router);
  const app = {
    hooks,
    h3App,
    router,
    localCall,
    localFetch
  };
  for (const plugin of plugins) {
    plugin(app);
  }
  return app;
}
const nitroApp = createNitroApp();
const useNitroApp = () => nitroApp;

const cert = process.env.NITRO_SSL_CERT;
const key = process.env.NITRO_SSL_KEY;
const server = cert && key ? new Server({ key, cert }, toNodeListener(nitroApp.h3App)) : new Server$1(toNodeListener(nitroApp.h3App));
const port = destr(process.env.NITRO_PORT || process.env.PORT) || 3e3;
const host = process.env.NITRO_HOST || process.env.HOST;
const s = server.listen(port, host, (err) => {
  if (err) {
    console.error(err);
    process.exit(1);
  }
  const protocol = cert && key ? "https" : "http";
  const i = s.address();
  const baseURL = (useRuntimeConfig().app.baseURL || "").replace(/\/$/, "");
  const url = `${protocol}://${i.family === "IPv6" ? `[${i.address}]` : i.address}:${i.port}${baseURL}`;
  console.log(`Listening ${url}`);
});
{
  process.on(
    "unhandledRejection",
    (err) => console.error("[nitro] [dev] [unhandledRejection] " + err)
  );
  process.on(
    "uncaughtException",
    (err) => console.error("[nitro] [dev] [uncaughtException] " + err)
  );
}
const nodeServer = {};

export { useNitroApp as a, getRouteRules as g, nodeServer as n, useRuntimeConfig as u };
//# sourceMappingURL=node-server.mjs.map
