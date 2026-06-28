import { test, expect } from '@playwright/test'

test.describe('前台', () => {
  test('首页加载并显示站点标题', async ({ page }) => {
    await page.goto('/')
    await expect(page.locator('h1')).toContainText('渐构')
  })

  test('文章列表页可访问', async ({ page }) => {
    await page.goto('/posts')
    await expect(page.locator('h1')).toContainText('文章')
  })

  test('搜索页可访问', async ({ page }) => {
    await page.goto('/search')
    await expect(page.locator('h1')).toContainText('搜索')
  })

  test('隐私政策页可访问', async ({ page }) => {
    await page.goto('/privacy')
    await expect(page.locator('h1')).toContainText('隐私政策')
  })

  test('页脚包含 RSS 链接', async ({ page }) => {
    await page.goto('/')
    const rss = page.locator('footer a[href*="rss"]')
    await expect(rss).toBeVisible()
  })
})
