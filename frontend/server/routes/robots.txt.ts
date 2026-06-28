export default defineEventHandler((event) => {
  const config = useRuntimeConfig()
  const siteUrl = String(config.public.siteUrl || '').replace(/\/$/, '')
  setHeader(event, 'Content-Type', 'text/plain; charset=utf-8')
  return [
    'User-agent: *',
    'Allow: /',
    'Disallow: /login',
    'Disallow: /register',
    'Disallow: /forgot-password',
    `Sitemap: ${siteUrl}/sitemap.xml`
  ].join('\n')
})
