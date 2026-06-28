<template>
  <footer class="footer">
    <nav class="links">
      <a href="/api/v1/rss/feed.xml" target="_blank" rel="noopener">RSS</a>
      <NuxtLink to="/archives">归档</NuxtLink>
      <NuxtLink to="/privacy">隐私政策</NuxtLink>
    </nav>
    <p>© {{ year }} 渐构 · Powered by JianGou</p>
  </footer>
</template>

<script setup lang="ts">
const year = new Date().getFullYear()
</script>

<style scoped>
.footer {
  text-align: center;
  padding: 2rem;
  color: var(--color-muted);
  font-size: 0.875rem;
  border-top: 1px solid var(--color-border);
}
.links {
  display: flex;
  justify-content: center;
  gap: 1.25rem;
  margin-bottom: 0.75rem;
}
.links a {
  color: var(--color-muted);
}
</style>
