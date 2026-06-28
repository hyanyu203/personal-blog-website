<template>
  <div class="auth-page">
    <h1>忘记密码</h1>
    <form class="card" @submit.prevent="submit">
      <label>
        注册邮箱
        <input v-model="email" type="email" autocomplete="email" required />
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
      <div class="code-row">
        <label class="grow">
          邮箱验证码
          <input v-model="emailCode" maxlength="6" required />
        </label>
        <button type="button" :disabled="sendingCode" @click="sendCode">
          {{ sendingCode ? '发送中…' : '发送验证码' }}
        </button>
      </div>
      <label>
        新密码
        <input v-model="newPassword" type="password" autocomplete="new-password" required />
      </label>
      <button type="submit" :disabled="loading">{{ loading ? '提交中…' : '重置密码' }}</button>
      <p v-if="error" class="error">{{ error }}</p>
      <p v-if="message" class="msg">{{ message }}</p>
      <p class="links"><NuxtLink to="/login">返回登录</NuxtLink></p>
    </form>
  </div>
</template>

<script setup lang="ts">
import { fetchCaptcha, resetPassword, sendResetCode } from '~/services/auth.api'

definePageMeta({ middleware: 'guest-only' })

const router = useRouter()

const email = ref('')
const emailCode = ref('')
const newPassword = ref('')
const captchaId = ref('')
const captchaCode = ref('')
const captchaImage = ref('')
const captchaSrc = computed(() =>
  /^data:image\/(png|jpeg);base64,/.test(captchaImage.value) ? captchaImage.value : ''
)
const loading = ref(false)
const sendingCode = ref(false)
const error = ref('')
const message = ref('')

async function loadCaptcha() {
  try {
    const data = await fetchCaptcha()
    captchaId.value = data.captchaId
    captchaImage.value = data.imageBase64
  } catch (e) {
    error.value = e instanceof Error ? e.message : '验证码加载失败'
  }
}

async function sendCode() {
  error.value = ''
  message.value = ''
  if (!email.value || !captchaCode.value) {
    error.value = '请填写邮箱和图形验证码'
    return
  }
  if (!captchaId.value) await loadCaptcha()
  sendingCode.value = true
  try {
    await sendResetCode(email.value, captchaId.value, captchaCode.value)
    message.value = '若邮箱存在，验证码已发送'
    await loadCaptcha()
    captchaCode.value = ''
  } catch (e) {
    error.value = e instanceof Error ? e.message : '发送失败'
    await loadCaptcha()
  } finally {
    sendingCode.value = false
  }
}

async function submit() {
  error.value = ''
  loading.value = true
  try {
    await resetPassword(email.value, emailCode.value, newPassword.value)
    message.value = '密码已重置，请登录'
    await router.push('/login')
  } catch (e) {
    error.value = e instanceof Error ? e.message : '重置失败'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCaptcha()
})
useSeoMeta({ title: '忘记密码 - 渐构' })
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
input {
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background: var(--color-bg);
  color: var(--color-text);
}
.captcha-row, .code-row { display: flex; gap: 0.5rem; align-items: flex-end; }
.grow { flex: 1; }
.captcha-btn {
  padding: 0;
  border: 1px solid var(--color-border);
  background: transparent;
  border-radius: 4px;
  cursor: pointer;
  height: 38px;
  min-width: 100px;
}
.captcha-btn img { display: block; height: 36px; width: 100px; }
button[type='submit'], .code-row button {
  padding: 0.6rem;
  background: var(--color-accent);
  color: var(--color-bg);
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.code-row button { white-space: nowrap; padding: 0.5rem 0.75rem; }
button:disabled { opacity: 0.7; cursor: default; }
.error { color: #c0392b; font-size: 0.9rem; }
.msg { color: var(--color-muted); font-size: 0.9rem; }
.links { font-size: 0.9rem; text-align: center; }
</style>
