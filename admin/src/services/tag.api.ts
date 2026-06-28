import http from './http'

export interface TagItem {
  id: number
  name: string
  slug: string
  color?: string
}

export function fetchTags() {
  return http.get< TagItem[]>('/admin/tags')
}

export function createTag(data: Record<string, unknown>) {
  return http.post('/admin/tags', data)
}

export function updateTag(id: number, data: Record<string, unknown>) {
  return http.patch(`/admin/tags/${id}`, data)
}

export function deleteTag(id: number) {
  return http.delete(`/admin/tags/${id}`)
}
