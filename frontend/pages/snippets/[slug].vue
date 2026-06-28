<template>
  <article v-if="snippet">
    <h1>{{ snippet.title }}</h1>
    <p class="meta">
      {{ snippet.language }} · {{ snippet.viewCount }} 次浏览
      <span v-if="copyCount"> · {{ copyCount }} 次复制</span>
    </p>
    <SafeHtml v-if="snippet.descriptionHtml" :html="snippet.descriptionHtml" />
    <SafeHtml class="code" :html="snippet.highlightedHtml" />
    <div class="actions">
      <button type="button" @click="doCopy">复制代码</button>
      <button type="button" :disabled="liked" @click="doLike">
        {{ liked ? '已赞' : '点赞' }} {{ likeCount }}
      </button>
      <a :href="rawUrl" target="_blank" rel="noopener">Raw</a>
    </div>
    <p v-if="msg" class="msg">{{ msg }}</p>
  </article>
</template>

<script setup lang="ts">
import { copySnippet, fetchSnippetBySlug, likeSnippet } from '~/services/snippet.api'

const route = useRoute()
const config = useRuntimeConfig()
const slug = route.params.slug as string
const rawUrl = computed(() => `${config.public.apiBase}/snippets/${slug}/raw`)
const { data: snippet } = await useAsyncData(`snippet-${slug}`, () => fetchSnippetBySlug(slug))

const liked = ref(false)
const likeCount = ref(snippet.value?.likeCount ?? 0)
const copyCount = ref(snippet.value?.copyCount ?? 0)
const msg = ref('')

watch(snippet, (s) => {
  if (s) {
    likeCount.value = s.likeCount ?? 0
    copyCount.value = s.copyCount ?? 0
  }
}, { immediate: true })

const { ensureAuthForAction, withAuth } = useAuth()

async function doCopy() {
  if (!snippet.value) return
  try {
    await navigator.clipboard.writeText(snippet.value.code)
    await copySnippet(snippet.value.id)
    copyCount.value += 1
    msg.value = '已复制到剪贴板'
  } catch {
    msg.value = '复制失败'
  }
}

async function doLike() {
  if (!snippet.value || liked.value) return
  if (!(await ensureAuthForAction())) return
  await withAuth(async () => {
    const res = await likeSnippet(snippet.value!.id)
    likeCount.value = res.likeCount
    liked.value = true
  })
}

useSeoMeta({ title: () => snippet.value ? `${snippet.value.title} - 渐构` : '渐构' })
</script>

<style scoped>
.meta { color: var(--color-muted); }
.code :deep(pre) { overflow-x: auto; padding: 1rem; background: var(--color-border); border-radius: 6px; }
.actions { display: flex; gap: 0.75rem; align-items: center; margin-top: 1rem; flex-wrap: wrap; }
.actions button {
  padding: 0.35rem 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background: transparent;
  cursor: pointer;
}
.msg { color: var(--color-accent); font-size: 0.9rem; margin-top: 0.5rem; }
</style>
