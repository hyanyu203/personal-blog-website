<template>
  <div>
    <h1>系统设置</h1>
    <div v-if="isAdmin" class="card actions">
      <button @click="rebuildSearch">重建搜索索引</button>
      <button @click="rebuildRss">重建 RSS</button>
      <p v-if="msg" class="msg">{{ msg }}</p>
    </div>
    <div class="card">
      <h2>站点配置</h2>
      <form @submit.prevent="saveSiteTitle">
        <label>站点标题
          <input v-model="siteTitle" :disabled="!canEditSettings" />
        </label>
        <button type="submit" :disabled="!canEditSettings">保存 siteTitle</button>
      </form>
      <form @submit.prevent="saveSiteDescription">
        <label>站点简介
          <textarea v-model="siteDescription" rows="3" :disabled="!canEditSettings"></textarea>
        </label>
        <button type="submit" :disabled="!canEditSettings">保存 siteDescription</button>
      </form>
      <table v-if="settings.length" class="table">
        <thead><tr><th>Key</th><th>Value</th><th>公开</th></tr></thead>
        <tbody>
          <tr v-for="s in settings" :key="s.key">
            <td>{{ s.key }}</td>
            <td>{{ s.value }}</td>
            <td>{{ s.isPublic ? '是' : '否' }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-if="isAdmin" class="card">
      <h2>审计日志</h2>
      <table v-if="auditLogs.length" class="table">
        <thead><tr><th>时间</th><th>操作</th><th>目标</th></tr></thead>
        <tbody>
          <tr v-for="log in auditLogs" :key="log.id">
            <td>{{ log.createdAt }}</td>
            <td>{{ log.action }}</td>
            <td>{{ log.targetType }} #{{ log.targetId }}</td>
          </tr>
        </tbody>
      </table>
      <p v-else class="empty">暂无审计记录</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { canUpdateSettings, isAdminRole } from '@jiangou/shared'
import {
  fetchSettings, updateSetting, rebuildSearch as rebuildSearchApi,
  rebuildRss as rebuildRssApi, fetchAuditLogs,
  type SettingItem, type AuditLogItem
} from '@/services/settings.api'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const currentUser = computed(() => auth.user)
const isAdmin = computed(() => (currentUser.value ? isAdminRole(currentUser.value.roles) : false))
const canEditSettings = computed(() => (currentUser.value ? canUpdateSettings(currentUser.value) : false))

const settings = ref<SettingItem[]>([])
const auditLogs = ref<AuditLogItem[]>([])
const siteTitle = ref('渐构')
const siteDescription = ref('')
const msg = ref('')

async function load() {
  settings.value = await fetchSettings()
  const title = settings.value.find((s) => s.key === 'siteTitle')
  if (title) siteTitle.value = title.value
  const desc = settings.value.find((s) => s.key === 'siteDescription')
  if (desc) siteDescription.value = desc.value
  if (isAdmin.value) {
    const logs = await fetchAuditLogs()
    auditLogs.value = logs.items
  } else {
    auditLogs.value = []
  }
}

async function saveSiteTitle() {
  if (!canEditSettings.value) return
  await updateSetting('siteTitle', siteTitle.value)
  msg.value = '已保存'
  await load()
}

async function saveSiteDescription() {
  if (!canEditSettings.value) return
  await updateSetting('siteDescription', siteDescription.value)
  msg.value = '已保存'
  await load()
}

async function rebuildSearch() {
  if (!isAdmin.value) return
  const res = await rebuildSearchApi()
  msg.value = `搜索索引已重建，${res.indexed} 条`
}

async function rebuildRss() {
  if (!isAdmin.value) return
  await rebuildRssApi()
  msg.value = 'RSS 已重建'
}

onMounted(load)
</script>

<style scoped>
.actions {
  margin-bottom: 1rem;
  display: flex;
  gap: 0.75rem;
  align-items: center;
  flex-wrap: wrap;
}
form {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  max-width: 400px;
  margin-bottom: 1.5rem;
}
.msg { color: #7b5f3a; margin: 0; }
.empty { color: #9a948a; }
</style>
