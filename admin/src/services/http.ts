import axios, { type AxiosError, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios'
import {
  ApiError,
  FORBIDDEN_CODE,
  UNAUTHORIZED_CODE,
  type ApiEnvelope
} from '@jiangou/shared'
import { csrfHeaders } from '@/utils/csrf'

const http = axios.create({
  baseURL: '/api/v1',
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' }
})

let refreshPromise: Promise<boolean> | null = null

type RetryConfig = InternalAxiosRequestConfig & { _retry?: boolean }

export function redirectToLogin() {
  const base = import.meta.env.BASE_URL || '/admin/'
  let relative = window.location.pathname
  const adminPrefix = '/admin'
  if (relative.startsWith(adminPrefix)) {
    relative = relative.slice(adminPrefix.length) || '/'
  }
  window.location.href = `${base}login?redirect=${encodeURIComponent(relative)}`
}

export async function refreshAccessToken(): Promise<boolean> {
  if (!refreshPromise) {
    refreshPromise = (async () => {
      try {
        const res = await axios.post<ApiEnvelope>('/api/v1/auth/refresh', {}, {
          withCredentials: true,
          headers: csrfHeaders('POST')
        })
        const body = res.data as ApiEnvelope | undefined
        return body?.code === 0
      } catch {
        return false
      } finally {
        refreshPromise = null
      }
    })()
  }
  return refreshPromise
}

export async function warmupCsrfCookie(): Promise<void> {
  await axios.get('/api/v1/settings/public', {
    withCredentials: true,
    headers: {
      'Cache-Control': 'no-store'
    }
  })
}

function isUnauthorized(body: ApiEnvelope | undefined, status?: number) {
  return body?.code === UNAUTHORIZED_CODE || status === 401
}

http.interceptors.request.use((config) => {
  const method = config.method?.toUpperCase()
  Object.assign(config.headers, csrfHeaders(method))
  return config
})

async function retryAfterRefresh(original: RetryConfig) {
  original._retry = true
  const ok = await refreshAccessToken()
  if (ok) {
    return http(original)
  }
  redirectToLogin()
  return Promise.reject(new ApiError(UNAUTHORIZED_CODE, '未登录或会话已过期'))
}

function rejectApiError(body: ApiEnvelope | undefined, fallback: string) {
  const code = body?.code ?? 50000
  const message = body?.message || fallback
  return Promise.reject(new ApiError(code, message))
}

http.interceptors.response.use(
  async (res: AxiosResponse): Promise<any> => {
    const body = res.data as ApiEnvelope
    const original = res.config as RetryConfig
    if (body.code !== 0 && isUnauthorized(body, res.status) && !original._retry) {
      return retryAfterRefresh(original)
    }
    if (body.code !== 0) {
      return rejectApiError(body, '请求失败')
    }
    return body.data
  },
  async (err: AxiosError<ApiEnvelope>) => {
    const original = err.config as RetryConfig | undefined
    const body = err.response?.data
    if (original && !original._retry && isUnauthorized(body, err.response?.status)) {
      return retryAfterRefresh(original)
    }
    if (body?.code === FORBIDDEN_CODE || err.response?.status === 403) {
      return Promise.reject(new ApiError(FORBIDDEN_CODE, body?.message || '无权限执行此操作'))
    }
    return rejectApiError(body, err.message || '请求失败')
  }
)

export default http
