import { CSRF_COOKIE, csrfHeadersFromToken, parseCookieHeader } from '@jiangou/shared'

export function readCsrfToken(): string | undefined {
  if (typeof document === 'undefined') {
    return undefined
  }
  return parseCookieHeader(document.cookie, CSRF_COOKIE)
}

export function csrfHeaders(method?: string): Record<string, string> {
  return csrfHeadersFromToken(readCsrfToken(), method)
}
