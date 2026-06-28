import fetch from 'node-fetch'

const backendUrl = process.env.BACKEND_URL || 'http://localhost:8080'

export interface ApiSession {
  cookieHeader: string
  csrfToken: string
}

function mergeSetCookie(existing: string, setCookieHeader: string | null): string {
  const jar = new Map<string, string>()
  for (const part of existing.split(';').map((s) => s.trim()).filter(Boolean)) {
    const eq = part.indexOf('=')
    if (eq > 0) jar.set(part.slice(0, eq), part.slice(eq + 1))
  }
  if (setCookieHeader) {
    for (const segment of setCookieHeader.split(/,(?=[^;]+=)/)) {
      const first = segment.split(';')[0]?.trim()
      if (!first) continue
      const eq = first.indexOf('=')
      if (eq > 0) jar.set(first.slice(0, eq), first.slice(eq + 1))
    }
  }
  return Array.from(jar.entries()).map(([k, v]) => `${k}=${v}`).join('; ')
}

function extractCookie(setCookieHeader: string | null, name: string): string | null {
  if (!setCookieHeader) {
    return null
  }
  const match = setCookieHeader.match(new RegExp(`${name}=([^;]+)`))
  return match ? match[1] : null
}

function extractCookieValue(cookieHeader: string, name: string): string | null {
  for (const part of cookieHeader.split(';').map((s) => s.trim()).filter(Boolean)) {
    const eq = part.indexOf('=')
    if (eq > 0 && part.slice(0, eq) === name) {
      return part.slice(eq + 1)
    }
  }
  return null
}

async function fetchCsrfSession(): Promise<{ cookieHeader: string; token: string } | null> {
  const res = await fetch(`${backendUrl}/api/v1/auth/captcha`)
  if (!res.ok) {
    return null
  }
  const setCookie = res.headers.get('set-cookie')
  const token = extractCookie(setCookie, 'XSRF-TOKEN')
  if (!token) {
    return null
  }
  return { cookieHeader: `XSRF-TOKEN=${token}`, token }
}

export async function isBackendReady(): Promise<boolean> {
  try {
    const res = await Promise.race([
      fetch(`${backendUrl}/actuator/health`),
      new Promise<never>((_, reject) => setTimeout(() => reject(new Error('timeout')), 3000))
    ])
    return res.ok
  } catch {
    return false
  }
}

export function requireBackendReady(): boolean {
  return process.env.E2E_REQUIRE_BACKEND === '1'
}

export async function assertBackendReady(): Promise<void> {
  if (!(await isBackendReady())) {
    if (requireBackendReady()) {
      throw new Error(`Backend not ready at ${backendUrl}`)
    }
  }
}

export async function loginViaApi(username: string, password: string): Promise<boolean> {
  const csrf = await fetchCsrfSession()
  if (!csrf) {
    return false
  }

  const captchaRes = await fetch(`${backendUrl}/api/v1/auth/captcha`, {
    headers: { Cookie: csrf.cookieHeader }
  })
  if (!captchaRes.ok) {
    return false
  }
  const captcha = (await captchaRes.json()) as { data?: { captchaId?: string } }
  const captchaId = captcha.data?.captchaId
  if (!captchaId) {
    return false
  }

  const loginRes = await fetch(`${backendUrl}/api/v1/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Cookie: csrf.cookieHeader,
      'X-XSRF-TOKEN': csrf.token
    },
    body: JSON.stringify({
      username,
      password,
      captchaId,
      captchaCode: 'E2E1'
    })
  })
  if (!loginRes.ok) {
    return false
  }
  const body = (await loginRes.json()) as { code?: number }
  return body.code === 0
}

export async function loginViaApiSession(username: string, password: string): Promise<ApiSession | null> {
  const csrf = await fetchCsrfSession()
  if (!csrf) {
    return null
  }

  const captchaRes = await fetch(`${backendUrl}/api/v1/auth/captcha`, {
    headers: { Cookie: csrf.cookieHeader }
  })
  if (!captchaRes.ok) {
    return null
  }
  const captcha = (await captchaRes.json()) as { data?: { captchaId?: string } }
  const captchaId = captcha.data?.captchaId
  if (!captchaId) {
    return null
  }

  let cookieHeader = csrf.cookieHeader
  const loginRes = await fetch(`${backendUrl}/api/v1/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Cookie: cookieHeader,
      'X-XSRF-TOKEN': csrf.token
    },
    body: JSON.stringify({
      username,
      password,
      captchaId,
      captchaCode: 'E2E1'
    })
  })
  if (!loginRes.ok) {
    return null
  }
  const body = (await loginRes.json()) as { code?: number }
  if (body.code !== 0) {
    return null
  }
  cookieHeader = mergeSetCookie(cookieHeader, loginRes.headers.get('set-cookie'))
  const refreshedToken = extractCookie(loginRes.headers.get('set-cookie'), 'XSRF-TOKEN')
    || extractCookieValue(cookieHeader, 'XSRF-TOKEN')
    || csrf.token
  return { cookieHeader, csrfToken: refreshedToken }
}

export async function adminApiRequest<T>(
  session: ApiSession,
  method: string,
  path: string,
  payload?: unknown
): Promise<{ code: number; data?: T }> {
  const headers: Record<string, string> = {
    Cookie: session.cookieHeader,
    'X-XSRF-TOKEN': session.csrfToken
  }
  if (payload !== undefined) {
    headers['Content-Type'] = 'application/json'
  }
  const res = await fetch(`${backendUrl}/api/v1${path}`, {
    method,
    headers,
    body: payload === undefined ? undefined : JSON.stringify(payload)
  })
  return (await res.json()) as { code: number; data?: T }
}
