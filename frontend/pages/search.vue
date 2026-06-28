<template>
  <div>
    <h1>搜索</h1>
    <form @submit.prevent="search">
      <input
        v-model="q"
        type="search"
        placeholder="输入关键词…"
        class="input"
        list="suggest-list"
        @input="onInput"
      />
      <datalist id="suggest-list">
        <option v-for="s in suggestions" :key="s" :value="s" />
      </datalist>
      <button type="submit">搜索</button>
    </form>
    <div class="types">
      <button
        v-for="t in types"
        :key="t.value"
        :class="{ active: type === t.value }"
        type="button"
        @click="setType(t.value)"
      >
        {{ t.label }}
      </button>
    </div>
    <p v-if="loading" class="summary">搜索中…</p>
    <p v-if="searchError" class="error">{{ searchError }}</p>
    <p v-if="total !== null && !loading" class="summary">共 {{ total }} 条结果</p>
    <ul v-if="results.length" class="results">
      <li v-for="item in results" :key="item.url + item.type">
        <span class="badge">{{ typeLabel(item.type) }}</span>
        <NuxtLink v-if="safeInternalPath(item.url)" :to="safeInternalPath(item.url)!">{{ item.title }}</NuxtLink>
        <span v-else>{{ item.title }}</span>
        <p>{{ item.snippet }}</p>
      </li>
    </ul>
    <p v-else-if="searched" class="empty">无匹配结果</p>
  </div>
</template>

<script setup lang="ts">
import { apiFetch } from '~/utils/http'
import type { PageResult } from '~/utils/http'
import { fetchSearchSuggest } from '~/services/search.api'
import { safeInternalPath } from '~/utils/safeUrl'

interface SearchItem {
  type: string
  title: string
  url: string
  snippet: string
}

const types = [
  { value: 'all', label: '全部' },
  { value: 'article', label: '文章' },
  { value: 'snippet', label: '代码' },
  { value: 'note', label: '碎碎念' },
  { value: 'project', label: '项目' }
]

const q = ref('')
const type = ref('all')
const results = ref<SearchItem[]>([])
const total = ref<number | null>(null)
const searched = ref(false)
const loading = ref(false)
const searchError = ref('')
const suggestions = ref<string[]>([])
let suggestTimer: ReturnType<typeof setTimeout> | null = null
let suggestAbort: AbortController | null = null

function typeLabel(t: string) {
  const found = types.find(x => x.value === t)
  return found ? found.label : t
}

async function search() {
  if (!q.value.trim()) return
  searched.value = true
  loading.value = true
  searchError.value = ''
  try {
    const data = await apiFetch<PageResult<SearchItem>>(
      `/search?q=${encodeURIComponent(q.value)}&type=${type.value}`
    )
    results.value = data.items
    total.value = data.total
  } catch (e) {
    searchError.value = e instanceof Error ? e.message : '搜索失败'
    results.value = []
    total.value = null
  } finally {
    loading.value = false
  }
}

function setType(t: string) {
  type.value = t
  if (q.value.trim()) search()
}

function onInput() {
  if (suggestTimer) clearTimeout(suggestTimer)
  suggestTimer = setTimeout(async () => {
    if (suggestAbort) suggestAbort.abort()
    suggestAbort = new AbortController()
    try {
      suggestions.value = await fetchSearchSuggest(q.value, suggestAbort.signal)
    } catch (e) {
      if (e instanceof DOMException && e.name === 'AbortError') return
      suggestions.value = []
    }
  }, 300)
}

useSeoMeta({ title: '搜索 - 渐构' })
</script>

<style scoped>
.input {
  width: 100%;
  max-width: 400px;
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  background: var(--color-bg);
  color: var(--color-text);
  margin-right: 0.5rem;
}
.types {
  display: flex;
  gap: 0.5rem;
  margin: 1rem 0;
  flex-wrap: wrap;
}
.types button {
  padding: 0.35rem 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background: transparent;
  cursor: pointer;
  font-size: 0.85rem;
}
.types button.active {
  background: var(--color-accent);
  color: #fff;
  border-color: var(--color-accent);
}
.summary { color: var(--color-muted); font-size: 0.9rem; }
.results {
  list-style: none;
  padding: 0;
  margin-top: 0.5rem;
}
.results li {
  padding: 1rem 0;
  border-bottom: 1px solid var(--color-border);
}
.badge {
  display: inline-block;
  font-size: 0.75rem;
  padding: 0.1rem 0.4rem;
  border-radius: 3px;
  background: var(--color-border);
  margin-right: 0.5rem;
  color: var(--color-muted);
}
.results p {
  color: var(--color-muted);
  margin: 0.25rem 0 0;
  font-size: 0.9rem;
}
.empty { color: var(--color-muted); }
.error { color: #c0392b; }
</style>
