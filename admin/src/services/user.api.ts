import http from './http'
import type { PageResult } from '@jiangou/shared'

export interface UserItem {
  id: number
  username: string
  displayName: string
  email?: string
  status: string
  provider?: string
  roles: string[]
  permissions: string[]
  createdAt: string
}

export function fetchUsers(status?: string, page = 1) {
  let url = `/admin/users?page=${page}&pageSize=20`
  if (status) url += `&status=${encodeURIComponent(status)}`
  return http.get<PageResult<UserItem>>(url)
}

export function fetchUser(id: number) {
  return http.get<UserItem>(`/admin/users/${id}`)
}

export function updateUser(id: number, data: Record<string, unknown>) {
  return http.patch(`/admin/users/${id}`, data)
}
