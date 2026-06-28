import { apiFetch } from '~/utils/http'

export interface FriendLink {
  id: number
  name: string
  url: string
  avatarUrl?: string
  description?: string
}

export function fetchFriendLinks() {
  return apiFetch<FriendLink[]>('/friend-links')
}

export function applyFriendLink(data: {
  name: string
  url: string
  avatarUrl?: string
  description?: string
  ownerEmail: string
}) {
  return apiFetch<FriendLink>('/friend-links/apply', {
    method: 'POST',
    body: JSON.stringify(data)
  })
}
