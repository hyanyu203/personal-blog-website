import { apiFetch } from '~/utils/http'

export default defineNuxtPlugin(async () => {
  try {
    await apiFetch<Record<string, unknown>>('/settings/public')
  } catch {
    // ignore — CSRF cookie is best-effort before mutations
  }
})
