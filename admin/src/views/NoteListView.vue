<template>
  <div>
    <div class="toolbar">
      <h1>碎碎念</h1>
      <button class="btn" @click="showForm = !showForm">新建</button>
    </div>
    <form v-if="showForm" class="card form" @submit.prevent="create">
      <textarea v-model="content" rows="4" placeholder="Markdown 内容" required />
      <button type="submit">保存草稿</button>
    </form>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-else-if="loading">加载中…</p>
    <div v-else class="card">
      <table class="table">
        <thead><tr><th>内容</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="n in items" :key="n.id">
            <td>{{ (n.contentMd || '').slice(0, 60) }}</td>
            <td>{{ n.status }}</td>
            <td>
              <button v-if="n.status !== 'published'" @click="publish(n.id)">发布</button>
              <button class="secondary" @click="remove(n.id)">删除</button>
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
import { createNote, deleteNote, fetchNotes, publishNote, type NoteItem } from '@/services/note.api'

const { items, loading, error, page, hasMore, total, load } = useListLoader<NoteItem>(fetchNotes)
const showForm = ref(false)
const content = ref('')

async function create() {
  try {
    await createNote({ contentMd: content.value })
    content.value = ''
    showForm.value = false
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '保存失败'
  }
}

async function publish(id: number) {
  try {
    await publishNote(id)
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '发布失败'
  }
}

async function remove(id: number) {
  if (!confirm('删除？')) return
  try {
    await deleteNote(id)
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
