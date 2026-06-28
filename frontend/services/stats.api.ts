import { apiFetch } from '~/utils/http'

export interface SiteStats {
  articleCount: number
  snippetCount: number
  noteCount: number
  runningDays: number
}

export function fetchStats() {
  return apiFetch<SiteStats>('/stats')
}
