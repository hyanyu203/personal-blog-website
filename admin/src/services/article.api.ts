import http from './http'
import type { PageResult } from '@jiangou/shared'

export type { PageResult } from '@jiangou/shared'

export interface ArticleItem {
  id: number
  title: string
  slug: string
  summary: string
  status: string
  updatedAt?: string
  publishedAt?: string
}

export interface ArticleDetail extends ArticleItem {
  contentMd?: string
  contentHtml?: string
  categoryId?: number
  tagIds?: number[]
  readingMinutes?: number
  wordCount?: number
  viewCount?: number
  likeCount?: number
  commentCount?: number
  pinned?: boolean
  category?: { name: string; slug: string }
  tags?: string[]
}

export function fetchAdminArticles(page = 1) {
  return http.get<PageResult<ArticleItem>>(`/admin/articles?page=${page}&pageSize=20`)
}

export function fetchAdminArticle(id: number) {
  return http.get<ArticleDetail>(`/admin/articles/${id}`)
}

export function createArticle(data: Record<string, unknown>) {
  return http.post<ArticleDetail>('/admin/articles', data)
}

export function updateArticle(id: number, data: Record<string, unknown>) {
  return http.patch(`/admin/articles/${id}`, data)
}

export function publishArticle(id: number) {
  return http.post(`/admin/articles/${id}/publish`)
}

export function deleteArticle(id: number) {
  return http.delete(`/admin/articles/${id}`)
}

export function unpublishArticle(id: number) {
  return http.post(`/admin/articles/${id}/unpublish`)
}

export function archiveArticle(id: number) {
  return http.post(`/admin/articles/${id}/archive`)
}

export interface ArticleVersion {
  version: number
  title: string
  changeNote?: string
  createdAt: string
}

export function fetchArticleVersions(id: number) {
  return http.get<ArticleVersion[]>(`/admin/articles/${id}/versions`)
}

export interface DiffLine {
  type: 'INSERT' | 'DELETE' | 'EQUAL'
  content: string
}

export interface VersionDiff {
  fromVersion: number
  toVersion: number
  lines: DiffLine[]
}

export function fetchVersionDiff(id: number, from: number, to?: number) {
  let url = `/admin/articles/${id}/versions/diff?from=${from}`
  if (to != null) url += `&to=${to}`
  return http.get<VersionDiff>(url)
}

export function restoreArticleVersion(id: number, version: number) {
  return http.post<ArticleDetail>(`/admin/articles/${id}/restore/${version}`)
}
