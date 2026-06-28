<template>
  <div>
    <h1>碎碎念</h1>
    <p class="subtitle">时光机</p>
    <div v-if="pending">加载中…</div>
    <div v-else class="timeline">
      <article v-for="n in data?.items" :key="n.id" :id="String(n.id)" class="note">
        <time>{{ n.publishedAt }}</time>
        <SafeHtml :html="n.contentHtml" />
        <button type="button" class="like-btn" @click="doLike(n.id)">
          ♥ {{ likes[n.id] ?? n.likeCount }}
        </button>
      </article>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { fetchNotes, likeNote } from '~/services/note.api'

const { ensureAuthForAction, withAuth } = useAuth()
const { data, pending } = await useAsyncData('notes', () => fetchNotes())
const likes = reactive<Record<number, number>>({})

async function doLike(id: number) {
  if (!(await ensureAuthForAction())) return
  await withAuth(async () => {
    const res = await likeNote(id)
    likes[id] = res.likeCount
  })
}

useSeoMeta({ title: '碎碎念 - 渐构' })
</script>

<style scoped>
.subtitle { color: var(--color-muted); margin-top: -0.5rem; }
.note { padding: 1.25rem 0; border-bottom: 1px solid var(--color-border); }
time { color: var(--color-muted); font-size: 0.85rem; }
.like-btn {
  margin-top: 0.5rem;
  padding: 0.25rem 0.75rem;
  font-size: 0.85rem;
  background: transparent;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  cursor: pointer;
}
</style>
