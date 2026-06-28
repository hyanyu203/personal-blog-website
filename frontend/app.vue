<template>
  <div class="layout">
    <SiteHeader />
    <main class="main">
      <NuxtPage />
    </main>
    <SiteFooter />
  </div>
</template>

<script setup lang="ts">
const { initTheme } = useTheme()

onMounted(() => {
  initTheme()
})
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.main {
  flex: 1;
  max-width: 960px;
  margin: 0 auto;
  padding: 2rem 1.5rem;
  width: 100%;
}
</style>
