<template>
  <div class="oauth-page">
    <p v-if="error" class="error">{{ error }}</p>
    <p v-else>登录成功，正在跳转…</p>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { exchangeOAuthCode } from '@/services/auth.api'
import { safeRedirect } from '@/utils/safeRedirect'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const error = ref('')

onMounted(async () => {
  const code = route.query.code as string
  if (!code) {
    error.value = 'OAuth 回调缺少 code'
    return
  }
  try {
    const data = await exchangeOAuthCode(code)
    auth.setUserFromOAuth(data.user)
    const redirect = safeRedirect(route.query.redirect as string)
    router.replace(redirect === '/' ? { name: 'dashboard' } : redirect)
  } catch (e) {
    error.value = e instanceof Error ? e.message : '登录失败'
  }
})
</script>

<style scoped>
.oauth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
.error { color: #c0392b; }
</style>
