import http from './http'
import type { PageResult } from './article.api'

export interface AttachmentItem {
  id: number
  filename: string
  url: string
  mimeType: string
  sizeBytes: number
}

export function fetchAttachments(page = 1) {
  return http.get<PageResult<AttachmentItem>>(`/admin/attachments?page=${page}`)
}

export function uploadFile(file: File) {
  const form = new FormData()
  form.append('file', file)
  return http.post<AttachmentItem>('/admin/attachments', form)
}

export function deleteAttachment(id: number) {
  return http.delete(`/admin/attachments/${id}`)
}
