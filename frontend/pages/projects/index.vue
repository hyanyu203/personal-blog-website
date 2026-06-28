<template>
  <div>
    <h1>开源项目</h1>
    <div v-if="pending">加载中…</div>
    <div v-else class="grid">
      <article v-for="p in projects" :key="p.id" class="card">
        <h2>
          <NuxtLink :to="`/projects/${p.owner}/${p.repo}`">{{ p.name }}</NuxtLink>
          <span v-if="p.pinned" class="pin">置顶</span>
        </h2>
        <p>{{ p.description }}</p>
        <div class="meta">
          <span v-if="p.language">{{ p.language }}</span>
          <span>★ {{ p.stars }}</span>
          <span>Fork {{ p.forks }}</span>
        </div>
      </article>
    </div>
  </div>
</template>

<script setup lang="ts">
import { fetchProjects } from '~/services/project.api'
const { data: projects, pending } = await useAsyncData('projects', () => fetchProjects())
useSeoMeta({ title: '项目 - 渐构' })
</script>

<style scoped>
.grid { display: grid; gap: 1rem; }
.card {
  padding: 1.25rem;
  border: 1px solid var(--color-border);
  border-radius: 8px;
}
.card h2 { margin: 0 0 0.5rem; font-size: 1.1rem; }
.card h2 a { color: inherit; text-decoration: none; }
.card h2 a:hover { color: var(--color-accent); }
.meta { color: var(--color-muted); font-size: 0.85rem; display: flex; gap: 1rem; }
.pin { font-size: 0.75rem; color: var(--color-accent); margin-left: 0.5rem; }
</style>
