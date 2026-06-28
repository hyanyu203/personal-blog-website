<template>
  <div>
    <h1>仪表盘</h1>
    <div v-if="stats" class="stats">
      <div class="card stat">
        <span class="num">{{ stats.articleCount }}</span>
        <span class="label">已发布文章</span>
      </div>
      <div class="card stat">
        <span class="num">{{ stats.draftCount }}</span>
        <span class="label">草稿</span>
      </div>
      <div class="card stat warn">
        <span class="num">{{ stats.pendingComments }}</span>
        <span class="label">待审评论</span>
      </div>
      <div class="card stat">
        <span class="num">{{ stats.subscriberCount }}</span>
        <span class="label">订阅者</span>
      </div>
      <div class="card stat">
        <span class="num">{{ stats.snippetCount }}</span>
        <span class="label">代码片段</span>
      </div>
      <div class="card stat">
        <span class="num">{{ stats.noteCount }}</span>
        <span class="label">碎碎念</span>
      </div>
      <div class="card stat">
        <span class="num">{{ stats.runningDays }}</span>
        <span class="label">运行天数</span>
      </div>
    </div>
    <div class="card quick">
      <h2>快捷入口</h2>
      <div class="links">
        <RouterLink to="/articles/new">写文章</RouterLink>
        <RouterLink v-if="stats?.pendingComments" to="/comments">审核评论 ({{ stats.pendingComments }})</RouterLink>
        <RouterLink to="/subscriptions">邮件订阅</RouterLink>
        <RouterLink to="/settings">系统设置</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { fetchAdminStats, type AdminStats } from '@/services/stats.api'

const stats = ref<AdminStats | null>(null)

onMounted(async () => {
  stats.value = await fetchAdminStats()
})
</script>

<style scoped>
.stats {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}
.stat {
  text-align: center;
  padding: 1.25rem;
}
.stat.warn .num { color: #c45c26; }
.num {
  display: block;
  font-size: 1.75rem;
  font-weight: 600;
  color: #7b5f3a;
}
.label {
  color: #9a948a;
  font-size: 0.85rem;
}
.quick h2 { margin: 0 0 1rem; font-size: 1rem; }
.links { display: flex; gap: 1rem; flex-wrap: wrap; }
.links a { color: #7b5f3a; }
</style>
