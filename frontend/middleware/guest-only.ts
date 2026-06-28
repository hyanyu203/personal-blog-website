import { safeRedirect } from '~/utils/safeRedirect'

export default defineNuxtRouteMiddleware(async (to) => {
  const { isAuthenticated, loaded, restoreSession } = useAuth()
  if (!loaded.value) {
    await restoreSession()
  }
  if (isAuthenticated.value) {
    const redirect = typeof to.query.redirect === 'string' ? to.query.redirect : undefined
    return navigateTo(safeRedirect(redirect))
  }
})
