import { ref, type Ref } from 'vue'

/** Loader for non-paginated admin lists with error/loading state. */
export function useSimpleLoader<T>(loader: () => Promise<T[]>) {
  const items: Ref<T[]> = ref([])
  const loading = ref(false)
  const error = ref('')

  async function load() {
    loading.value = true
    error.value = ''
    try {
      items.value = await loader()
    } catch (e) {
      error.value = e instanceof Error ? e.message : '加载失败'
    } finally {
      loading.value = false
    }
  }

  async function runAction(action: () => Promise<unknown>) {
    try {
      await action()
      await load()
    } catch (e) {
      error.value = e instanceof Error ? e.message : '操作失败'
    }
  }

  return { items, loading, error, load, runAction }
}
