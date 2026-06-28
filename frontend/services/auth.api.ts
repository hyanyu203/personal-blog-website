import { apiFetch, tryRefreshSession } from '~/utils/http'
import type { AuthUser } from '~/types/auth'

export type { AuthUser }

export interface AuthResult {
  user: AuthUser
}

export interface CaptchaResult {
  captchaId: string
  imageBase64: string
}

export function fetchCaptcha() {
  return apiFetch<CaptchaResult>('/auth/captcha')
}

export function login(username: string, password: string, captchaId: string, captchaCode: string) {
  return apiFetch<AuthResult>('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password, captchaId, captchaCode })
  })
}

export function sendRegisterCode(email: string, captchaId: string, captchaCode: string) {
  return apiFetch<void>('/auth/register/send-code', {
    method: 'POST',
    body: JSON.stringify({ email, captchaId, captchaCode })
  })
}

export function register(data: {
  username: string
  email: string
  password: string
  emailCode: string
  captchaId: string
  captchaCode: string
}) {
  return apiFetch<AuthResult>('/auth/register', {
    method: 'POST',
    body: JSON.stringify(data)
  })
}

export function sendResetCode(email: string, captchaId: string, captchaCode: string) {
  return apiFetch<void>('/auth/forgot-password/send-code', {
    method: 'POST',
    body: JSON.stringify({ email, captchaId, captchaCode })
  })
}

export function resetPassword(email: string, emailCode: string, newPassword: string) {
  return apiFetch<void>('/auth/reset-password', {
    method: 'POST',
    body: JSON.stringify({ email, emailCode, newPassword })
  })
}

export function fetchMe() {
  return apiFetch<AuthUser>('/auth/me')
}

export function logoutApi() {
  return apiFetch<void>('/auth/logout', { method: 'POST', body: '{}' })
}

export async function refreshSession() {
  return tryRefreshSession()
}
