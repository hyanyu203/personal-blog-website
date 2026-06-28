import http from './http'
import type { PageResult } from './article.api'

export interface SnippetItem {
  id: number
  title: string
  slug: string
  language: string
  code: string
}

export function fetchSnippets(page = 1) {
  return http.get< PageResult<SnippetItem>>(`/admin/snippets?page=${page}`)
}

export function createSnippet(data: Record<string, unknown>) {
  return http.post('/admin/snippets', data)
}

export function updateSnippet(id: number, data: Record<string, unknown>) {
  return http.patch(`/admin/snippets/${id}`, data)
}

export function deleteSnippet(id: number) {
  return http.delete(`/admin/snippets/${id}`)
}
