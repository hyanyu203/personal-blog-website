import { defineStore } from 'pinia'
import { canAccessAdmin, type AuthUser } from '@jiangou/shared'
import { login as loginApi, logoutApi, fetchMe } from '@/services/auth.api'

// 5-minute local cache for the session; forced invalidation still relies on tokenVersion.
const SESSION_TTL_MS = 300_000

function assertAdminAccess(user: AuthUser) {
  if (!canAccessAdmin(user)) {
    throw new Error('无后台访问权限')
  }
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as AuthUser | null,
    sessionVerifiedAt: 0
  }),
  getters: {
    isLoggedIn: (s) => !!s.user
  },
  actions: {
    async login(username: string, password: string, captchaId: string, captchaCode: string) {
      const data = await loginApi(username, password, captchaId, captchaCode)
      assertAdminAccess(data.user)
      this.user = data.user
      this.sessionVerifiedAt = Date.now()
    },
    async restoreSession() {
      return this.ensureAdminSession()
    },
    async ensureAdminSession() {
      if (this.user && Date.now() - this.sessionVerifiedAt < SESSION_TTL_MS) {
        return true
      }
      try {
        const user = await fetchMe()
        assertAdminAccess(user)
        this.user = user
        this.sessionVerifiedAt = Date.now()
        return true
      } catch {
        this.user = null
        this.sessionVerifiedAt = 0
        return false
      }
    },
    setUserFromOAuth(user: AuthUser) {
      assertAdminAccess(user)
      this.user = user
      this.sessionVerifiedAt = Date.now()
    },
    async logout() {
      try {
        await logoutApi()
      } catch {
        // ignore network errors during logout
      }
      this.user = null
      this.sessionVerifiedAt = 0
    }
  }
})
