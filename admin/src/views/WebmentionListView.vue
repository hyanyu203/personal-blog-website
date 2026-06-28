<template>
  <div>
    <h1>Webmention</h1>
    <p class="hint">在系统设置中启用 <code>webmentionEnabled=true</code> 后接收外部引用。</p>
    <div class="filters">
      <button :class="{ active: filter === '' }" @click="setFilter('')">全部</button>
      <button :class="{ active: filter === 'verified' }" @click="setFilter('verified')">已验证</button>
      <button :class="{ active: filter === 'pending' }" @click="setFilter('pending')">待验证</button>
    </div>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-else-if="loading">加载中…</p>
    <div v-else class="card">
      <table class="table">
        <thead><tr><th>来源</th><th>目标</th><th>状态</th><th>时间</th></tr></thead>
        <tbody>
          <tr v-for="w in items" :key="w.id">
            <td><a v-if="safeUrl(w.sourceUrl)" :href="safeUrl(w.sourceUrl)" target="_blank" rel="noopener">{{ w.sourceUrl }}</a><span v-else>{{ w.sourceUrl }}</span></td>
            <td>{{ w.targetUrl }}</td>
            <td>{{ w.status }}</td>
            <td>{{ w.createdAt }}</td>
          </tr>
        </tbody>
      </table>
      <p v-if="!items.length">暂无 Webmention</p>
      <PaginationBar :page="page" :has-more="hasMore" :total="total" @change="loadPage" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { computeHasMore } from '@jiangou/shared'
import PaginationBar from '@/components/PaginationBar.vue'
import { fetchWebmentions, type WebmentionItem } from '@/services/webmention.api'
import { safeExternalHref } from '@/utils/safeUrl'

const safeUrl = (url: string) => safeExternalHref(url)

const items = ref<WebmentionItem[]>([])
const loading = ref(false)
const error = ref('')
const filter = ref('')
const page = ref(1)
const total = ref(0)
const hasMore = ref(false)

async function loadPage(p = page.value) {
  loading.value = true
  error.value = ''
  try {
    const data = await fetchWebmentions(filter.value || undefined, p)
    items.value = data.items
    page.value = p
    total.value = data.total
    hasMore.value = computeHasMore(data.page, data.pageSize, data.total)
  } catch (e) {
    error.value = e instanceof Error ? e.message : '加载失败'
  } finally {
    loading.value = false
  }
}

function setFilter(status: string) {
  filter.value = status
  loadPage(1)
}

onMounted(() => loadPage())
</script>

<style scoped>
.hint { color: #9a948a; font-size: 0.9rem; }
.filters { display: flex; gap: 0.5rem; margin-bottom: 1rem; }
.filters button.active { background: #7b5f3a; color: #fff; }
.error { color: #c0392b; }
td a { word-break: break-all; }
</style>
