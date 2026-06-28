export default defineNuxtPlugin(() => {
  const config = useRuntimeConfig()
  if (process.env.NODE_ENV === 'production' && !config.apiBaseInternal) {
    throw new Error(
      'NUXT_API_BASE_INTERNAL is required for SSR in production. Example: http://backend:8080/api/v1'
    )
  }
})
