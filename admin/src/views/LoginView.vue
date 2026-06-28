<template>
  <div class="login-page">
    <form class="card login-form" @submit.prevent="submit">
      <h1>渐构 CMS</h1>
      <label>
        用户名
        <input v-model="username" required autocomplete="username" />
      </label>
      <label>
        密码
        <input v-model="password" type="password" required autocomplete="current-password" />
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
      <p v-if="error" class="error">{{ error }}</p>
      <button type="submit" :disabled="loading">{{ loading ? '登录中…' : '登录' }}</button>
      <a v-if="githubEnabled" :href="githubLoginUrl()" class="github-btn">使用 GitHub 登录</a>
      <p v-if="isDev" class="hint">dev 环境默认账号 admin / admin123</p>
    </form>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { fetchCaptcha, fetchGitHubOAuthEnabled, githubLoginUrl } from '@/services/auth.api'
import { safeRedirect } from '@/utils/safeRedirect'

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
const githubEnabled = ref(false)
const isDev = import.meta.env.DEV

onMounted(async () => {
  await loadCaptcha()
  try {
    const res = await fetchGitHubOAuthEnabled()
    githubEnabled.value = res.enabled
  } catch {
    githubEnabled.value = false
  }
})

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

async function loadCaptcha() {
  try {
    const data = await fetchCaptcha()
    captchaId.value = data.captchaId
    captchaImage.value = data.imageBase64
  } catch {
    error.value = '验证码加载失败'
  }
}

async function submit() {
  loading.value = true
  error.value = ''
  try {
    if (!captchaId.value) {
      await loadCaptcha()
    }
    await auth.login(username.value, password.value, captchaId.value, captchaCode.value)
    router.push(safeRedirect(route.query.redirect as string))
  } catch (e) {
    error.value = e instanceof Error ? e.message : '登录失败'
    captchaCode.value = ''
    await loadCaptcha()
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-form {
  width: 360px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.login-form h1 {
  margin: 0;
  text-align: center;
}
label {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  font-size: 0.9rem;
}
.captcha-row {
  display: flex;
  gap: 0.5rem;
  align-items: flex-end;
}
.captcha-row .grow {
  flex: 1;
}
.captcha-btn {
  padding: 0;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
  height: 38px;
  min-width: 100px;
}
.captcha-btn img {
  display: block;
  height: 36px;
  width: 100px;
}
.error {
  color: #c0392b;
  margin: 0;
  font-size: 0.9rem;
}
.github-btn {
  display: block;
  text-align: center;
  padding: 0.6rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  text-decoration: none;
  color: #333;
  background: #fafafa;
}
.hint {
  color: #7a746b;
  font-size: 0.8rem;
  text-align: center;
  margin: 0;
}
</style>
