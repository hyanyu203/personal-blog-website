import { apiFetch, type PageResult } from '~/utils/http'
import type { ArticleListItem } from '~/services/article.api'

export interface HomeStats {
  articleCount: number
  snippetCount: number
  noteCount: number
  runningDays: number
}

export interface HomeSettings {
  siteTitle?: string
  siteSubtitle?: string
  siteDescription?: string
  [key: string]: unknown
}

export interface HomeCategory {
  id: number
  name: string
  slug: string
  postCount?: number
}

export interface HomeTag {
  id: number
  name: string
  slug: string
  usageCount?: number
}

export interface HomeData {
  articles: PageResult<ArticleListItem>
  stats: HomeStats
  settings: HomeSettings
  categories: HomeCategory[]
  tags: HomeTag[]
}

export function fetchHome() {
  return apiFetch<HomeData>('/home')
}
