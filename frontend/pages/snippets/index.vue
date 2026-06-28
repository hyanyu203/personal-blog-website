<template>
  <div>
    <h1>代码片段</h1>
    <div v-if="pending">加载中…</div>
    <div v-else-if="error">{{ userFacingError(error) }}</div>
    <ul v-else class="list">
      <li v-for="s in data?.items" :key="s.id">
        <NuxtLink :to="`/snippets/${s.slug}`">
          <span class="lang">{{ s.language }}</span> {{ s.title }}
        </NuxtLink>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { fetchSnippets } from '~/services/snippet.api'
import { userFacingError } from '~/utils/userErrorMessage'
const { data, pending, error } = await useAsyncData('snippets', () => fetchSnippets())
useSeoMeta({ title: '代码片段 - 渐构' })
</script>

<style scoped>
.list { list-style: none; padding: 0; }
.list li { padding: 0.75rem 0; border-bottom: 1px solid var(--color-border); }
.lang {
  font-family: var(--font-mono);
  font-size: 0.8rem;
  background: var(--color-border);
  padding: 0.1rem 0.4rem;
  border-radius: 4px;
  margin-right: 0.5rem;
}
</style>
