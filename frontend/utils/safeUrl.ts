/** Return href only for http/https URLs; blocks javascript: and protocol-relative links. */
export function safeExternalHref(url: string | undefined | null): string | undefined {
  if (!url) return undefined
  try {
    const parsed = new URL(url)
    if (parsed.protocol === 'http:' || parsed.protocol === 'https:') {
      return parsed.href
    }
  } catch {
    return undefined
  }
  return undefined
}

const INTERNAL_PATH_PREFIXES = [
  '/posts/',
  '/snippets/',
  '/notes/',
  '/projects/',
  '/categories/',
  '/tags/',
  '/archives',
  '/search',
  '/about',
  '/friends',
  '/guestbook'
]

/** Allow only same-site relative paths for in-app navigation (e.g. search results). */
export function safeInternalPath(url: string | undefined | null): string | undefined {
  if (!url || !url.startsWith('/') || url.startsWith('//')) {
    return undefined
  }
  if (url.includes('://') || url.includes('\\')) {
    return undefined
  }
  const path = url.split('?')[0].split('#')[0]
  if (path === '/' || INTERNAL_PATH_PREFIXES.some(prefix => path === prefix || path.startsWith(prefix + '/'))) {
    return url
  }
  return undefined
}

