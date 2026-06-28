import http from './http'

export interface ProjectItem {
  id: number
  owner: string
  repo: string
  name: string
  description?: string
  stars: number
  syncStatus: string
  pinned: boolean
}

export function fetchProjects(page = 1, pageSize = 50) {
  return http.get<{ items: ProjectItem[] }>(`/admin/projects?page=${page}&pageSize=${pageSize}`)
    .then(r => r.items)
}

export function createProject(data: Record<string, unknown>) {
  return http.post('/admin/projects', data)
}

export function updateProject(id: number, data: Record<string, unknown>) {
  return http.patch(`/admin/projects/${id}`, data)
}

export function deleteProject(id: number) {
  return http.delete(`/admin/projects/${id}`)
}

export function syncProject(id: number) {
  return http.post(`/admin/projects/${id}/sync`)
}

export function syncAllProjects() {
  return http.post('/admin/projects/sync')
}
