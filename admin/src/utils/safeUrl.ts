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
