export const CSRF_COOKIE = 'XSRF-TOKEN'

export function parseCookieHeader(cookieHeader: string | undefined, name: string): string | undefined {
  if (!cookieHeader) {
    return undefined
  }
  const parts = cookieHeader.split(';')
  for (const part of parts) {
    const trimmed = part.trim()
    const eq = trimmed.indexOf('=')
    if (eq <= 0) {
      continue
    }
    if (trimmed.slice(0, eq) === name) {
      return decodeURIComponent(trimmed.slice(eq + 1))
    }
  }
  return undefined
}

export function isMutatingMethod(method?: string): boolean {
  const m = (method || 'GET').toUpperCase()
  return m === 'POST' || m === 'PUT' || m === 'PATCH' || m === 'DELETE'
}

export function csrfHeadersFromToken(token: string | undefined, method?: string): Record<string, string> {
  if (!isMutatingMethod(method) || !token) {
    return {}
  }
  return { 'X-XSRF-TOKEN': token }
}
