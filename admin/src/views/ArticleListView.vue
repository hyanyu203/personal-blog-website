<template>
  <div>
    <div class="toolbar">
      <h1>文章管理</h1>
      <RouterLink v-if="canCreate" to="/articles/new" class="btn">写文章</RouterLink>
    </div>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-else-if="loading">加载中...</p>
    <div v-else class="card">
      <table class="table">
        <thead>
          <tr>
            <th>标题</th>
            <th>状态</th>
            <th>Slug</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.id">
            <td>{{ item.title }}</td>
            <td>{{ item.status }}</td>
            <td>{{ item.slug }}</td>
            <td class="actions">
              <RouterLink v-if="canUpdate || canPublish" :to="`/articles/${item.id}/edit`">
                {{ canUpdate ? '编辑' : '查看' }}
              </RouterLink>
              <button v-if="canPublish && item.status !== 'published'" @click="publish(item.id)">发布</button>
              <button v-if="canUpdate && item.status === 'published'" @click="unpublish(item.id)">下线</button>
              <button v-if="canUpdate && item.status === 'published'" class="secondary" @click="archive(item.id)">归档</button>
              <button v-if="canUpdate" class="secondary" @click="remove(item.id)">删除</button>
              <span v-if="!canUpdate && !canPublish">-</span>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-if="!items.length">暂无文章</p>
      <PaginationBar :page="page" :has-more="hasMore" :total="total" @change="load" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { canCreateArticles, canPublishArticles, canUpdateArticles } from '@jiangou/shared'
import PaginationBar from '@/components/PaginationBar.vue'
import { useListLoader } from '@/composables/useListLoader'
import {
  fetchAdminArticles,
  publishArticle,
  unpublishArticle,
  archiveArticle,
  deleteArticle,
  type ArticleItem
} from '@/services/article.api'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const currentUser = computed(() => auth.user)
const canCreate = computed(() => (currentUser.value ? canCreateArticles(currentUser.value) : false))
const canUpdate = computed(() => (currentUser.value ? canUpdateArticles(currentUser.value) : false))
const canPublish = computed(() => (currentUser.value ? canPublishArticles(currentUser.value) : false))

const { items, loading, error, page, hasMore, total, load } = useListLoader<ArticleItem>(fetchAdminArticles)

async function publish(id: number) {
  if (!canPublish.value) return
  try {
    await publishArticle(id)
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '操作失败'
  }
}

async function unpublish(id: number) {
  if (!canUpdate.value) return
  try {
    await unpublishArticle(id)
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '操作失败'
  }
}

async function archive(id: number) {
  if (!canUpdate.value || !confirm('确认归档吗？')) return
  try {
    await archiveArticle(id)
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '操作失败'
  }
}

async function remove(id: number) {
  if (!canUpdate.value || !confirm('确认删除吗？')) return
  try {
    await deleteArticle(id)
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '操作失败'
  }
}

onMounted(() => load())
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1rem;
}
.actions {
  display: flex;
  gap: 0.75rem;
  align-items: center;
  flex-wrap: wrap;
}
.actions button {
  padding: 0.25rem 0.5rem;
  font-size: 0.85rem;
}
.error { color: #c0392b; }
</style>
