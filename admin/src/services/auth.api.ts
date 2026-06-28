import http from './http'
import type { AuthUser } from '@jiangou/shared'

export type { AuthUser }

export function fetchCaptcha() {
  return http.get<{ captchaId: string; imageBase64: string }>('/auth/captcha')
}

export function login(username: string, password: string, captchaId: string, captchaCode: string) {
  return http.post<{ user: AuthUser }>('/auth/login', { username, password, captchaId, captchaCode })
}

export function fetchMe() {
  return http.get<AuthUser>('/auth/me')
}

export function fetchGitHubOAuthEnabled() {
  return http.get<{ enabled: boolean }>('/auth/github/enabled')
}

export function githubLoginUrl() {
  return '/api/v1/auth/github'
}

export function logoutApi() {
  return http.post('/auth/logout', {})
}

export function exchangeOAuthCode(code: string) {
  return http.post<{ user: AuthUser }>('/auth/oauth/exchange', { code })
}
