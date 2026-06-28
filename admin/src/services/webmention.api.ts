import http from './http'
import type { PageResult } from '@jiangou/shared'

export interface WebmentionItem {
  id: number
  sourceUrl: string
  targetUrl: string
  type: string
  status: string
  verifiedAt?: string
  createdAt: string
}

export function fetchWebmentions(status?: string, page = 1) {
  let url = `/admin/webmentions?page=${page}&pageSize=20`
  if (status) url += `&status=${encodeURIComponent(status)}`
  return http.get<PageResult<WebmentionItem>>(url)
}
