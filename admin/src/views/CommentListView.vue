<template>
  <div>
    <div class="toolbar">
      <h1>评论审核</h1>
      <select v-model="statusFilter" @change="load(1)">
        <option value="">全部</option>
        <option value="pending">待审核</option>
        <option value="approved">已通过</option>
        <option value="rejected">已拒绝</option>
        <option value="spam">垃圾</option>
      </select>
    </div>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-else-if="loading">加载中…</p>
    <div v-else class="card">
      <table class="table">
        <thead><tr><th>作者</th><th>内容</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="c in items" :key="c.id">
            <td>{{ c.nickname }}</td>
            <td v-html="sanitizeHtml(c.contentHtml)" />
            <td>{{ c.status }}</td>
            <td class="actions">
              <button v-if="c.status === 'pending'" @click="approve(c.id)">通过</button>
              <button v-if="c.status === 'pending'" class="secondary" @click="reject(c.id)">拒绝</button>
              <button v-if="c.status !== 'spam'" class="secondary" @click="spam(c.id)">垃圾</button>
              <button class="secondary" @click="remove(c.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-if="!items.length">暂无评论</p>
      <PaginationBar :page="page" :has-more="hasMore" :total="total" @change="load" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import PaginationBar from '@/components/PaginationBar.vue'
import { sanitizeHtml } from '@/utils/sanitize'
import { useListLoader } from '@/composables/useListLoader'
import {
  approveComment, fetchCommentsPage, rejectComment,
  spamComment, deleteComment, type CommentItem
} from '@/services/comment.api'

const statusFilter = ref('pending')
const { items, loading, error, page, hasMore, total, load } = useListLoader<CommentItem>(
  (p) => fetchCommentsPage(statusFilter.value || undefined, p)
)

async function withReload(fn: () => Promise<unknown>) {
  try {
    await fn()
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '操作失败'
  }
}

function approve(id: number) { return withReload(() => approveComment(id)) }
function reject(id: number) { return withReload(() => rejectComment(id)) }
function spam(id: number) { return withReload(() => spamComment(id)) }

async function remove(id: number) {
  if (!confirm('确认删除？')) return
  await withReload(() => deleteComment(id))
}

onMounted(() => load())
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
}
.toolbar select {
  padding: 0.35rem 0.5rem;
  border: 1px solid var(--color-border, #ddd);
  border-radius: 4px;
}
.actions { display: flex; gap: 0.5rem; flex-wrap: wrap; }
.error { color: #c0392b; }
</style>
