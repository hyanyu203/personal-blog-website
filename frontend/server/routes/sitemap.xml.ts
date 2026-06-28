const STATIC_PATHS = [
  '/',
  '/posts',
  '/categories',
  '/tags',
  '/archives',
  '/snippets',
  '/notes',
  '/projects',
  '/friends',
  '/about',
  '/subscribe',
  '/privacy'
]

export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig()
  const siteUrl = String(config.public.siteUrl || '').replace(/\/$/, '')
  const apiBase = config.apiBaseInternal || config.public.apiBase

  let articlePaths: string[] = []
  try {
    const res = await $fetch<{ code?: number; data?: { items?: Array<{ slug?: string }> } }>(
      `${apiBase}/articles?page=1&pageSize=100`
    )
    if (res?.code === 0 && res.data?.items) {
      articlePaths = res.data.items
        .filter((item) => item.slug)
        .map((item) => `/posts/${item.slug}`)
    }
  } catch {
    articlePaths = []
  }

  const urls = [...STATIC_PATHS, ...articlePaths]
  const lastmod = new Date().toISOString().slice(0, 10)
  const body = urls.map((path) => `
  <url>
    <loc>${siteUrl}${path}</loc>
    <lastmod>${lastmod}</lastmod>
  </url>`).join('')

  setHeader(event, 'Content-Type', 'application/xml; charset=utf-8')
  return `<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">${body}
</urlset>`
})
