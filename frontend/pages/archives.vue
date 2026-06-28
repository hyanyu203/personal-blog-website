<template>
  <div>
    <h1>归档</h1>
    <div v-if="pending">加载中…</div>
    <section v-for="g in groups" :key="`${g.year}-${g.month}`" class="group">
      <h2>{{ g.year }} 年 {{ g.month }} 月</h2>
      <ul>
        <li v-for="a in g.articles" :key="a.id">
          <NuxtLink :to="`/posts/${a.slug}`">{{ a.title }}</NuxtLink>
        </li>
      </ul>
    </section>
  </div>
</template>

<script setup lang="ts">
import { fetchArchives } from '~/services/taxonomy.api'
const { data: groups, pending } = await useAsyncData('archives', () => fetchArchives())
useSeoMeta({ title: '归档 - 渐构' })
</script>

<style scoped>
.group { margin-bottom: 2rem; }
ul { list-style: none; padding: 0; }
li { padding: 0.35rem 0; }
</style>
