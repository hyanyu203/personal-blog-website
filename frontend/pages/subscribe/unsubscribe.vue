<template>
  <div>
    <h1>退订</h1>
    <p v-if="pending">处理中…</p>
    <p v-else-if="error">{{ error }}</p>
    <p v-else class="success">{{ message }}</p>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { unsubscribe } from '~/services/subscribe.api'
import { warmupCsrfCookie } from '~/utils/http'

const route = useRoute()
const token = route.query.token as string | undefined
const message = ref('')
const error = ref('')
const pending = ref(true)

onMounted(() => {
  if (!token) {
    error.value = '缺少退订 token'
    pending.value = false
    return
  }
  warmupCsrfCookie('no-referrer')
    .then(() => unsubscribe(token))
    .then((res) => {
      message.value = res.message
    })
    .catch((e) => {
      error.value = e instanceof Error ? e.message : '退订失败'
    })
    .finally(() => {
      pending.value = false
    })
})

useSeoMeta({ title: '退订 - 渐构' })
</script>

<style scoped>
.success { color: var(--color-accent); }
</style>
