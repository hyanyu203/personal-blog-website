<template>
  <div>
    <div class="toolbar">
      <h1>标签管理</h1>
      <button class="btn" @click="showForm = true; resetForm()">新建</button>
    </div>
    <form v-if="showForm" class="card form" @submit.prevent="save">
      <h3>{{ editingId ? '编辑标签' : '新建标签' }}</h3>
      <input v-model="form.name" placeholder="名称" required />
      <input v-model="form.slug" placeholder="slug" required />
      <input v-model="form.color" placeholder="颜色 #7b5f3a" />
      <button type="submit">保存</button>
    </form>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-else-if="loading">加载中…</p>
    <div v-else class="card">
      <table class="table">
        <thead><tr><th>名称</th><th>slug</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="t in items" :key="t.id">
            <td>{{ t.name }}</td>
            <td>{{ t.slug }}</td>
            <td>
              <button class="secondary" @click="startEdit(t)">编辑</button>
              <button class="secondary" @click="remove(t.id)">删除</button>
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
import { createTag, deleteTag, fetchTags, updateTag } from '@/services/tag.api'

const { items, loading, error, load, runAction } = useSimpleLoader(fetchTags)
const showForm = ref(false)
const editingId = ref<number | null>(null)
const form = ref({ name: '', slug: '', color: '' })

function resetForm() {
  form.value = { name: '', slug: '', color: '' }
  editingId.value = null
}

function startEdit(t: { id: number; name: string; slug: string; color?: string }) {
  editingId.value = t.id
  form.value = { name: t.name, slug: t.slug, color: t.color || '' }
  showForm.value = true
}

async function save() {
  try {
    if (editingId.value) {
      await updateTag(editingId.value, form.value)
    } else {
      await createTag(form.value)
    }
    resetForm()
    showForm.value = false
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '保存失败'
  }
}

async function remove(id: number) {
  if (!confirm('删除？')) return
  await runAction(() => deleteTag(id))
}

onMounted(load)
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; }
.form { margin-bottom: 1rem; display: flex; flex-direction: column; gap: 0.75rem; }
.error { color: #c0392b; }
</style>
