<template>
  <div>
    <h1>留言板</h1>
    <CommentSection v-if="targetId" target-type="guestbook" :target-id="targetId" />
    <p v-else-if="pending">加载中…</p>
    <p v-else class="error">留言板未配置，请在后台设置 guestbookTargetId。</p>
  </div>
</template>

<script setup lang="ts">
import { useSiteSettings } from '~/composables/useSiteSettings'

useSeoMeta({ title: '留言板 - 渐构' })

const { data: settings, pending } = useSiteSettings()

const targetId = computed(() => {
  const raw = settings.value?.guestbookTargetId
  const id = raw != null ? Number(raw) : NaN
  return Number.isFinite(id) && id > 0 ? id : null
})
</script>

<style scoped>
.error { color: var(--color-muted); }
</style>
