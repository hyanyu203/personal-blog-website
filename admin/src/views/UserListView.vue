<template>
  <div>
    <h1>用户管理</h1>
    <div class="filters">
      <button :class="{ active: filter === '' }" @click="setFilter('')">全部</button>
      <button :class="{ active: filter === 'active' }" @click="setFilter('active')">活跃</button>
      <button :class="{ active: filter === 'disabled' }" @click="setFilter('disabled')">禁用</button>
    </div>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-else-if="loading">加载中…</p>
    <div v-else class="card">
      <table class="table">
        <thead><tr><th>用户名</th><th>显示名</th><th>角色</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="u in items" :key="u.id">
            <td>{{ u.username }}</td>
            <td>{{ u.displayName }}</td>
            <td>{{ u.roles.join(', ') || '-' }}</td>
            <td>{{ u.status }}</td>
            <td><button class="secondary" @click="edit(u)">编辑</button></td>
          </tr>
        </tbody>
      </table>
      <PaginationBar :page="page" :has-more="hasMore" :total="total" @change="loadPage" />
    </div>

    <form v-if="editing" class="card form" @submit.prevent="save">
      <h3>编辑用户 #{{ editing.id }}</h3>
      <input v-model="form.displayName" placeholder="显示名" />
      <input v-model="form.email" placeholder="邮箱" />
      <select v-model="form.status">
        <option value="active">active</option>
        <option value="disabled">disabled</option>
      </select>
      <input v-model="rolesText" placeholder="角色，逗号分隔，如 USER,ADMIN" />
      <div class="actions">
        <button type="submit">保存</button>
        <button type="button" class="secondary" @click="editing = null">取消</button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { computeHasMore } from '@jiangou/shared'
import PaginationBar from '@/components/PaginationBar.vue'
import { fetchUsers, updateUser, type UserItem } from '@/services/user.api'

const items = ref<UserItem[]>([])
const loading = ref(false)
const error = ref('')
const filter = ref('')
const page = ref(1)
const total = ref(0)
const hasMore = ref(false)
const editing = ref<UserItem | null>(null)
const form = ref({ displayName: '', email: '', status: 'active' })
const rolesText = ref('')

async function loadPage(p = page.value) {
  loading.value = true
  error.value = ''
  try {
    const data = await fetchUsers(filter.value || undefined, p)
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

function edit(u: UserItem) {
  editing.value = u
  form.value = { displayName: u.displayName, email: u.email || '', status: u.status }
  rolesText.value = u.roles.join(', ')
}

async function save() {
  if (!editing.value) return
  try {
    const roles = rolesText.value.split(',').map((s) => s.trim()).filter(Boolean)
    await updateUser(editing.value.id, { ...form.value, roles })
    editing.value = null
    await loadPage()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '保存失败'
  }
}

onMounted(() => loadPage())
</script>

<style scoped>
.filters { display: flex; gap: 0.5rem; margin-bottom: 1rem; }
.filters button.active { background: #7b5f3a; color: #fff; }
.form { margin-top: 1rem; display: flex; flex-direction: column; gap: 0.75rem; max-width: 480px; }
.actions { display: flex; gap: 0.5rem; }
.error { color: #c0392b; }
</style>
