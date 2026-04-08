import { expect, test } from '@playwright/test'

const username = process.env.E2E_USERNAME || 'admin'
const password = process.env.E2E_PASSWORD || 'Admin@123456'

async function login(page: import('@playwright/test').Page) {
  await page.goto('/login')
  await page.getByPlaceholder('请输入用户名').fill(username)
  await page.getByPlaceholder('请输入密码').fill(password)
  await page.getByRole('button', { name: /登录/ }).click()
  await expect(page).toHaveURL(/\/dashboard$/)
}

test('login and show dashboard', async ({ page }) => {
  await login(page)
  await expect(page.getByRole('heading', { name: '今日预约' })).toBeVisible()
  await expect(page.getByRole('heading', { name: '医生业绩' })).toBeVisible()
})

test('core pages open without runtime errors', async ({ page }) => {
  const pageErrors: string[] = []
  page.on('pageerror', (error) => pageErrors.push(error.message))

  await login(page)

  await page.goto('/patients')
  await expect(page.getByRole('heading', { name: '患者中心' })).toBeVisible()

  await page.goto('/appointments')
  await expect(page.getByRole('heading', { name: '预约中心' })).toBeVisible()
  await page.getByText('日历视图').click()
  await expect(page.locator('.el-calendar__title')).toBeVisible()

  await page.goto('/billing')
  await expect(page.getByRole('heading', { name: '收费财务' })).toBeVisible()

  await page.goto('/members')
  await expect(page.getByRole('heading', { name: '会员中心' })).toBeVisible()

  await page.goto('/sms')
  await expect(page.getByRole('heading', { name: '短信中心' })).toBeVisible()

  await page.goto('/settings')
  await expect(page.getByRole('heading', { name: '基础设置' })).toBeVisible()
  await page.getByRole('tab', { name: '角色' }).click()
  await expect(page.getByRole('heading', { name: '权限矩阵' })).toBeVisible()
  await page.getByRole('button', { name: '新增角色' }).click()
  await expect(page.getByRole('heading', { name: '新增角色' })).toBeVisible()
  await page.getByRole('button', { name: '取消' }).click()

  await page.goto('/reports')
  await expect(page.getByRole('heading', { name: '统计分析' })).toBeVisible()
  await expect(pageErrors).toEqual([])
})

test('emr dialogs render key editable controls', async ({ page }) => {
  await login(page)

  await page.goto('/emr')
  await expect(page.getByRole('heading', { name: '门诊接诊' })).toBeVisible()

  await page.getByRole('button', { name: '新建病历' }).click()
  await expect(page.getByText('诊断记录')).toBeVisible()
  await expect(page.getByRole('button', { name: '新增诊断' })).toBeVisible()
  await page.getByRole('button', { name: '取消' }).click()

  await page.getByRole('button', { name: '新建治疗计划' }).click()
  await expect(page.getByText('治疗项目')).toBeVisible()
  await expect(page.getByRole('button', { name: '新增项目' })).toBeVisible()
  await page.getByRole('button', { name: '取消' }).click()

  await page.getByRole('tab', { name: '同意书与签名' }).click()
  await expect(page.getByRole('button', { name: '新增同意书' })).toBeVisible()
  await expect(page.getByRole('button', { name: '新增签名' })).toBeVisible()
  await expect(page.getByRole('heading', { name: '知情同意书' })).toBeVisible()
  await expect(page.getByRole('heading', { name: '电子签名' })).toBeVisible()

  await page.getByRole('tab', { name: '模板与牙位图' }).click()
  await expect(page.getByRole('button', { name: '保存牙位图' })).toBeVisible()
  await expect(page.getByRole('button', { name: '新增牙位' })).toBeVisible()
})
