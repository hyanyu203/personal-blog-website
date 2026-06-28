<template>
  <div v-if="article" class="article-layout">
    <article class="main">
      <header class="header">
        <h1>{{ article.title }}</h1>
        <p class="meta">
          {{ article.readingMinutes }} 分钟 · {{ article.wordCount }} 字 ·
          {{ article.viewCount }} 次阅读
          <button type="button" class="like-btn" :disabled="liked" @click="doLike">
            {{ liked ? '已赞' : '点赞' }} {{ likeCount }}
          </button>
        </p>
        <p v-if="article.summary" class="summary">{{ article.summary }}</p>
      </header>
      <SafeHtml class="article-content" :html="article.contentHtml" />
      <section v-if="related.length" class="related">
        <h2>相关文章</h2>
        <ul>
          <li v-for="item in related" :key="item.id">
            <NuxtLink :to="`/posts/${item.slug}`">{{ item.title }}</NuxtLink>
          </li>
        </ul>
      </section>
      <CommentSection v-if="article.id" target-type="article" :target-id="article.id" />
    </article>
    <aside v-if="toc.length" class="toc">
      <h2>目录</h2>
      <nav>
        <a
          v-for="item in toc"
          :key="item.id"
          :href="`#${item.id}`"
          :style="{ paddingLeft: `${(item.level - 1) * 0.75}rem` }"
        >
          {{ item.text }}
        </a>
      </nav>
    </aside>
  </div>
  <div v-else-if="pending">加载中…</div>
  <div v-else>文章不存在</div>
</template>

<script setup lang="ts">
import {
  fetchArticleBySlug,
  fetchArticleToc,
  fetchRelatedArticles,
  likeArticle
} from '~/services/article.api'

const route = useRoute()
const slug = route.params.slug as string

const { data: articleBundle, pending } = await useAsyncData(
  `article-${slug}`,
  async () => {
    const article = await fetchArticleBySlug(slug)
    if (!article?.id) {
      return { article: null, toc: [] as Awaited<ReturnType<typeof fetchArticleToc>>, related: [] as Awaited<ReturnType<typeof fetchRelatedArticles>> }
    }
    const [toc, related] = await Promise.all([
      fetchArticleToc(article.id),
      fetchRelatedArticles(article.id)
    ])
    return { article, toc, related }
  }
)

const article = computed(() => articleBundle.value?.article ?? null)
const toc = computed(() => articleBundle.value?.toc ?? [])
const related = computed(() => articleBundle.value?.related ?? [])

const liked = ref(false)
const likeCount = ref(0)

watch(article, (a) => {
  if (a) {
    likeCount.value = a.likeCount ?? 0
  }
}, { immediate: true })

const { ensureAuthForAction, withAuth } = useAuth()

async function doLike() {
  if (!article.value?.id || liked.value) return
  if (!(await ensureAuthForAction())) return
  await withAuth(async () => {
    const res = await likeArticle(article.value!.id)
    likeCount.value = res.likeCount
    liked.value = true
  })
}

useSeoMeta({
  title: () => (article.value ? `${article.value.title} - 渐构` : '渐构'),
  description: () => article.value?.summary || ''
})

watch(article, (value) => {
  if (!value) return
  usePageSeo({
    title: value.title,
    description: value.summary || '',
    path: `/posts/${value.slug}`,
    type: 'article'
  })
}, { immediate: true })

const config = useRuntimeConfig()
useHead({
  link: computed(() => article.value ? [{
    rel: 'webmention',
    href: `${config.public.apiBase}/webmention`
  }] : [])
})
</script>

<style scoped>
.article-layout {
  display: flex;
  gap: 2rem;
  align-items: flex-start;
}
.main {
  flex: 1;
  min-width: 0;
}
.header {
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--color-border);
}
.meta, .summary {
  color: var(--color-muted);
}
.like-btn {
  margin-left: 1rem;
  padding: 0.25rem 0.75rem;
  font-size: 0.85rem;
  background: transparent;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  cursor: pointer;
}
.like-btn:disabled {
  opacity: 0.6;
  cursor: default;
}
.toc {
  width: 220px;
  flex-shrink: 0;
  position: sticky;
  top: 1rem;
  padding: 1rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
}
.toc h2 {
  font-size: 0.95rem;
  margin: 0 0 0.75rem;
}
.toc nav {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}
.toc a {
  color: var(--color-muted);
  text-decoration: none;
  font-size: 0.85rem;
  line-height: 1.4;
}
.toc a:hover {
  color: var(--color-text);
}
.related {
  margin: 2rem 0;
  padding-top: 1rem;
  border-top: 1px solid var(--color-border);
}
.related ul {
  list-style: none;
  padding: 0;
}
.related li {
  padding: 0.35rem 0;
}
@media (max-width: 768px) {
  .article-layout {
    flex-direction: column;
  }
  .toc {
    width: 100%;
    position: static;
  }
}
</style>
