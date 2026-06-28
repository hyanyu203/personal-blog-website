<template>
  <nav v-if="hasMore || page > 1" class="pagination">
    <button type="button" :disabled="page <= 1" @click="$emit('change', page - 1)">上一页</button>
    <span>第 {{ page }} 页 · 共 {{ total }} 条</span>
    <button type="button" :disabled="!hasMore" @click="$emit('change', page + 1)">下一页</button>
  </nav>
</template>

<script setup lang="ts">
defineProps<{ page: number; hasMore: boolean; total: number }>()
defineEmits<{ change: [page: number] }>()
</script>

<style scoped>
.pagination {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-top: 1rem;
  font-size: 0.9rem;
}
.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
