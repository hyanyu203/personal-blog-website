<template>
  <div>
    <div class="toolbar">
      <h1>分类管理</h1>
      <button class="btn" @click="showForm = true; resetForm()">新建</button>
    </div>
    <form v-if="showForm" class="card form" @submit.prevent="save">
      <h3>{{ editingId ? '编辑分类' : '新建分类' }}</h3>
      <input v-model="form.name" placeholder="名称" required />
      <input v-model="form.slug" placeholder="slug" required />
      <input v-model="form.description" placeholder="描述" />
      <button type="submit">保存</button>
    </form>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-else-if="loading">加载中…</p>
    <div v-else class="card">
      <table class="table">
        <thead><tr><th>名称</th><th>slug</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="c in items" :key="c.id">
            <td>{{ c.name }}</td>
            <td>{{ c.slug }}</td>
            <td>
              <button class="secondary" @click="startEdit(c)">编辑</button>
              <button class="secondary" @click="remove(c.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useSimpleLoader } from '@/composables/useSimpleLoader'
import { createCategory, deleteCategory, fetchCategories, updateCategory } from '@/services/category.api'

const { items, loading, error, load, runAction } = useSimpleLoader(fetchCategories)
const showForm = ref(false)
const editingId = ref<number | null>(null)
const form = ref({ name: '', slug: '', description: '' })

function resetForm() {
  form.value = { name: '', slug: '', description: '' }
  editingId.value = null
  showForm.value = false
}

async function create() {
  try {
    await createCategory(form.value)
    resetForm()
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '保存失败'
  }
}

function startEdit(c: { id: number; name: string; slug: string; description?: string }) {
  editingId.value = c.id
  form.value = { name: c.name, slug: c.slug, description: c.description || '' }
  showForm.value = true
}

async function save() {
  try {
    if (editingId.value) {
      await updateCategory(editingId.value, form.value)
    } else {
      await createCategory(form.value)
    }
    resetForm()
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '保存失败'
  }
}

async function remove(id: number) {
  if (!confirm('删除？')) return
  await runAction(() => deleteCategory(id))
}

onMounted(load)
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; }
.form { margin-bottom: 1rem; display: flex; flex-direction: column; gap: 0.75rem; }
.error { color: #c0392b; }
</style>
