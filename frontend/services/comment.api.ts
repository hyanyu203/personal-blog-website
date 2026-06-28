import { apiFetch } from '~/utils/http'

export interface CommentItem {
  id: number
  parentId?: number
  depth: number
  nickname: string
  website?: string
  contentHtml: string
  likeCount: number
  createdAt: string
  replies?: CommentItem[]
}

export function fetchComments(targetType: string, targetId: number) {
  return apiFetch<CommentItem[]>(
    `/comments?targetType=${targetType}&targetId=${targetId}`
  )
}

export function postComment(data: {
  targetType: string
  targetId: number
  parentId?: number
  website?: string
  contentMd: string
}) {
  return apiFetch<CommentItem>('/comments', {
    method: 'POST',
    body: JSON.stringify(data)
  })
}

export function likeComment(id: number) {
  return apiFetch<{ likeCount: number }>(`/comments/${id}/like`, {
    method: 'POST',
    body: '{}'
  })
}
