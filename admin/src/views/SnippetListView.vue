<template>
  <div>
    <div class="toolbar">
      <h1>代码片段</h1>
      <button class="btn" @click="showForm = true; resetForm()">新建</button>
    </div>
    <form v-if="showForm" class="card form" @submit.prevent="save">
      <h3>{{ editingId ? '编辑片段' : '新建片段' }}</h3>
      <input v-model="form.title" placeholder="标题" required />
      <input v-model="form.slug" placeholder="slug" required />
      <input v-model="form.language" placeholder="语言" required />
      <textarea v-model="form.code" rows="8" placeholder="代码" required />
      <button type="submit">保存</button>
    </form>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-else-if="loading">加载中…</p>
    <div v-else class="card">
      <table class="table">
        <thead><tr><th>标题</th><th>语言</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="s in items" :key="s.id">
            <td>{{ s.title }}</td>
            <td>{{ s.language }}</td>
            <td>
              <button class="secondary" @click="startEdit(s)">编辑</button>
              <button class="secondary" @click="remove(s.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <PaginationBar :page="page" :has-more="hasMore" :total="total" @change="load" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import PaginationBar from '@/components/PaginationBar.vue'
import { useListLoader } from '@/composables/useListLoader'
import { createSnippet, deleteSnippet, fetchSnippets, updateSnippet, type SnippetItem } from '@/services/snippet.api'

const { items, loading, error, page, hasMore, total, load } = useListLoader<SnippetItem>(fetchSnippets)
const showForm = ref(false)
const editingId = ref<number | null>(null)
const form = ref({ title: '', slug: '', language: 'java', code: '' })

function resetForm() {
  form.value = { title: '', slug: '', language: 'java', code: '' }
  editingId.value = null
}

function startEdit(s: SnippetItem) {
  editingId.value = s.id
  form.value = { title: s.title, slug: s.slug, language: s.language, code: s.code }
  showForm.value = true
}

async function save() {
  try {
    if (editingId.value) {
      await updateSnippet(editingId.value, form.value)
    } else {
      await createSnippet(form.value)
    }
    showForm.value = false
    resetForm()
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '保存失败'
  }
}

async function remove(id: number) {
  if (!confirm('删除？')) return
  try {
    await deleteSnippet(id)
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '删除失败'
  }
}

onMounted(() => load())
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; }
.form { margin-bottom: 1rem; display: flex; flex-direction: column; gap: 0.75rem; }
.error { color: #c0392b; }
</style>
