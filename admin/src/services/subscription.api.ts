import http from './http'
import type { PageResult } from '@jiangou/shared'

export interface SubscriptionItem {
  id: number
  email: string
  status: string
  createdAt: string
}

export function fetchSubscriptions(status?: string, page = 1) {
  let url = `/admin/subscriptions?page=${page}&pageSize=20`
  if (status) url += `&status=${encodeURIComponent(status)}`
  return http.get<PageResult<SubscriptionItem>>(url)
}

export function sendNewsletter(subject: string, body: string) {
  return http.post<{ sent: number }>('/admin/subscriptions/newsletter', { subject, body })
}
