export interface SeoOptions {
  title?: string
  description?: string
  path?: string
  type?: 'website' | 'article' | 'book' | 'profile' | 'music.song' | 'music.album' | 'music.playlist' | 'music.radio_status' | 'video.movie' | 'video.episode' | 'video.tv_show' | 'video.other'
}

export function usePageSeo(options: SeoOptions) {
  const config = useRuntimeConfig()
  const siteUrl = (config.public.siteUrl as string).replace(/\/$/, '')
  const path = options.path || ''
  const url = `${siteUrl}${path.startsWith('/') ? path : `/${path}`}`
  const title = options.title ? `${options.title} - 渐构` : '渐构'
  const description = options.description || '渐次构建，理解计算机世界'

  useSeoMeta({
    title,
    description,
    ogTitle: title,
    ogDescription: description,
    ogUrl: url,
    ogType: options.type || 'website',
    twitterCard: 'summary',
    twitterTitle: title,
    twitterDescription: description
  })

  useHead({
    link: [{ rel: 'canonical', href: url }]
  })
}
