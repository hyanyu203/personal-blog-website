<template>
  <div>
    <h1>友链</h1>
    <ul class="links">
      <li v-for="l in links" :key="l.id">
        <a
          v-if="safeExternalHref(l.url)"
          :href="safeExternalHref(l.url)"
          target="_blank"
          rel="noopener noreferrer"
        >{{ l.name }}</a>
        <span v-else>{{ l.name }}</span>
        <p v-if="l.description">{{ l.description }}</p>
      </li>
    </ul>
    <section class="apply card">
      <h2>友链申请</h2>
      <form @submit.prevent="submit">
        <input v-model="form.name" placeholder="站点名" required />
        <input v-model="form.url" placeholder="URL" required />
        <input v-model="form.ownerEmail" placeholder="邮箱" required />
        <textarea v-model="form.description" placeholder="描述" rows="3" />
        <button type="submit">提交申请</button>
        <p v-if="msg" class="msg">{{ msg }}</p>
        <p v-if="error" class="error">{{ error }}</p>
      </form>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { applyFriendLink, fetchFriendLinks } from '~/services/friend.api'
import { safeExternalHref } from '~/utils/safeUrl'

const { ensureAuthForAction, withAuth } = useAuth()
const { data: links } = await useAsyncData('friends', () => fetchFriendLinks())
const form = ref({ name: '', url: '', ownerEmail: '', description: '' })
const msg = ref('')
const error = ref('')

async function submit() {
  if (!(await ensureAuthForAction())) return
  msg.value = ''
  error.value = ''
  try {
    await withAuth(async () => {
      await applyFriendLink(form.value)
      msg.value = '申请已提交，审核通过后展示。'
      form.value = { name: '', url: '', ownerEmail: '', description: '' }
    })
  } catch (e) {
    error.value = e instanceof Error ? e.message : '提交失败'
  }
}

useSeoMeta({ title: '友链 - 渐构' })
</script>

<style scoped>
.links { list-style: none; padding: 0; }
.links li { padding: 1rem 0; border-bottom: 1px solid var(--color-border); }
.apply { margin-top: 2rem; padding: 1.5rem; border: 1px solid var(--color-border); border-radius: 8px; }
form { display: flex; flex-direction: column; gap: 0.75rem; }
.msg { color: var(--color-muted); font-size: 0.9rem; }
.error { color: #c0392b; font-size: 0.9rem; }
</style>
