export type ThemeMode = 'light' | 'dark' | 'system'

const STORAGE_KEY = 'jiangou-theme'

export function useTheme() {
  const mode = useState<ThemeMode>('theme-mode', () => 'system')

  function applyTheme(value: ThemeMode) {
    if (typeof window === 'undefined') return
    mode.value = value
    localStorage.setItem(STORAGE_KEY, value)
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
    const resolved = value === 'system' ? (prefersDark ? 'dark' : 'light') : value
    document.documentElement.setAttribute('data-theme', resolved)
  }

  function initTheme() {
    if (typeof window === 'undefined') return
    const saved = (localStorage.getItem(STORAGE_KEY) as ThemeMode | null) || 'system'
    applyTheme(saved)
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', () => {
      if (mode.value === 'system') applyTheme('system')
    })
  }

  return { mode, applyTheme, initTheme }
}
