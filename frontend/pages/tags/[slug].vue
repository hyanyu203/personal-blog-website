<template>
  <div>
    <h1>标签：{{ tag?.name || slug }}</h1>
    <div v-if="pending">加载中…</div>
    <div v-else-if="error">{{ userFacingError(error) }}</div>
    <template v-else>
      <p v-if="!tag">标签不存在</p>
      <ArticleCard v-for="item in articles?.items" :key="item.id" :article="item" />
    </template>
  </div>
</template>

<script setup lang="ts">
import { fetchTag, fetchArticlesByTag } from '~/services/taxonomy.api'
import { userFacingError } from '~/utils/userErrorMessage'

const route = useRoute()
const slug = route.params.slug as string
const { data: tag, pending, error } = await useAsyncData(`tag-${slug}`, () => fetchTag(slug))
const { data: articles } = await useAsyncData(`tag-posts-${slug}`, () => fetchArticlesByTag(slug))
useSeoMeta({ title: () => tag.value ? `${tag.value.name} - 渐构` : '渐构' })
</script>
