<template>
  <div class="layout">
    <aside class="sidebar">
      <h2>渐构 CMS</h2>
      <nav>
        <RouterLink
          v-for="item in visibleNav"
          :key="item.path"
          :to="item.path"
        >
          {{ item.label }}
        </RouterLink>
      </nav>
      <button class="logout" @click="logout">退出</button>
    </aside>
    <main class="content">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ADMIN_NAV_ITEMS } from '@jiangou/shared'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

const visibleNav = computed(() => {
  const user = auth.user
  if (!user) {
    return []
  }
  return ADMIN_NAV_ITEMS.filter((item) => item.visible(user))
})

async function logout() {
  await auth.logout()
  router.push({ name: 'login' })
}
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
}
.sidebar {
  width: 220px;
  background: #24211d;
  color: #e8e2d9;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
}
.sidebar h2 {
  margin: 0 0 1.5rem;
  font-size: 1.1rem;
}
.sidebar nav {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  flex: 1;
}
.sidebar a {
  color: #9a948a;
  text-decoration: none;
  padding: 0.5rem;
  border-radius: 4px;
}
.sidebar a.router-link-active {
  background: #3d3830;
  color: #faf7f0;
}
.logout {
  margin-top: auto;
  background: #3d3830;
}
.content {
  flex: 1;
  padding: 2rem;
}
</style>
