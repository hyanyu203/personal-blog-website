import { csrfHeaders } from '~/utils/csrf'

export interface ApiResult<T> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  items: T[]
  total: number
  page: number
  pageSize: number
  hasMore: boolean
}

export class ApiError extends Error {
  code: number

  constructor(code: number, message: string) {
    super(message)
    this.code = code
  }
}

function resolveApiBase(): string {
  const config = useRuntimeConfig()
  if (typeof window === 'undefined' && config.apiBaseInternal) {
    return config.apiBaseInternal as string
  }
  return config.public.apiBase as string
}

function buildRequestHeaders(options: RequestInit): Record<string, string> {
  const headers: Record<string, string> = {
    ...(options.headers as Record<string, string> | undefined),
    ...csrfHeaders(options.method)
  }
  const hasBody = options.body != null && options.body !== ''
  if (hasBody && !headers['Content-Type'] && !headers['content-type']) {
    headers['Content-Type'] = 'application/json'
  }
  if (typeof window === 'undefined') {
    const incoming = useRequestHeaders(['cookie'])
    if (incoming.cookie) {
      headers.cookie = incoming.cookie
    }
  }
  return headers
}

let refreshPromise: Promise<boolean> | null = null

export async function tryRefreshSession(): Promise<boolean> {
  if (!refreshPromise) {
    refreshPromise = (async () => {
      try {
        const base = resolveApiBase()
        const headers = buildRequestHeaders({ method: 'POST', body: '{}' })
        const res = await fetch(`${base}/auth/refresh`, {
          method: 'POST',
          credentials: 'include',
          headers,
          body: '{}'
        })
        const json = (await res.json()) as ApiResult<unknown>
        return json.code === 0
      } catch {
        return false
      } finally {
        refreshPromise = null
      }
    })()
  }
  return refreshPromise
}

export async function warmupCsrfCookie(referrerPolicy?: ReferrerPolicy): Promise<void> {
  if (typeof window === 'undefined') {
    return
  }
  const base = resolveApiBase()
  await fetch(`${base}/settings/public`, {
    method: 'GET',
    credentials: 'include',
    cache: 'no-store',
    referrerPolicy
  })
}

export async function apiFetch<T>(
  path: string,
  options: RequestInit = {},
  retried = false
): Promise<T> {
  if (path.startsWith('http://') || path.startsWith('https://')) {
    throw new Error('Absolute API URLs are not allowed')
  }
  const base = resolveApiBase()
  const url = `${base}${path}`

  const res = await fetch(url, {
    ...options,
    credentials: 'include',
    headers: buildRequestHeaders(options)
  })

  const contentType = res.headers.get('content-type') || ''
  if (!contentType.includes('application/json')) {
    throw new Error(res.ok ? '响应格式错误' : `请求失败 (${res.status})`)
  }

  const json = (await res.json()) as ApiResult<T>
  if ((!res.ok || json.code !== 0) && json.code === 40101 && !retried) {
    const refreshed = await tryRefreshSession()
    if (refreshed) {
      return apiFetch(path, options, true)
    }
  }
  if (!res.ok || json.code !== 0) {
    throw new ApiError(json.code ?? res.status, json.message || `请求失败 (${res.status})`)
  }
  return json.data
}
