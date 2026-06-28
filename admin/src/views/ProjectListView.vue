<template>
  <div>
    <div class="toolbar">
      <h1>GitHub 项目</h1>
      <div class="toolbar-actions">
        <button v-if="isAdmin" class="btn" @click="showForm = true; resetForm()">添加</button>
        <button v-if="canSync" class="secondary" @click="runAction(syncAllProjects)">同步全部</button>
      </div>
    </div>
    <form v-if="showForm && isAdmin" class="card form" @submit.prevent="save">
      <h3>{{ editingId ? '编辑项目' : '添加项目' }}</h3>
      <input v-model="form.owner" placeholder="owner" required :readonly="!!editingId" />
      <input v-model="form.repo" placeholder="repo" required :readonly="!!editingId" />
      <input v-model="form.name" placeholder="显示名称" />
      <textarea v-model="form.description" placeholder="描述" rows="3"></textarea>
      <label><input v-model="form.pinned" type="checkbox" /> 置顶</label>
      <button type="submit">保存</button>
    </form>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-else-if="loading">加载中...</p>
    <div v-else class="card">
      <table class="table">
        <thead><tr><th>项目</th><th>Stars</th><th>同步</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="p in items" :key="p.id">
            <td>{{ p.owner }}/{{ p.repo }}</td>
            <td>{{ p.stars }}</td>
            <td>{{ p.syncStatus }}</td>
            <td class="actions">
              <button v-if="canSync" @click="runAction(() => syncProject(p.id))">同步</button>
              <button v-if="isAdmin" class="secondary" @click="startEdit(p)">编辑</button>
              <button v-if="isAdmin" class="secondary" @click="remove(p.id)">删除</button>
              <span v-if="!canSync && !isAdmin">-</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { canSyncProjects, isAdminRole } from '@jiangou/shared'
import { useSimpleLoader } from '@/composables/useSimpleLoader'
import {
  createProject, deleteProject, fetchProjects, updateProject,
  syncAllProjects, syncProject
} from '@/services/project.api'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const currentUser = computed(() => auth.user)
const isAdmin = computed(() => (currentUser.value ? isAdminRole(currentUser.value.roles) : false))
const canSync = computed(() => (currentUser.value ? canSyncProjects(currentUser.value) : false))

const { items, loading, error, load, runAction } = useSimpleLoader(fetchProjects)
const showForm = ref(false)
const editingId = ref<number | null>(null)
const form = ref({ owner: '', repo: '', name: '', description: '', pinned: false })

function resetForm() {
  form.value = { owner: '', repo: '', name: '', description: '', pinned: false }
  editingId.value = null
}

function startEdit(p: { id: number; owner: string; repo: string; name: string; description?: string; pinned: boolean }) {
  if (!isAdmin.value) return
  editingId.value = p.id
  form.value = {
    owner: p.owner,
    repo: p.repo,
    name: p.name,
    description: p.description || '',
    pinned: p.pinned
  }
  showForm.value = true
}

async function save() {
  if (!isAdmin.value) return
  try {
    if (editingId.value) {
      await updateProject(editingId.value, form.value)
    } else {
      await createProject(form.value)
    }
    showForm.value = false
    resetForm()
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '保存失败'
  }
}

async function remove(id: number) {
  if (!isAdmin.value || !confirm('确认删除吗？')) return
  await runAction(() => deleteProject(id))
}

onMounted(load)
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}
.toolbar-actions {
  display: flex;
  gap: 0.5rem;
}
.form {
  margin-bottom: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}
.actions {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}
.error { color: #c0392b; }
</style>
