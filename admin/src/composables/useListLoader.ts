import { ref, type Ref } from 'vue'
import type { PageResult } from '@jiangou/shared'

export function useListLoader<T>(loader: (page: number) => Promise<PageResult<T>>) {
  const items: Ref<T[]> = ref([])
  const loading = ref(false)
  const error = ref('')
  const page = ref(1)
  const hasMore = ref(false)
  const total = ref(0)

  async function load(p = page.value) {
    loading.value = true
    error.value = ''
    try {
      const data = await loader(p)
      items.value = data.items
      page.value = data.page
      total.value = data.total
      hasMore.value = data.page * data.pageSize < data.total
    } catch (e) {
      error.value = e instanceof Error ? e.message : '加载失败'
    } finally {
      loading.value = false
    }
  }

  return { items, loading, error, page, hasMore, total, load }
}
