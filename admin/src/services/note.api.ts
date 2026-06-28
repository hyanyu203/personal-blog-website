import http from './http'
import type { PageResult } from './article.api'

export interface NoteItem {
  id: number
  contentMd?: string
  status: string
  publishedAt?: string
}

export function fetchNotes(page = 1) {
  return http.get< PageResult<NoteItem>>(`/admin/notes?page=${page}`)
}

export function createNote(data: { contentMd: string }) {
  return http.post('/admin/notes', data)
}

export function publishNote(id: number) {
  return http.post(`/admin/notes/${id}/publish`)
}

export function deleteNote(id: number) {
  return http.delete(`/admin/notes/${id}`)
}
