<template>
  <div>
    <h1>关于</h1>
    <p v-if="settings?.siteDescription">{{ settings.siteDescription }}</p>
    <p v-else>渐构是个人技术知识沉淀平台，融合 Blog、Wiki、代码片段与项目陈列。</p>
    <p v-if="stats" class="stats">
      {{ stats.articleCount }} 篇文章 · {{ stats.snippetCount }} 个代码片段 ·
      {{ stats.noteCount }} 条碎碎念 · 运行 {{ stats.runningDays }} 天
    </p>
  </div>
</template>

<script setup lang="ts">
import { useSiteSettings } from '~/composables/useSiteSettings'
import { fetchStats } from '~/services/stats.api'

const { data: settings } = await useSiteSettings()
const { data: stats } = await useAsyncData('about-stats', () => fetchStats())

useSeoMeta({ title: '关于 - 渐构' })
</script>

<style scoped>
.stats {
  margin-top: 1.5rem;
  color: var(--color-muted);
  font-size: 0.9rem;
}
</style>
