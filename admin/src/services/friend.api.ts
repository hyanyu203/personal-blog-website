import http from './http'

export interface FriendItem {
  id: number
  name: string
  url: string
  status: string
}

export function fetchFriends(status?: string, page = 1, pageSize = 50) {
  const params = new URLSearchParams({ page: String(page), pageSize: String(pageSize) })
  if (status) params.set('status', status)
  return http.get<{ items: FriendItem[] }>(`/admin/friend-links?${params}`).then(r => r.items)
}

export function createFriend(data: Record<string, unknown>) {
  return http.post('/admin/friend-links', data)
}

export function updateFriend(id: number, data: Record<string, unknown>) {
  return http.patch(`/admin/friend-links/${id}`, data)
}

export function approveFriend(id: number) {
  return http.post(`/admin/friend-links/${id}/approve`)
}

export function rejectFriend(id: number) {
  return http.post(`/admin/friend-links/${id}/reject`)
}

export function deleteFriend(id: number) {
  return http.delete(`/admin/friend-links/${id}`)
}
