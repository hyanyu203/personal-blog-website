<template>
  <article v-if="project">
    <h1>{{ project.name }}</h1>
    <p class="desc">{{ project.description }}</p>
    <dl class="meta">
      <div v-if="project.language"><dt>语言</dt><dd>{{ project.language }}</dd></div>
      <div><dt>Stars</dt><dd>{{ project.stars }}</dd></div>
      <div><dt>Forks</dt><dd>{{ project.forks }}</dd></div>
      <div v-if="project.license"><dt>License</dt><dd>{{ project.license }}</dd></div>
    </dl>
    <a v-if="githubHref" :href="githubHref" target="_blank" rel="noopener noreferrer" class="btn">在 GitHub 查看</a>
  </article>
  <div v-else-if="pending">加载中…</div>
  <div v-else>项目不存在</div>
</template>

<script setup lang="ts">
import { fetchProjectDetail } from '~/services/project.api'
import { safeExternalHref } from '~/utils/safeUrl'

const route = useRoute()
const owner = route.params.owner as string
const repo = route.params.repo as string

const { data: project, pending } = await useAsyncData(
  `project-${owner}-${repo}`,
  () => fetchProjectDetail(owner, repo)
)

const githubHref = computed(() => safeExternalHref(project.value?.githubUrl))

useSeoMeta({
  title: () => project.value ? `${project.value.name} - 渐构` : '渐构'
})
</script>

<style scoped>
.desc { color: var(--color-muted); margin-bottom: 1.5rem; }
.meta {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}
.meta dt { font-size: 0.8rem; color: var(--color-muted); }
.meta dd { margin: 0.25rem 0 0; font-weight: 500; }
.btn {
  display: inline-block;
  padding: 0.5rem 1rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  text-decoration: none;
  color: var(--color-text);
}
</style>
