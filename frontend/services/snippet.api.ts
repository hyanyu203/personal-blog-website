import type { PageResult } from '~/utils/http'
import { apiFetch } from '~/utils/http'

export interface SnippetItem {
  id: number
  title: string
  slug: string
  language: string
  code: string
  highlightedHtml: string
  descriptionHtml?: string
  viewCount: number
  copyCount?: number
  likeCount?: number
}

export function fetchSnippets(page = 1, language?: string) {
  let url = `/snippets?page=${page}&pageSize=20`
  if (language) url += `&language=${encodeURIComponent(language)}`
  return apiFetch<PageResult<SnippetItem>>(url)
}

export function fetchSnippetBySlug(slug: string) {
  return apiFetch<SnippetItem>(`/snippets/${slug}`)
}

export function copySnippet(id: number) {
  return apiFetch<void>(`/snippets/${id}/copy`, { method: 'POST' })
}

export function likeSnippet(id: number) {
  return apiFetch<{ likeCount: number }>(`/snippets/${id}/like`, {
    method: 'POST',
    body: '{}'
  })
}
