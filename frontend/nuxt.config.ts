export default defineNuxtConfig({
  devtools: { enabled: false },
  telemetry: false,
  runtimeConfig: {
    apiBaseInternal: process.env.NUXT_API_BASE_INTERNAL || '',
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE || '/api/v1',
      siteUrl: process.env.NUXT_PUBLIC_SITE_URL || 'http://localhost:3000'
    }
  },
  app: {
    head: {
      title: '渐构',
      meta: [{ name: 'description', content: '渐次构建，理解计算机世界' }]
    }
  },
  css: ['~/assets/css/main.css'],
  routeRules: {
    '/': { swr: 3600 },
    '/posts/**': { swr: 3600 },
    '/categories/**': { swr: 3600 },
    '/tags/**': { swr: 3600 },
    '/archives': { swr: 3600 },
    '/snippets/**': { swr: 3600 },
    '/notes/**': { swr: 3600 },
    '/subscribe/confirm': {
      headers: {
        'X-Frame-Options': 'DENY',
        'X-Content-Type-Options': 'nosniff',
        'Referrer-Policy': 'no-referrer'
      }
    },
    '/subscribe/unsubscribe': {
      headers: {
        'X-Frame-Options': 'DENY',
        'X-Content-Type-Options': 'nosniff',
        'Referrer-Policy': 'no-referrer'
      }
    },
    '/**': {
      headers: {
        'X-Frame-Options': 'DENY',
        'X-Content-Type-Options': 'nosniff',
        'Referrer-Policy': 'strict-origin-when-cross-origin'
      }
    }
  },
  nitro: {
    devProxy: {
      '/api/v1': {
        target: 'http://localhost:8080/api/v1',
        changeOrigin: true
      }
    }
  }
})
