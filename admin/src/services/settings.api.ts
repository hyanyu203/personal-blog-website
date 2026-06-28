import http from './http'
import type { PageResult } from '@jiangou/shared'

export interface SettingItem {
  key: string
  value: string
  description?: string
  isPublic: boolean
}

export function fetchSettings() {
  return http.get<SettingItem[]>('/admin/settings')
}

export function updateSetting(key: string, value: string) {
  return http.patch(`/admin/settings/${key}`, { value })
}

export function rebuildSearch() {
  return http.post<{ indexed: number }>('/admin/search/rebuild')
}

export function rebuildRss() {
  return http.post('/admin/rss/rebuild')
}

export interface AuditLogItem {
  id: number
  actorId: number
  action: string
  targetType: string
  targetId: number
  createdAt: string
}

export function fetchAuditLogs(page = 1) {
  return http.get<PageResult<AuditLogItem>>(`/admin/audit-logs?page=${page}&pageSize=20`)
}
