import type { PageResult } from '~/utils/http'
import { apiFetch } from '~/utils/http'

export interface NoteItem {
  id: number
  contentHtml: string
  publishedAt: string
  likeCount: number
}

export function fetchNotes(page = 1) {
  return apiFetch<PageResult<NoteItem>>(`/notes?page=${page}&pageSize=30`)
}

export function likeNote(id: number) {
  return apiFetch<{ likeCount: number }>(`/notes/${id}/like`, {
    method: 'POST',
    body: '{}'
  })
}
