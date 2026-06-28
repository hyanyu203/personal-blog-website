<template>
  <div>
    <h1>友链审核</h1>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-else-if="loading">加载中…</p>
    <div v-else class="card">
      <table class="table">
        <thead><tr><th>站点</th><th>URL</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="f in items" :key="f.id">
            <td>{{ f.name }}</td>
            <td>{{ f.url }}</td>
            <td>{{ f.status }}</td>
            <td>
              <button v-if="f.status === 'pending'" @click="runAction(() => approveFriend(f.id))">通过</button>
              <button v-if="f.status === 'pending'" class="secondary" @click="runAction(() => rejectFriend(f.id))">拒绝</button>
              <button class="secondary" @click="runAction(() => deleteFriend(f.id))">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-if="!items.length">暂无友链</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useSimpleLoader } from '@/composables/useSimpleLoader'
import { approveFriend, deleteFriend, fetchFriends, rejectFriend } from '@/services/friend.api'

const { items, loading, error, load, runAction } = useSimpleLoader(fetchFriends)

onMounted(load)
</script>

<style scoped>
.error { color: #c0392b; }
</style>
