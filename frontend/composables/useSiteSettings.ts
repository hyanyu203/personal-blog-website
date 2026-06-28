import { fetchPublicSettings } from '~/services/settings.api'

export function useSiteSettings() {
  return useAsyncData('site-settings', () => fetchPublicSettings())
}
