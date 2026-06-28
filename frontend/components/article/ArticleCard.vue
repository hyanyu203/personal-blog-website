<script setup lang="ts">
import type { ArticleListItem } from '~/services/article.api'

defineProps<{
  article: ArticleListItem
}>()

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('zh-CN')
}
</script>

<template>
  <article class="card">
    <h2>
      <span v-if="article.pinned" class="pinned">置顶</span>
      <NuxtLink :to="`/posts/${article.slug}`">{{ article.title }}</NuxtLink>
    </h2>
    <p class="summary">{{ article.summary }}</p>
    <div class="meta">
      <span v-if="article.category">{{ article.category.name }}</span>
      <span v-if="article.publishedAt">{{ formatDate(article.publishedAt) }}</span>
      <span>{{ article.readingMinutes }} 分钟 · {{ article.viewCount }} 阅读</span>
      <span v-for="tag in article.tags" :key="tag" class="tag">{{ tag }}</span>
    </div>
  </article>
</template>

<style scoped>
.card {
  padding: 1.5rem 0;
  border-bottom: 1px solid var(--color-border);
}
.card h2 {
  margin: 0 0 0.5rem;
  font-size: 1.35rem;
}
.card h2 a {
  color: var(--color-text);
  text-decoration: none;
}
.card h2 a:hover {
  color: var(--color-accent);
}
.summary {
  color: var(--color-muted);
  margin: 0 0 0.75rem;
}
.meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  font-size: 0.85rem;
  color: var(--color-muted);
}
.pinned {
  background: var(--color-accent);
  color: var(--color-bg);
  font-size: 0.7rem;
  padding: 0.1rem 0.4rem;
  border-radius: 3px;
  margin-right: 0.5rem;
  vertical-align: middle;
}
.tag {
  background: var(--color-border);
  padding: 0.1rem 0.5rem;
  border-radius: 4px;
}
</style>
