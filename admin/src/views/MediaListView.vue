<template>
  <div>
    <div class="toolbar">
      <h1>媒体库</h1>
      <label class="btn upload-btn">
        上传文件
        <input type="file" accept="image/*,.pdf" hidden @change="onUpload" />
      </label>
    </div>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-else-if="loading">加载中…</p>
    <div v-else class="card">
      <table class="table">
        <thead><tr><th>文件名</th><th>URL</th><th>大小</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="a in items" :key="a.id">
            <td>{{ a.filename }}</td>
            <td><a v-if="safeUrl(a.url)" :href="safeUrl(a.url)" target="_blank" rel="noopener noreferrer">打开</a></td>
            <td>{{ formatSize(a.sizeBytes) }}</td>
            <td><button class="secondary" @click="remove(a.id)">删除</button></td>
          </tr>
        </tbody>
      </table>
      <PaginationBar :page="page" :has-more="hasMore" :total="total" @change="load" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import PaginationBar from '@/components/PaginationBar.vue'
import { useListLoader } from '@/composables/useListLoader'
import { deleteAttachment, fetchAttachments, uploadFile, type AttachmentItem } from '@/services/media.api'
import { safeExternalHref } from '@/utils/safeUrl'

const safeUrl = (url: string) => safeExternalHref(url)

const { items, loading, error, page, hasMore, total, load } = useListLoader<AttachmentItem>(fetchAttachments)

async function onUpload(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  try {
    await uploadFile(file)
    input.value = ''
    await load()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '上传失败'
  }
}

async function remove(id: number) {
  if (!confirm('删除？')) return
  try {
    await deleteAttachment(id)
    await load()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '删除失败'
  }
}

function formatSize(bytes: number) {
  if (bytes < 1024) return bytes + ' B'
  return (bytes / 1024).toFixed(1) + ' KB'
}

onMounted(() => load())
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; }
.upload-btn { cursor: pointer; display: inline-block; }
.error { color: #c0392b; }
</style>
