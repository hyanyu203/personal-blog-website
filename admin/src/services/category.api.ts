import http from './http'

export interface CategoryItem {
  id: number
  name: string
  slug: string
  description?: string
}

export function fetchCategories() {
  return http.get< CategoryItem[]>('/admin/categories')
}

export function createCategory(data: Record<string, unknown>) {
  return http.post('/admin/categories', data)
}

export function updateCategory(id: number, data: Record<string, unknown>) {
  return http.patch(`/admin/categories/${id}`, data)
}

export function deleteCategory(id: number) {
  return http.delete(`/admin/categories/${id}`)
}
