<template>
  <div class="theme-toggle">
    <button
      v-for="opt in options"
      :key="opt.value"
      type="button"
      :class="{ active: mode === opt.value }"
      :title="opt.label"
      @click="applyTheme(opt.value)"
    >
      {{ opt.icon }}
    </button>
  </div>
</template>

<script setup lang="ts">
import type { ThemeMode } from '~/composables/useTheme'

const { mode, applyTheme } = useTheme()

const options: { value: ThemeMode; label: string; icon: string }[] = [
  { value: 'light', label: '浅色', icon: '☀' },
  { value: 'dark', label: '深色', icon: '☾' },
  { value: 'system', label: '跟随系统', icon: '◐' }
]
</script>

<style scoped>
.theme-toggle {
  display: flex;
  gap: 0.25rem;
}
.theme-toggle button {
  border: 1px solid var(--color-border);
  background: transparent;
  color: var(--color-muted);
  border-radius: 4px;
  padding: 0.2rem 0.45rem;
  cursor: pointer;
  font-size: 0.85rem;
  line-height: 1;
}
.theme-toggle button.active {
  color: var(--color-accent);
  border-color: var(--color-accent);
}
</style>
