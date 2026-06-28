<template>
  <div>
    <h1>文章</h1>
    <div v-if="pending">加载中…</div>
    <div v-else-if="error">{{ userFacingError(error) }}</div>
    <template v-else>
      <ArticleCard v-for="item in data?.items" :key="item.id" :article="item" />
      <nav v-if="data && (data.hasMore || page > 1)" class="pagination">
        <NuxtLink v-if="page > 1" :to="`/posts?page=${page - 1}`">上一页</NuxtLink>
        <span>第 {{ page }} 页</span>
        <NuxtLink v-if="data.hasMore" :to="`/posts?page=${page + 1}`">下一页</NuxtLink>
      </nav>
    </template>
  </div>
</template>

<script setup lang="ts">
import { fetchArticles } from '~/services/article.api'
import { userFacingError } from '~/utils/userErrorMessage'

const route = useRoute()
const page = computed(() => Number(route.query.page || 1))

const { data, pending, error } = await useAsyncData(
  () => `posts-${page.value}`,
  () => fetchArticles(page.value, 20),
  { watch: [page] }
)

useSeoMeta({ title: '文章 - 渐构' })
</script>

<style scoped>
.pagination {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
  align-items: center;
}
</style>
