<template>
  <div>
    <h1>{{ isEdit ? '编辑文章' : '写文章' }}</h1>
    <form class="card form" @submit.prevent="save">
      <p v-if="isReadOnly" class="hint">当前账号可查看或发布文章，但不能修改正文与元数据。</p>
      <label>标题 <input v-model="form.title" required :readonly="isReadOnly" /></label>
      <label>Slug <input v-model="form.slug" required :readonly="isReadOnly" /></label>
      <label>摘要 <input v-model="form.summary" :readonly="isReadOnly" /></label>
      <label>分类
        <select v-model="form.categoryId" :disabled="isReadOnly">
          <option :value="undefined">无分类</option>
          <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
        </select>
      </label>
      <fieldset class="tags" :disabled="isReadOnly">
        <legend>标签</legend>
        <label v-for="t in tags" :key="t.id" class="tag-check">
          <input type="checkbox" :value="t.id" v-model="form.tagIds" />
          {{ t.name }}
        </label>
      </fieldset>
      <label>
        正文（Markdown）
        <textarea v-model="form.contentMd" rows="16" required :readonly="isReadOnly"></textarea>
      </label>
      <div class="actions">
        <button v-if="canSave" type="submit" :disabled="saving">{{ saving ? '保存中...' : '保存草稿' }}</button>
        <button v-if="isEdit && canPublish" type="button" @click="doPublish">发布</button>
      </div>
      <p v-if="message" class="message">{{ message }}</p>
    </form>
    <div v-if="isEdit && versions.length" class="card versions">
      <h2>版本历史</h2>
      <ul>
        <li v-for="v in versions" :key="v.version">
          v{{ v.version }} · {{ v.title }}
          <span v-if="v.changeNote" class="note">（{{ v.changeNote }}）</span>
          <button type="button" class="secondary" @click="showDiff(v.version)">Diff</button>
          <button v-if="canUpdate" type="button" class="secondary" @click="restore(v.version)">恢复</button>
        </li>
      </ul>
      <div v-if="diffLines.length" class="diff-panel">
        <h3>v{{ diffMeta.from }} → v{{ diffMeta.to }}</h3>
        <pre class="diff"><code><span
          v-for="(line, i) in diffLines"
          :key="i"
          :class="line.type.toLowerCase()"
        >{{ linePrefix(line.type) }}{{ line.content }}
</span></code></pre>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { canCreateArticles, canPublishArticles, canUpdateArticles } from '@jiangou/shared'
import {
  createArticle,
  updateArticle,
  fetchAdminArticle,
  publishArticle,
  fetchArticleVersions,
  fetchVersionDiff,
  restoreArticleVersion,
  type ArticleVersion,
  type DiffLine
} from '@/services/article.api'
import { fetchCategories, type CategoryItem } from '@/services/category.api'
import { fetchTags, type TagItem } from '@/services/tag.api'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const currentUser = computed(() => auth.user)
const canCreate = computed(() => (currentUser.value ? canCreateArticles(currentUser.value) : false))
const canUpdate = computed(() => (currentUser.value ? canUpdateArticles(currentUser.value) : false))
const canPublish = computed(() => (currentUser.value ? canPublishArticles(currentUser.value) : false))

const route = useRoute()
const router = useRouter()
const id = computed(() => route.params.id ? Number(route.params.id) : null)
const isEdit = computed(() => !!id.value)
const isReadOnly = computed(() => isEdit.value && !canUpdate.value)
const canSave = computed(() => isEdit.value ? canUpdate.value : canCreate.value)

const categories = ref<CategoryItem[]>([])
const tags = ref<TagItem[]>([])

const form = reactive({
  title: '',
  slug: '',
  summary: '',
  contentMd: '',
  categoryId: undefined as number | undefined,
  tagIds: [] as number[]
})
const saving = ref(false)
const message = ref('')
const versions = ref<ArticleVersion[]>([])
const diffLines = ref<DiffLine[]>([])
const diffMeta = ref({ from: 0, to: 0 })

