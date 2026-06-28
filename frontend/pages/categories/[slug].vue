<template>
  <div>
    <h1>分类：{{ category?.name || slug }}</h1>
    <div v-if="pending">加载中…</div>
    <div v-else-if="error">{{ userFacingError(error) }}</div>
    <template v-else>
      <p v-if="category?.description" class="desc">{{ category.description }}</p>
      <p v-if="!category">分类不存在</p>
      <ArticleCard v-for="item in articles?.items" :key="item.id" :article="item" />
    </template>
  </div>
</template>

<script setup lang="ts">
import { fetchCategory, fetchArticlesByCategory } from '~/services/taxonomy.api'
import { userFacingError } from '~/utils/userErrorMessage'

const route = useRoute()
const slug = route.params.slug as string

const { data: category, pending, error } = await useAsyncData(
  `cat-${slug}`,
  () => fetchCategory(slug)
)
const { data: articles } = await useAsyncData(
  `cat-posts-${slug}`,
  () => fetchArticlesByCategory(slug),
  { watch: [() => category.value?.slug] }
)

useSeoMeta({ title: () => category.value ? `${category.value.name} - 渐构` : '渐构' })
</script>

<style scoped>
.desc { color: var(--color-muted); }
</style>
