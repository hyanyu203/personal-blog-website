<template>
  <div>
    <h1>订阅</h1>
    <p class="desc">通过邮件接收新文章通知，或通过 RSS 阅读器订阅。</p>
    <p class="rss-link">
      <a href="/api/v1/rss/feed.xml" target="_blank" rel="noopener">RSS Feed →</a>
    </p>
    <form v-if="!done" class="form" @submit.prevent="submit">
      <input v-model="email" type="email" placeholder="your@email.com" required />
      <button type="submit">订阅</button>
      <p v-if="error" class="error">{{ error }}</p>
    </form>
    <p v-else class="success">{{ message }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { subscribe } from '~/services/subscribe.api'

const email = ref('')
const done = ref(false)
const message = ref('')
const error = ref('')

async function submit() {
  error.value = ''
  try {
    const res = await subscribe(email.value)
    message.value = res.message
    done.value = true
  } catch (e) {
    error.value = e instanceof Error ? e.message : '订阅失败'
  }
}

usePageSeo({ title: '订阅', description: '邮件与 RSS 订阅渐构', path: '/subscribe' })
</script>

<style scoped>
.desc { color: var(--color-muted); }
.rss-link { margin: 0.5rem 0 1rem; }
.form { display: flex; gap: 0.5rem; max-width: 400px; margin-top: 1rem; }
.success { color: var(--color-accent); }
.error { color: #c0392b; }
</style>
