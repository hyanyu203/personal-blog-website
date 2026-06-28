type DomPurifyLike = {
  addHook: (entryPoint: string, hook: (node: Element) => void) => void
}

const ALLOWED_URI = /^(https?:|mailto:)/i

function sanitizeUrlAttr(node: Element, attr: string) {
  const value = node.getAttribute(attr)
  if (!value) {
    return
  }
  if (!ALLOWED_URI.test(value.trim())) {
    node.removeAttribute(attr)
  }
}

export function applyDomPurifyHooks(DOMPurify: DomPurifyLike) {
  DOMPurify.addHook('afterSanitizeAttributes', (node) => {
    if (node.tagName === 'A') {
      sanitizeUrlAttr(node, 'href')
      if (node.getAttribute('target') === '_blank') {
        node.setAttribute('rel', 'noopener noreferrer')
      }
    }
    if (node.tagName === 'IMG') {
      sanitizeUrlAttr(node, 'src')
    }
  })
}
