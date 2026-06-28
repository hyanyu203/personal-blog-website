/** Only allow same-app relative paths; blocks open redirects and encoded bypasses. */
export function safeRedirect(value: string | undefined): string {
  if (!value || typeof value !== 'string') {
    return '/'
  }
  const trimmed = value.trim()
  if (!trimmed.startsWith('/') || trimmed.startsWith('//')) {
    return '/'
  }
  if (trimmed.includes('://') || trimmed.includes('\\')) {
    return '/'
  }
  try {
    const decoded = decodeURIComponent(trimmed)
    if (decoded.startsWith('//') || decoded.includes('://') || decoded.includes('\\')) {
      return '/'
    }
  } catch {
    return '/'
  }
  return trimmed
}
