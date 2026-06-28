import { apiFetch } from '~/utils/http'

export interface PublicSettings {
  siteTitle?: string
  siteSubtitle?: string
  siteDescription?: string
  siteLaunchDate?: string
  guestbookTargetId?: string | number
}

export function fetchPublicSettings() {
  return apiFetch<PublicSettings>('/settings/public')
}