function linePrefix(type: string) {
  if (type === 'INSERT') return '+ '
  if (type === 'DELETE') return '- '
  return '  '
}

async function showDiff(fromVersion: number) {
  if (!id.value) return
  try {
    const diff = await fetchVersionDiff(id.value, fromVersion)
    diffLines.value = diff.lines
    diffMeta.value = { from: diff.fromVersion, to: diff.toVersion }
  } catch (e) {
    message.value = e instanceof Error ? e.message : 'Diff 加载失败'
  }
}

async function loadVersions() {
  if (!id.value) return
  versions.value = await fetchArticleVersions(id.value)
}

onMounted(async () => {
  categories.value = await fetchCategories()
  tags.value = await fetchTags()
  if (id.value) {
    const article = await fetchAdminArticle(id.value)
    form.title = article.title
    form.slug = article.slug
    form.summary = article.summary || ''
    form.contentMd = article.contentMd || ''
    form.categoryId = article.categoryId
    form.tagIds = article.tagIds || []
    await loadVersions()
  }
})

async function save() {
  if (!canSave.value) return
  saving.value = true
  message.value = ''
  try {
    const payload = {
      title: form.title,
      slug: form.slug,
      summary: form.summary,
      contentMd: form.contentMd,
      categoryId: form.categoryId,
      tagIds: form.tagIds
    }
    if (isEdit.value && id.value) {
      await updateArticle(id.value, payload)
      message.value = '已保存'
    } else {
      const created = await createArticle(payload)
      message.value = '已创建'
      router.replace({ name: 'article-edit', params: { id: created.id } })
    }
  } catch (e) {
    message.value = e instanceof Error ? e.message : '保存失败'
  } finally {
    saving.value = false
  }
}

async function doPublish() {
  if (!id.value || !canPublish.value) return
  try {
    await publishArticle(id.value)
    message.value = '已发布'
    await loadVersions()
  } catch (e) {
    message.value = e instanceof Error ? e.message : '发布失败'
  }
}

async function restore(version: number) {
  if (!id.value || !canUpdate.value || !confirm(`恢复到版本 v${version} 吗？`)) return
  try {
    const article = await restoreArticleVersion(id.value, version)
    form.title = article.title
    form.slug = article.slug
    form.summary = article.summary || ''
    form.contentMd = article.contentMd || ''
    form.categoryId = article.categoryId
    form.tagIds = article.tagIds || []
    message.value = `已恢复到 v${version}`
    await loadVersions()
  } catch (e) {
    message.value = e instanceof Error ? e.message : '恢复失败'
  }
}
</script>

<style scoped>
.form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  max-width: 800px;
}
label {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}
.hint {
  margin: 0;
  color: #7a746b;
  font-size: 0.9rem;
}
.tags {
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 0.75rem;
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}
.tag-check {
  flex-direction: row;
  align-items: center;
  gap: 0.35rem;
  font-size: 0.9rem;
}
.actions {
  display: flex;
  gap: 0.75rem;
}
.message {
  color: #7b5f3a;
}
.versions {
  margin-top: 1.5rem;
  max-width: 800px;
}
.versions ul {
  list-style: none;
  padding: 0;
  margin: 0;
}
.versions li {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem 0;
  border-bottom: 1px solid #eee;
}
.note {
  color: #9a948a;
  font-size: 0.85rem;
}
.diff-panel {
  margin-top: 1rem;
  border-top: 1px solid #eee;
  padding-top: 1rem;
}
.diff {
  background: #1a1814;
  color: #e8e2d9;
  padding: 1rem;
  border-radius: 4px;
  overflow-x: auto;
  font-size: 0.85rem;
  line-height: 1.5;
}
.diff .insert { color: #27ae60; }
.diff .delete { color: #e74c3c; }
</style>
