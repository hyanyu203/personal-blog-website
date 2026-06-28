import { createRouter, createWebHistory } from 'vue-router'
import {
  canAccessAdminRoute,
  canAccessSettings,
  canCreateArticles,
  canManageUsers,
  canReviewComments,
  canViewArticles,
  canViewDashboard,
  canViewProjects,
  firstAccessibleAdminRoute,
  type AuthUser
} from '@jiangou/shared'
import { useAuthStore } from '@/stores/auth'

type AccessCheck = (user: AuthUser) => boolean

const router = createRouter({
  history: createWebHistory('/admin/'),
  routes: [
    { path: '/login', name: 'login', component: () => import('@/views/LoginView.vue') },
    { path: '/oauth/callback', name: 'oauth-callback', component: () => import('@/views/OAuthCallbackView.vue') },
    {
      path: '/',
      component: () => import('@/layouts/AdminLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        { path: '', name: 'dashboard', component: () => import('@/views/DashboardView.vue'), meta: { access: canViewDashboard } },
        { path: 'articles', name: 'articles', component: () => import('@/views/ArticleListView.vue'), meta: { access: canViewArticles } },
        { path: 'articles/new', name: 'article-new', component: () => import('@/views/ArticleEditorView.vue'), meta: { access: canCreateArticles } },
        { path: 'articles/:id/edit', name: 'article-edit', component: () => import('@/views/ArticleEditorView.vue'), meta: { access: canViewArticles } },
        { path: 'snippets', name: 'snippets', component: () => import('@/views/SnippetListView.vue') },
        { path: 'notes', name: 'notes', component: () => import('@/views/NoteListView.vue') },
        { path: 'projects', name: 'projects', component: () => import('@/views/ProjectListView.vue'), meta: { access: canViewProjects } },
        { path: 'comments', name: 'comments', component: () => import('@/views/CommentListView.vue'), meta: { access: canReviewComments } },
        { path: 'friends', name: 'friends', component: () => import('@/views/FriendListView.vue') },
        { path: 'subscriptions', name: 'subscriptions', component: () => import('@/views/SubscriptionListView.vue') },
        { path: 'webmentions', name: 'webmentions', component: () => import('@/views/WebmentionListView.vue') },
        { path: 'users', name: 'users', component: () => import('@/views/UserListView.vue'), meta: { access: canManageUsers } },
        { path: 'categories', name: 'categories', component: () => import('@/views/CategoryListView.vue') },
        { path: 'tags', name: 'tags', component: () => import('@/views/TagListView.vue') },
        { path: 'media', name: 'media', component: () => import('@/views/MediaListView.vue') },
        { path: 'settings', name: 'settings', component: () => import('@/views/SettingsView.vue'), meta: { access: canAccessSettings } }
      ]
    }
  ]
})

function resolveRouteAccess(user: AuthUser, path: string, access?: AccessCheck): boolean {
  if (typeof access === 'function') {
    return access(user)
  }
  return canAccessAdminRoute(user, path)
}

router.beforeEach(async (to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth) {
    const ok = await auth.ensureAdminSession()
    if (!ok) {
      return { name: 'login', query: { redirect: to.fullPath } }
    }
    if (auth.user && !resolveRouteAccess(auth.user, to.path, to.meta.access as AccessCheck | undefined)) {
      return { path: firstAccessibleAdminRoute(auth.user) }
    }
  }
  if (to.name === 'login' && auth.isLoggedIn && auth.user) {
    return { path: firstAccessibleAdminRoute(auth.user) }
  }
})

export default router
