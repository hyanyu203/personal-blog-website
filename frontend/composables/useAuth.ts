import {
  fetchMe,
  login as loginApi,
  logoutApi,
  refreshSession,
  type AuthUser
} from '~/services/auth.api'
import { ApiError } from '~/utils/http'

export function useAuth() {
  const user = useState<AuthUser | null>('auth-user', () => null)
  const loaded = useState('auth-loaded', () => false)
  const route = useRoute()
  const router = useRouter()

  async function restoreSession() {
    try {
      user.value = await fetchMe()
      return true
    } catch {
      user.value = null
      return false
    } finally {
      loaded.value = true
    }
  }

  async function login(username: string, password: string, captchaId: string, captchaCode: string) {
    const data = await loginApi(username, password, captchaId, captchaCode)
    user.value = data.user
    loaded.value = true
  }

  async function logout() {
    try {
      await logoutApi()
    } catch {
      /* ignore */
    }
    user.value = null
  }

  function requireAuth(redirect?: string) {
    const target = redirect || route.fullPath
    return router.push(`/login?redirect=${encodeURIComponent(target)}`)
  }

  async function ensureAuthForAction() {
    if (user.value) return true
    if (!loaded.value) {
      await restoreSession()
    }
    if (user.value) return true
    await requireAuth()
    return false
  }

  async function withAuth<T>(action: () => Promise<T>): Promise<T | null> {
    try {
      return await action()
    } catch (e) {
      if (e instanceof ApiError && e.code === 40101) {
        user.value = null
        await requireAuth()
        return null
      }
      throw e
    }
  }

  const isAuthenticated = computed(() => !!user.value)

  return {
    user: readonly(user),
    loaded: readonly(loaded),
    isAuthenticated,
    restoreSession,
    login,
    logout,
    requireAuth,
    ensureAuthForAction,
    withAuth,
    refreshSession
  }
}
