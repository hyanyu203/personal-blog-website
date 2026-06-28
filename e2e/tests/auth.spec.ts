import { test, expect } from '@playwright/test'

test.describe('用户认证', () => {
  test('登录页可访问', async ({ page }) => {
    await page.goto('/login')
    await expect(page.locator('h1')).toContainText('登录')
    await expect(page.getByRole('link', { name: '注册账号' })).toBeVisible()
  })

  test('注册页可访问', async ({ page }) => {
    await page.goto('/register')
    await expect(page.locator('h1')).toContainText('注册')
    await expect(page.locator('img[alt="验证码"]')).toBeVisible()
  })

  test('忘记密码页可访问', async ({ page }) => {
    await page.goto('/forgot-password')
    await expect(page.locator('h1')).toContainText('忘记密码')
  })

  test('导航栏显示登录/注册入口', async ({ page }) => {
    await page.goto('/')
    await expect(page.getByRole('link', { name: '登录' })).toBeVisible()
    await expect(page.getByRole('link', { name: '注册' })).toBeVisible()
  })

  test('未登录时文章页评论区提示登录', async ({ page }) => {
    await page.goto('/posts')
    const firstPost = page.locator('a[href^="/posts/"]').first()
    if (await firstPost.count() === 0) {
      test.skip()
      return
    }
    await firstPost.click()
    await expect(page.locator('.comments')).toBeVisible()
    await expect(page.locator('.login-hint')).toContainText('登录')
  })

  test('登录页包含图形验证码', async ({ page }) => {
    await page.goto('/login')
    await expect(page.locator('img[alt="验证码"]')).toBeVisible()
  })

  test.skip('管理员凭据可登录（需连接后端并输入验证码）', async ({ page }) => {
    await page.goto('/login')
    await page.fill('input[autocomplete="username"]', 'admin')
    await page.fill('input[autocomplete="current-password"]', 'admin123')
    await page.getByRole('button', { name: '登录' }).click()
    await page.waitForURL(url => !url.pathname.includes('/login'), { timeout: 10000 })
    await expect(page.getByText('管理员').or(page.getByText('admin'))).toBeVisible()
  })
})
