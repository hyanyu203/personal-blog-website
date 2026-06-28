<template>
  <component :is="tag" v-html="safe" />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { sanitizeHtml } from '~/utils/sanitize'

const props = withDefaults(defineProps<{
  html: string
  tag?: string
}>(), {
  tag: 'div'
})

const safe = computed(() => sanitizeHtml(props.html))
</script>
