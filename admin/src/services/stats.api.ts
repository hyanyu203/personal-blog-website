import http from './http'

export interface AdminStats {
  articleCount: number
  draftCount: number
  pendingComments: number
  subscriberCount: number
  snippetCount: number
  noteCount: number
  runningDays: number
}

export function fetchAdminStats() {
  return http.get< AdminStats>('/admin/stats')
}
