import DOMPurify from 'dompurify'
import { HTML_SANITIZE_CONFIG, applyDomPurifyHooks } from '@jiangou/shared'

applyDomPurifyHooks(DOMPurify as unknown as Parameters<typeof applyDomPurifyHooks>[0])

export function sanitizeHtml(html: string): string {
  if (!html) return ''
  return DOMPurify.sanitize(html, HTML_SANITIZE_CONFIG)
}
