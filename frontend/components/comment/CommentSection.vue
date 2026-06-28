<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { fetchComments, postComment, likeComment, type CommentItem } from '~/services/comment.api'

const props = defineProps<{
  targetType: string
  targetId: number
}>()

const { isAuthenticated, ensureAuthForAction, withAuth } = useAuth()

const comments = ref<CommentItem[]>([])
const content = ref('')
const message = ref('')
const loading = ref(true)
const likedIds = ref<Set<number>>(new Set())
const replyTo = ref<number | null>(null)
const replyContent = ref('')

async function load() {
  loading.value = true
  try {
    comments.value = await fetchComments(props.targetType, props.targetId)
  } catch (e) {
    message.value = e instanceof Error ? e.message : '评论加载失败'
  } finally {
    loading.value = false
  }
}

async function submit(parentId?: number) {
  if (!(await ensureAuthForAction())) return
  const text = parentId ? replyContent.value : content.value
  if (!text.trim()) return
  message.value = ''
  try {
    const result = await withAuth(async () => {
      await postComment({
        targetType: props.targetType,
        targetId: props.targetId,
        parentId,
        contentMd: text
      })
      return true
    })
    if (!result) return
    if (parentId) {
      replyContent.value = ''
      replyTo.value = null
    } else {
      content.value = ''
    }
    message.value = '评论已提交，审核通过后显示。'
  } catch (e) {
    message.value = e instanceof Error ? e.message : '评论提交失败'
  }
}

async function doLike(comment: CommentItem) {
  if (likedIds.value.has(comment.id)) return
  if (!(await ensureAuthForAction())) return
  try {
    await withAuth(async () => {
      const res = await likeComment(comment.id)
      comment.likeCount = res.likeCount
      likedIds.value.add(comment.id)
    })
  } catch (e) {
    message.value = e instanceof Error ? e.message : '点赞失败'
  }
}

function startReply(id: number) {
  replyTo.value = replyTo.value === id ? null : id
  replyContent.value = ''
}

onMounted(async () => {
  const { restoreSession, loaded } = useAuth()
  if (!loaded.value) {
    await restoreSession()
  }
  await load()
})
</script>

<template>
  <section class="comments">
    <h2>评论</h2>
    <div v-if="loading">加载中…</div>
    <ul v-else class="list">
      <li v-for="c in comments" :key="c.id" class="item">
        <strong>{{ c.nickname }}</strong>
        <span class="time">{{ c.createdAt }}</span>
        <SafeHtml class="body" :html="c.contentHtml" />
        <div class="actions">
          <button type="button" class="like-btn" :disabled="likedIds.has(c.id)" @click="doLike(c)">
            ♥ {{ c.likeCount }}
          </button>
          <button type="button" class="reply-btn" @click="startReply(c.id)">回复</button>
        </div>
        <form v-if="replyTo === c.id && isAuthenticated" class="reply-form" @submit.prevent="submit(c.id)">
          <textarea v-model="replyContent" rows="3" placeholder="写下回复…" required />
          <button type="submit">提交回复</button>
        </form>
        <p v-else-if="replyTo === c.id" class="hint">
          <NuxtLink to="/login">登录</NuxtLink> 后回复
        </p>
        <ul v-if="c.replies?.length" class="replies">
          <li v-for="r in c.replies" :key="r.id">
            <strong>{{ r.nickname }}</strong>
            <SafeHtml class="body" :html="r.contentHtml" />
            <button type="button" class="like-btn" :disabled="likedIds.has(r.id)" @click="doLike(r)">
              ♥ {{ r.likeCount }}
            </button>
          </li>
        </ul>
      </li>
    </ul>
    <p v-if="!loading && !comments.length" class="empty">暂无评论</p>
    <form v-if="isAuthenticated" class="form" @submit.prevent="submit()">
      <textarea v-model="content" rows="4" placeholder="写下评论…" required />
      <button type="submit">提交</button>
      <p v-if="message" class="msg">{{ message }}</p>
    </form>
    <p v-else class="login-hint">
      <NuxtLink to="/login">登录</NuxtLink> 后参与评论
    </p>
  </section>
</template>

<style scoped>
.comments {
  margin-top: 3rem;
  padding-top: 2rem;
  border-top: 1px solid var(--color-border);
}
.list { list-style: none; padding: 0; }
.item { padding: 1rem 0; border-bottom: 1px solid var(--color-border); }
.time { color: var(--color-muted); font-size: 0.85rem; margin-left: 0.5rem; }
.actions { display: flex; gap: 0.5rem; margin-top: 0.35rem; }
.like-btn, .reply-btn {
  padding: 0.15rem 0.5rem;
  font-size: 0.8rem;
  background: transparent;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  cursor: pointer;
}
.like-btn:disabled { opacity: 0.6; cursor: default; }
.reply-form {
  margin-top: 0.75rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
.replies {
  margin-top: 0.75rem;
  padding-left: 1rem;
  border-left: 2px solid var(--color-border);
}
.form {
  margin-top: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}
.empty, .msg, .hint, .login-hint { color: var(--color-muted); font-size: 0.9rem; }
</style>
