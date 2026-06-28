<template>
  <div class="home">
    <section class="hero">
      <h1>渐构</h1>
      <p class="tagline">{{ settings?.siteSubtitle || '渐次构建，理解计算机世界' }}</p>
      <p v-if="stats" class="stats">
        {{ stats.articleCount }} 篇文章 · {{ stats.snippetCount }} 代码片段 ·
        {{ stats.noteCount }} 碎碎念 · 运行 {{ stats.runningDays }} 天
      </p>
    </section>

    <div class="grid">
      <section class="main-col">
        <h2>最近文章</h2>
        <div v-if="pending">加载中…</div>
        <div v-else-if="error">{{ loadErrorMessage }}</div>
        <template v-else>
          <ArticleCard v-for="item in articles" :key="item.id" :article="item" />
          <p v-if="!articles?.length" class="empty">暂无文章</p>
        </template>
      </section>

      <aside class="sidebar">
        <div v-if="categories?.length" class="side-block">
          <h3>分类</h3>
          <ul>
            <li v-for="c in categories" :key="c.id">
              <NuxtLink :to="`/categories/${c.slug}`">{{ c.name }}</NuxtLink>
            </li>
          </ul>
        </div>
        <div v-if="tags?.length" class="side-block">
          <h3>标签</h3>
          <div class="tag-cloud">
            <NuxtLink v-for="t in tags.slice(0, 20)" :key="t.id" :to="`/tags/${t.slug}`" class="tag">
              {{ t.name }}
            </NuxtLink>
          </div>
        </div>
        <div class="side-block">
          <h3>订阅</h3>
          <p><NuxtLink to="/subscribe">邮件订阅</NuxtLink></p>
          <p><a href="/api/v1/rss/feed.xml" target="_blank" rel="noopener">RSS Feed</a></p>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { fetchHome } from '~/services/home.api'
import { userFacingError } from '~/utils/userErrorMessage'

const { data: home, pending, error } = await useAsyncData('home', () => fetchHome())

const articles = computed(() => home.value?.articles?.items ?? [])
const stats = computed(() => home.value?.stats)
const settings = computed(() => home.value?.settings)
const categories = computed(() => home.value?.categories ?? [])
const tags = computed(() => home.value?.tags ?? [])

const loadErrorMessage = computed(() => userFacingError(error.value))

usePageSeo({
  title: settings.value?.siteTitle || '渐构',
  description: settings.value?.siteDescription || '渐次构建，理解计算机世界',
  path: '/'
})
</script>

<style scoped>
.hero {
  text-align: center;
  padding: 2rem 0;
}
.hero h1 {
  font-size: 2.5rem;
  margin: 0;
}
.tagline {
  color: var(--color-muted);
  margin-top: 0.5rem;
}
.stats {
  color: var(--color-muted);
  font-size: 0.9rem;
  margin-top: 0.75rem;
}
.grid {
  display: grid;
  grid-template-columns: 1fr 220px;
  gap: 2rem;
  align-items: start;
}
.sidebar {
  position: sticky;
  top: 1rem;
}
.side-block {
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--color-border);
}
.side-block h3 {
  margin: 0 0 0.75rem;
  font-size: 1rem;
}
.side-block ul {
  list-style: none;
  padding: 0;
  margin: 0;
}
.side-block li {
  margin-bottom: 0.35rem;
}
.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 0.35rem;
}
.tag {
  background: var(--color-border);
  padding: 0.1rem 0.5rem;
  border-radius: 4px;
  font-size: 0.85rem;
  color: var(--color-muted);
  text-decoration: none;
}
.empty {
  color: var(--color-muted);
}
@media (max-width: 768px) {
  .grid {
    grid-template-columns: 1fr;
  }
  .sidebar {
    position: static;
  }
}
</style>
