import { test, expect } from '@playwright/test'
import { isBackendReady, loginViaApi, loginViaApiSession, adminApiRequest, requireBackendReady } from '../helpers/backend'

async function skipIfNoBackend(): Promise<boolean> {
  if (await isBackendReady()) {
    return false
  }
  if (requireBackendReady()) {
    throw new Error('Backend required but not reachable')
  }
  test.skip(true, '后端未运行（需 docker compose + SPRING_PROFILES_ACTIVE=e2e,dev）')
  return true
}

test.describe('后台 CMS', () => {
  test('登录页显示表单', async ({ page }) => {
    await page.goto('/login')
    await expect(page.locator('h1')).toContainText('登录')
    await expect(page.locator('input[type="password"]')).toBeVisible()
  })

  test('未登录访问仪表盘重定向到登录', async ({ page }) => {
    await page.goto('/')
    await expect(page).toHaveURL(/login/)
  })

  test('admin 账号登录后进入仪表盘', async ({ page }) => {
    if (await skipIfNoBackend()) {
      return
    }

    await page.goto('/login')
    await expect(page.locator('img[alt="验证码"]')).toBeVisible({ timeout: 15000 })
    await page.fill('input[autocomplete="username"]', 'admin')
    await page.fill('input[autocomplete="current-password"]', 'admin123')
    await page.fill('label:has-text("图形验证码") input', 'E2E1')
    await page.click('button[type="submit"]')
    await expect(page).toHaveURL(/\/admin\/?$/, { timeout: 20000 })
    await expect(page.locator('h1')).toContainText('仪表盘')
  })

  test('API 登录可用（e2e profile 固定验证码 E2E1）', async () => {
    if (await skipIfNoBackend()) {
      return
    }
    expect(await loginViaApi('admin', 'admin123')).toBe(true)
  })

  test('admin 代码片段 CRUD via API', async () => {
    if (await skipIfNoBackend()) {
      return
    }
    const session = await loginViaApiSession('admin', 'admin123')
    expect(session).not.toBeNull()
    if (!session) return

    const slug = `e2e-snippet-${Date.now()}`
    const created = await adminApiRequest<{ id: number; slug: string }>(session, 'POST', '/admin/snippets', {
      title: 'E2E Snippet',
      slug,
      language: 'javascript',
      code: 'console.log("e2e")',
      visibility: 'public'
    })
    expect(created.code).toBe(0)
    expect(created.data?.slug).toBe(slug)

    const list = await adminApiRequest<{ items: { slug: string }[] }>(session, 'GET', '/admin/snippets?page=1')
    expect(list.code).toBe(0)
    expect(list.data?.items.some((item) => item.slug === slug)).toBe(true)

    const deleted = await adminApiRequest(session, 'DELETE', `/admin/snippets/${created.data?.id}`)
    expect(deleted.code).toBe(0)
  })
})
