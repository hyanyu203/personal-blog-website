import type { PageResult } from '~/utils/http'
import { apiFetch } from '~/utils/http'

export interface ArticleListItem {
  id: number
  title: string
  slug: string
  summary: string
  readingMinutes: number
  wordCount: number
  viewCount: number
  publishedAt: string
  pinned?: boolean
  category?: { name: string; slug: string }
  tags: string[]
}

export interface ArticleDetail {
  id: number
  title: string
  slug: string
  summary: string
  contentHtml: string
  readingMinutes: number
  wordCount: number
  viewCount: number
  likeCount?: number
  publishedAt: string
  tags: string[]
  category?: Record<string, string>
}

export function fetchArticles(page = 1, pageSize = 20, category?: string, tag?: string, keyword?: string) {
  let url = `/articles?page=${page}&pageSize=${pageSize}`
  if (category) url += `&category=${encodeURIComponent(category)}`
  if (tag) url += `&tag=${encodeURIComponent(tag)}`
  if (keyword) url += `&keyword=${encodeURIComponent(keyword)}`
  return apiFetch<PageResult<ArticleListItem>>(url)
}

export function fetchArticleBySlug(slug: string) {
  return apiFetch<ArticleDetail>(`/articles/slug/${slug}`)
}

export interface TocItem {
  id: string
  text: string
  level: number
}

export function fetchArticleToc(id: number) {
  return apiFetch<TocItem[]>(`/articles/${id}/toc`)
}

export function fetchRelatedArticles(id: number, limit = 5) {
  return apiFetch<ArticleListItem[]>(`/articles/${id}/related?limit=${limit}`)
}

export function likeArticle(id: number) {
  return apiFetch<{ likeCount: number }>(`/articles/${id}/like`, {
    method: 'POST',
    body: '{}'
  })
}
