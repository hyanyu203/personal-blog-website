<template>
  <div>
    <h1>邮件订阅</h1>
    <form class="card newsletter" @submit.prevent="send">
      <h2>Newsletter 群发</h2>
      <input v-model="newsletter.subject" placeholder="邮件主题" required />
      <textarea v-model="newsletter.body" rows="5" placeholder="邮件正文" required />
      <button type="submit" :disabled="sending">{{ sending ? '发送中…' : '发送给已确认订阅者' }}</button>
      <p v-if="sentMsg" class="success">{{ sentMsg }}</p>
    </form>

    <div class="filters">
      <button :class="{ active: filter === '' }" @click="setFilter('')">全部</button>
      <button :class="{ active: filter === 'confirmed' }" @click="setFilter('confirmed')">已确认</button>
      <button :class="{ active: filter === 'pending' }" @click="setFilter('pending')">待确认</button>
    </div>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-else-if="loading">加载中…</p>
    <div v-else class="card">
      <table class="table">
        <thead><tr><th>邮箱</th><th>状态</th><th>订阅时间</th></tr></thead>
        <tbody>
          <tr v-for="s in items" :key="s.id">
            <td>{{ s.email }}</td>
            <td>{{ s.status }}</td>
            <td>{{ s.createdAt }}</td>
          </tr>
        </tbody>
      </table>
      <p v-if="!items.length">暂无订阅</p>
      <PaginationBar :page="page" :has-more="hasMore" :total="total" @change="loadPage" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { computeHasMore } from '@jiangou/shared'
import PaginationBar from '@/components/PaginationBar.vue'
import { fetchSubscriptions, sendNewsletter, type SubscriptionItem } from '@/services/subscription.api'

const items = ref<SubscriptionItem[]>([])
const loading = ref(false)
const error = ref('')
const filter = ref('')
const page = ref(1)
const total = ref(0)
const hasMore = ref(false)
const newsletter = ref({ subject: '', body: '' })
const sending = ref(false)
const sentMsg = ref('')

async function send() {
  sending.value = true
  sentMsg.value = ''
  try {
    const res = await sendNewsletter(newsletter.value.subject, newsletter.value.body)
    sentMsg.value = `已发送 ${res.sent} 封`
    newsletter.value = { subject: '', body: '' }
  } catch (e) {
    error.value = e instanceof Error ? e.message : '发送失败'
  } finally {
    sending.value = false
  }
}

async function loadPage(p = page.value) {
  loading.value = true
  error.value = ''
  try {
    const data = await fetchSubscriptions(filter.value || undefined, p)
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
.filters { display: flex; gap: 0.5rem; margin-bottom: 1rem; }
.newsletter { margin-bottom: 1.5rem; display: flex; flex-direction: column; gap: 0.75rem; max-width: 560px; }
.success { color: #27ae60; }
.filters button.active { background: #7b5f3a; color: #fff; }
.error { color: #c0392b; }
</style>
