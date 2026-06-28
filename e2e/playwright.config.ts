import { defineConfig, devices } from '@playwright/test'

const frontendUrl = process.env.FRONTEND_URL || 'http://localhost:3000'
const adminUrl = process.env.ADMIN_URL || 'http://localhost:5173/admin'

export default defineConfig({
  testDir: './tests',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: [['list'], ['html', { open: 'never' }]],
  use: {
    trace: 'on-first-retry',
    screenshot: 'only-on-failure'
  },
  projects: [
    {
      name: 'frontend',
      use: { ...devices['Desktop Chrome'], baseURL: frontendUrl }
    },
    {
      name: 'admin',
      use: { ...devices['Desktop Chrome'], baseURL: adminUrl }
    }
  ]
})
