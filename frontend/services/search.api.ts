import { apiFetch } from '~/utils/http'

export function fetchSearchSuggest(q: string, signal?: AbortSignal) {
  if (!q.trim()) {
    return Promise.resolve([] as string[])
  }
  return apiFetch<string[]>(`/search/suggest?q=${encodeURIComponent(q)}`, { signal })
}
