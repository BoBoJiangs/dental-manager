import { expect, test, type Locator, type Page } from '@playwright/test'

const username = process.env.E2E_USERNAME || 'admin'
const password = process.env.E2E_PASSWORD || 'Admin@123456'

async function login(page: Page) {
  await page.goto('/login')
  await page.getByPlaceholder('请输入用户名').fill(username)
  await page.getByPlaceholder('请输入密码').fill(password)
  await page.getByRole('button', { name: /登录/ }).click()
  await expect(page).toHaveURL(/\/dashboard$/)
}

function getFormItemByLabel(page: Page, container: Locator, label: string) {
  return container
    .locator('.el-form-item')
    .filter({ has: page.locator('.el-form-item__label', { hasText: label }) })
    .first()
}

async function fillInputByLabel(page: Page, container: Locator, label: string, value: string) {
  const item = getFormItemByLabel(page, container, label)
  const input = item.locator('input, textarea').first()
  await input.click()
  await input.press('Control+A')
  await input.fill(value)
}

async function selectFirstOptionByLabel(page: Page, container: Locator, label: string) {
  const item = getFormItemByLabel(page, container, label)
  await item.locator('.el-select__wrapper').first().click()
  const combobox = item.getByRole('combobox').first()
  await combobox.press('ArrowDown')
  await combobox.press('Enter')
}

async function selectRemoteOptionByKeyword(page: Page, container: Locator, label: string, keyword: string) {
  const item = getFormItemByLabel(page, container, label)
  await item.locator('.el-select__wrapper').first().click()
  const input = item.getByRole('combobox').first()
  await input.focus()
  await input.fill(keyword)
  await page.waitForTimeout(300)
  await input.press('ArrowDown')
  await input.press('Enter')
}

test('create flow: patient -> appointment -> charge order', async ({ page }) => {
  test.setTimeout(120_000)

  const suffix = `${Date.now()}`
  const now = new Date()
  const yyyy = now.getFullYear()
  const mm = `${now.getMonth() + 1}`.padStart(2, '0')
  const dd = `${now.getDate()}`.padStart(2, '0')
  const appointmentDate = `${yyyy}-${mm}-${dd}`
  const patientName = `E2E患者${suffix.slice(-6)}`
  const patientMobile = `1${suffix.slice(-10)}`
  const treatmentItemName = `E2E项目${suffix.slice(-6)}`
  const chargeItemCode = `E2E${suffix.slice(-6)}`

  await login(page)

  await page.goto('/patients')
  await expect(page.getByRole('heading', { name: '患者中心' })).toBeVisible()

  await page.getByRole('button', { name: '新建患者' }).click()
  const patientDialog = page.locator('.el-dialog').filter({ hasText: '新建患者' }).last()
  await expect(patientDialog).toBeVisible()
  await fillInputByLabel(page, patientDialog, '患者姓名', patientName)
  await fillInputByLabel(page, patientDialog, '手机号', patientMobile)
  await patientDialog.getByRole('button', { name: '保存' }).click()

  await expect(patientDialog).toBeHidden({ timeout: 15_000 })
  await expect(page.locator('.el-table').first()).toContainText(patientName)

  await page.goto('/appointments')
  await expect(page.getByRole('heading', { name: '预约中心' })).toBeVisible()

  await page.getByRole('button', { name: '新建预约' }).click()
  const appointmentDialog = page.locator('.el-dialog').filter({ hasText: '新建预约' }).last()
  await expect(appointmentDialog).toBeVisible()
  await selectFirstOptionByLabel(page, appointmentDialog, '门诊')
  await selectRemoteOptionByKeyword(page, appointmentDialog, '患者', patientName)
  await selectFirstOptionByLabel(page, appointmentDialog, '医生')
  await selectFirstOptionByLabel(page, appointmentDialog, '预约来源')
  await selectFirstOptionByLabel(page, appointmentDialog, '预约类型')
  await fillInputByLabel(page, appointmentDialog, '预约日期', appointmentDate)
  await fillInputByLabel(page, appointmentDialog, '开始时间', `${appointmentDate} 09:30:00`)
  await fillInputByLabel(page, appointmentDialog, '结束时间', `${appointmentDate} 10:00:00`)
  await fillInputByLabel(page, appointmentDialog, '治疗项目', treatmentItemName)
  await appointmentDialog.getByRole('button', { name: '保存' }).click()

  await expect(appointmentDialog).toBeHidden({ timeout: 15_000 })
  await expect(page.locator('.el-table').first()).toContainText(patientName)

  await page.goto('/billing')
  await expect(page.getByRole('heading', { name: '收费财务' })).toBeVisible()

  await page.getByRole('button', { name: '新建收费单' }).click()
  const chargeDialog = page.locator('.el-dialog').filter({ hasText: '新建收费单' }).last()
  await expect(chargeDialog).toBeVisible()
  await selectFirstOptionByLabel(page, chargeDialog, '门诊')
  await selectRemoteOptionByKeyword(page, chargeDialog, '患者', patientName)
  await fillInputByLabel(page, chargeDialog, '项目名称', treatmentItemName)
  await fillInputByLabel(page, chargeDialog, '项目编码', chargeItemCode)
  await fillInputByLabel(page, chargeDialog, '单价', '200')
  await fillInputByLabel(page, chargeDialog, '数量', '1')
  await chargeDialog.getByRole('button', { name: '保存' }).click()

  await expect(chargeDialog).toBeHidden({ timeout: 15_000 })
  await fillInputByLabel(page, page.locator('.billing-filter-grid').first(), '关键词', patientName)
  await page.getByRole('button', { name: '查询' }).click()
  await expect(page.locator('.el-table').first()).toContainText(patientName)
})
