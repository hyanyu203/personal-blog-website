<template>
  <div class="auth-page">
    <h1>登录</h1>
    <form class="card" @submit.prevent="submit">
      <label>
        用户名
        <input v-model="username" autocomplete="username" required />
      </label>
      <label>
        密码
        <input v-model="password" type="password" autocomplete="current-password" required />
      </label>
      <div class="captcha-row">
        <label class="grow">
          图形验证码
          <input v-model="captchaCode" required />
        </label>
        <button type="button" class="captcha-btn" @click="loadCaptcha">
          <img v-if="captchaSrc" :src="captchaSrc" alt="验证码" />
          <span v-else>加载</span>
        </button>
      </div>
      <button type="submit" :disabled="loading">{{ loading ? '登录中…' : '登录' }}</button>
      <p v-if="error" class="error">{{ error }}</p>
      <p class="links">
        <NuxtLink to="/register">注册账号</NuxtLink>
        ·
        <NuxtLink to="/forgot-password">忘记密码</NuxtLink>
      </p>
    </form>
  </div>
</template>

<script setup lang="ts">
import { fetchCaptcha } from '~/services/auth.api'
import { safeRedirect } from '~/utils/safeRedirect'

definePageMeta({ middleware: 'guest-only' })

const route = useRoute()
const router = useRouter()
const { login } = useAuth()

const username = ref('')
const password = ref('')
const captchaId = ref('')
const captchaCode = ref('')
const captchaImage = ref('')
const captchaSrc = computed(() =>
  /^data:image\/(png|jpeg);base64,/.test(captchaImage.value) ? captchaImage.value : ''
)
const loading = ref(false)
const error = ref('')

async function loadCaptcha() {
  const data = await fetchCaptcha()
  captchaId.value = data.captchaId
  captchaImage.value = data.imageBase64
}

onMounted(() => {
  loadCaptcha().catch(() => {
    error.value = '验证码加载失败'
  })
})

async function submit() {
  error.value = ''
  loading.value = true
  try {
    if (!captchaId.value) {
      await loadCaptcha()
    }
    await login(username.value, password.value, captchaId.value, captchaCode.value)
    const redirect = safeRedirect(typeof route.query.redirect === 'string' ? route.query.redirect : undefined)
    await router.push(redirect)
  } catch (e) {
    error.value = e instanceof Error ? e.message : '登录失败'
    captchaCode.value = ''
    await loadCaptcha().catch(() => {})
  } finally {
    loading.value = false
  }
}

useSeoMeta({ title: '登录 - 渐构' })
</script>

<style scoped>
.auth-page { max-width: 420px; margin: 0 auto; }
.card {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1.5rem;
  border: 1px solid var(--color-border);
  border-radius: 8px;
}
label { display: flex; flex-direction: column; gap: 0.35rem; font-size: 0.9rem; }
.captcha-row { display: flex; gap: 0.5rem; align-items: flex-end; }
.captcha-row .grow { flex: 1; }
.captcha-btn {
  padding: 0;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background: var(--color-bg);
  cursor: pointer;
  height: 38px;
  min-width: 100px;
}
.captcha-btn img { display: block; height: 36px; width: 100px; }
input {
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background: var(--color-bg);
  color: var(--color-text);
}
button[type="submit"] {
  padding: 0.6rem;
  background: var(--color-accent);
  color: var(--color-bg);
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:disabled { opacity: 0.7; cursor: default; }
.error { color: #c0392b; font-size: 0.9rem; }
.links { font-size: 0.9rem; color: var(--color-muted); text-align: center; }
</style>
