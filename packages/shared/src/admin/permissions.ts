import type { AuthUser } from '../types/auth'

/** Mirrors AdminAccessEvaluator.ADMIN_API_PERMISSIONS in backend. */
export const ADMIN_API_PERMISSIONS = [
  'article:create',
  'article:update',
  'article:publish',
  'comment:review',
  'project:sync',
  'setting:update',
  'user:manage'
] as const

export type AdminPermission = typeof ADMIN_API_PERMISSIONS[number]

export function isAdminRole(roles?: string[]): boolean {
  return roles?.includes('ADMIN') ?? false
}

export function hasAnyPermission(user: Pick<AuthUser, 'roles' | 'permissions'>, ...codes: string[]): boolean {
  if (isAdminRole(user.roles)) {
    return true
  }
  const perms = user.permissions ?? []
  return codes.some((code) => perms.includes(code))
}

export function canAccessAdmin(user: Pick<AuthUser, 'roles' | 'permissions'>): boolean {
  if (isAdminRole(user.roles)) {
    return true
  }
  const perms = user.permissions ?? []
  return ADMIN_API_PERMISSIONS.some((code) => perms.includes(code))
}

export function canViewArticles(user: Pick<AuthUser, 'roles' | 'permissions'>): boolean {
  return hasAnyPermission(user, 'article:create', 'article:update', 'article:publish')
}

export function canCreateArticles(user: Pick<AuthUser, 'roles' | 'permissions'>): boolean {
  return hasAnyPermission(user, 'article:create')
}

export function canUpdateArticles(user: Pick<AuthUser, 'roles' | 'permissions'>): boolean {
  return hasAnyPermission(user, 'article:update')
}

export function canPublishArticles(user: Pick<AuthUser, 'roles' | 'permissions'>): boolean {
  return hasAnyPermission(user, 'article:publish')
}

export function canReviewComments(user: Pick<AuthUser, 'roles' | 'permissions'>): boolean {
  return hasAnyPermission(user, 'comment:review')
}

export function canSyncProjects(user: Pick<AuthUser, 'roles' | 'permissions'>): boolean {
  return hasAnyPermission(user, 'project:sync')
}

export function canManageUsers(user: Pick<AuthUser, 'roles' | 'permissions'>): boolean {
  return hasAnyPermission(user, 'user:manage')
}

export function canUpdateSettings(user: Pick<AuthUser, 'roles' | 'permissions'>): boolean {
  return hasAnyPermission(user, 'setting:update')
}

/** Routes only exposed to full ADMIN role (no delegated permission code yet). */
export function canUseAdminOnlySections(user: Pick<AuthUser, 'roles'>): boolean {
  return isAdminRole(user.roles)
}

export function canViewDashboard(user: Pick<AuthUser, 'roles'>): boolean {
  return canUseAdminOnlySections(user)
}

export function canViewProjects(user: Pick<AuthUser, 'roles' | 'permissions'>): boolean {
  return canUseAdminOnlySections(user) || canSyncProjects(user)
}

export function canAccessSettings(user: Pick<AuthUser, 'roles' | 'permissions'>): boolean {
  return canUseAdminOnlySections(user) || canUpdateSettings(user)
}

export function canAccessAdminRoute(user: AuthUser, path: string): boolean {
  const normalized = path === '' ? '/' : path
  const item = ADMIN_NAV_ITEMS.find((nav) => {
    if (nav.path === '/') {
      return normalized === '/'
    }
    return normalized === nav.path || normalized.startsWith(`${nav.path}/`)
  })
  if (!item) {
    return isAdminRole(user.roles)
  }
  return item.visible(user)
}

export function firstAccessibleAdminRoute(user: AuthUser): string {
  const first = ADMIN_NAV_ITEMS.find((item) => item.visible(user))
  return first?.path || '/'
}

export type AdminNavItem = {
  path: string
  label: string
  visible: (user: AuthUser) => boolean
}

export const ADMIN_NAV_ITEMS: AdminNavItem[] = [
  { path: '/', label: '仪表盘', visible: canViewDashboard },
  { path: '/articles', label: '文章', visible: canViewArticles },
  { path: '/snippets', label: '代码片段', visible: canUseAdminOnlySections },
  { path: '/notes', label: '碎碎念', visible: canUseAdminOnlySections },
  { path: '/projects', label: '项目', visible: canViewProjects },
  { path: '/categories', label: '分类', visible: canUseAdminOnlySections },
  { path: '/tags', label: '标签', visible: canUseAdminOnlySections },
  { path: '/media', label: '媒体库', visible: canUseAdminOnlySections },
  { path: '/comments', label: '评论审核', visible: (u) => canUseAdminOnlySections(u) || canReviewComments(u) },
  { path: '/friends', label: '友链审核', visible: canUseAdminOnlySections },
  { path: '/subscriptions', label: '邮件订阅', visible: canUseAdminOnlySections },
  { path: '/webmentions', label: 'Webmention', visible: canUseAdminOnlySections },
  { path: '/users', label: '用户管理', visible: (u) => canUseAdminOnlySections(u) || canManageUsers(u) },
  { path: '/settings', label: '系统设置', visible: canAccessSettings }
]
