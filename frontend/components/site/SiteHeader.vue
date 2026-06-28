<template>
  <header class="header">
    <NuxtLink to="/" class="logo">{{ siteTitle }}</NuxtLink>
    <nav class="nav">
      <NuxtLink to="/posts">文章</NuxtLink>
      <NuxtLink to="/snippets">代码</NuxtLink>
      <NuxtLink to="/notes">碎碎念</NuxtLink>
      <NuxtLink to="/projects">项目</NuxtLink>
      <NuxtLink to="/archives">归档</NuxtLink>
      <NuxtLink to="/friends">友链</NuxtLink>
      <NuxtLink to="/guestbook">留言</NuxtLink>
      <NuxtLink to="/search">搜索</NuxtLink>
      <NuxtLink to="/subscribe">订阅</NuxtLink>
      <NuxtLink to="/about">关于</NuxtLink>
      <template v-if="isAuthenticated">
        <span class="user">{{ user?.displayName || user?.username }}</span>
        <button type="button" class="auth-btn" @click="logout">退出</button>
      </template>
      <template v-else>
        <NuxtLink to="/login">登录</NuxtLink>
        <NuxtLink to="/register">注册</NuxtLink>
      </template>
      <ThemeToggle />
    </nav>
  </header>
</template>

<script setup lang="ts">
import { useSiteSettings } from '~/composables/useSiteSettings'

const { data: settings } = await useSiteSettings()
const siteTitle = computed(() => settings.value?.siteTitle || '渐构')
const { user, isAuthenticated, logout } = useAuth()
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid var(--color-border);
  max-width: 960px;
  margin: 0 auto;
}
.logo {
  font-family: var(--font-serif);
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-text);
  text-decoration: none;
}
.nav {
  display: flex;
  gap: 1.25rem;
  flex-wrap: wrap;
  align-items: center;
}
.nav a {
  color: var(--color-muted);
  font-size: 0.95rem;
}
.nav a.router-link-active {
  color: var(--color-accent);
}
.user {
  color: var(--color-muted);
  font-size: 0.9rem;
}
.auth-btn {
  padding: 0.15rem 0.5rem;
  font-size: 0.85rem;
  background: transparent;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  cursor: pointer;
  color: var(--color-muted);
}
</style>
