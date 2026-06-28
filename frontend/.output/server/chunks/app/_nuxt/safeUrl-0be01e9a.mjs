function safeExternalHref(url) {
  if (!url)
    return void 0;
  try {
    const parsed = new URL(url);
    if (parsed.protocol === "http:" || parsed.protocol === "https:") {
      return parsed.href;
    }
  } catch {
    return void 0;
  }
  return void 0;
}
const INTERNAL_PATH_PREFIXES = [
  "/posts/",
  "/snippets/",
  "/notes/",
  "/projects/",
  "/categories/",
  "/tags/",
  "/archives",
  "/search",
  "/about",
  "/friends",
  "/guestbook"
];
function safeInternalPath(url) {
  if (!url || !url.startsWith("/") || url.startsWith("//")) {
    return void 0;
  }
  if (url.includes("://") || url.includes("\\")) {
    return void 0;
  }
  const path = url.split("?")[0].split("#")[0];
  if (path === "/" || INTERNAL_PATH_PREFIXES.some((prefix) => path === prefix || path.startsWith(prefix + "/"))) {
    return url;
  }
  return void 0;
}

export { safeInternalPath as a, safeExternalHref as s };
//# sourceMappingURL=safeUrl-0be01e9a.mjs.map
