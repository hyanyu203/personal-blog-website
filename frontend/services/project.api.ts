import { apiFetch } from '~/utils/http'

export interface ProjectItem {
  id: number
  owner: string
  repo: string
  name: string
  description: string
  githubUrl: string
  language: string
  stars: number
  forks: number
  pinned: boolean
}

export function fetchProjects() {
  return apiFetch<ProjectItem[]>('/projects')
}

export function fetchProjectDetail(owner: string, repo: string) {
  return apiFetch<ProjectItem>(`/projects/${owner}/${repo}`)
}
