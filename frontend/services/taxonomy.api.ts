import { apiFetch } from '~/utils/http'
import { fetchArticles, type ArticleListItem } from '~/services/article.api'

export interface ArchiveGroup {
  year: number
  month: number
  articles: { id: number; title: string; slug: string; publishedAt: string }[]
}

export function fetchArchives() {
  return apiFetch<ArchiveGroup[]>('/articles/archives')
}

export function fetchCategories() {
  return apiFetch<{ id: number; name: string; slug: string; postCount?: number }[]>('/categories')
}

export function fetchTags() {
  return apiFetch<{ id: number; name: string; slug: string; usageCount?: number }[]>('/tags')
}

export function fetchCategory(slug: string) {
  return apiFetch<{ id: number; name: string; slug: string; description?: string }>(
    `/categories/${slug}`
  )
}

export function fetchTag(slug: string) {
  return apiFetch<{ id: number; name: string; slug: string; color?: string }>(`/tags/${slug}`)
}

export function fetchArticlesByCategory(slug: string, page = 1) {
  return fetchArticles(page, 20, slug, undefined, undefined) as Promise<{
    items: ArticleListItem[]
    total: number
  }>
}

export function fetchArticlesByTag(slug: string, page = 1) {
  return fetchArticles(page, 20, undefined, slug, undefined) as Promise<{
    items: ArticleListItem[]
    total: number
  }>
}
