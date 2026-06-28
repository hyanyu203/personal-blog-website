import http from './http'
import type { PageResult } from '@jiangou/shared'

export interface CommentItem {
  id: number
  nickname: string
  contentHtml: string
  status: string
  createdAt: string
}

export function fetchCommentsPage(status?: string, page = 1, pageSize = 20) {
  const params = new URLSearchParams({
    page: String(page),
    pageSize: String(pageSize)
  })
  if (status) params.set('status', status)
  return http.get<PageResult<CommentItem>>(`/admin/comments?${params}`)
}

export function approveComment(id: number) {
  return http.post(`/admin/comments/${id}/approve`)
}

export function rejectComment(id: number) {
  return http.post(`/admin/comments/${id}/reject`)
}

export function spamComment(id: number) {
  return http.post(`/admin/comments/${id}/spam`)
}

export function deleteComment(id: number) {
  return http.delete(`/admin/comments/${id}`)
}
