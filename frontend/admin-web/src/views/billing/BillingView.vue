<template>
  <section class="page-shell billing-page">
    <div class="page-card billing-toolbar-card">
      <div class="el-card__body">
        <div class="billing-toolbar-head">
          <div class="billing-toolbar-copy">
            <h2 class="billing-title">收费财务</h2>
            <p class="billing-desc">收费、退款、欠费和交班统一维护。</p>
          </div>
          <div class="billing-hero-actions">
            <el-tag effect="plain" round>开班中 {{ openShiftCount }}</el-tag>
            <el-button v-if="hasButtonPermission('BILLING_PAYMENT')" plain @click="openShiftDialog">开班</el-button>
            <el-button v-if="hasButtonPermission('BILLING_CREATE')" type="primary" @click="openChargeDialog">新建收费单</el-button>
          </div>
        </div>

        <div class="billing-stat-list">
          <div class="billing-stat-chip billing-stat-chip-primary">
            <span class="billing-stat-label">收费单</span>
            <strong class="billing-stat-value">{{ pagination.total }}</strong>
          </div>
          <div class="billing-stat-chip">
            <span class="billing-stat-label">应收</span>
            <strong class="billing-stat-value">{{ formatCurrency(pageReceivableAmount) }}</strong>
          </div>
          <div class="billing-stat-chip">
            <span class="billing-stat-label">实收</span>
            <strong class="billing-stat-value">{{ formatCurrency(pagePaidAmount) }}</strong>
          </div>
          <div class="billing-stat-chip">
            <span class="billing-stat-label">欠费</span>
            <strong class="billing-stat-value">{{ formatCurrency(pageArrearsAmount) }}</strong>
          </div>
        </div>

        <el-form :model="filters" label-position="top" class="billing-filter-grid">
          <el-form-item label="门诊">
            <el-select v-model="filters.clinicId" clearable placeholder="全部门诊">
              <el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="filters.orderStatus" clearable placeholder="全部状态">
              <el-option label="草稿" value="DRAFT" />
              <el-option label="部分支付" value="PART_PAID" />
              <el-option label="已结清" value="PAID" />
              <el-option label="已退款" value="REFUNDED" />
            </el-select>
          </el-form-item>
          <el-form-item label="收费日期">
            <el-date-picker v-model="chargeDateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始日期" end-placeholder="结束日期" />
          </el-form-item>
          <el-form-item label="关键词" class="billing-filter-keyword">
            <el-input v-model.trim="filters.keyword" placeholder="收费单号/患者/手机号" clearable />
          </el-form-item>
          <el-form-item class="billing-filter-actions">
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button @click="resetFilters">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="page-card tab-shell">
      <div class="el-card__body">
        <el-tabs>
          <el-tab-pane :label="`收费单 ${pagination.total}`">
            <el-table :data="chargeOrders" v-loading="loading" size="large" stripe empty-text="暂无收费单数据">
              <el-table-column prop="chargeNo" label="收费单号" min-width="130" />
              <el-table-column prop="patientName" label="患者" min-width="120" />
              <el-table-column prop="clinicName" label="门诊" min-width="120" />
              <el-table-column prop="receivableAmount" label="应收" min-width="120" />
              <el-table-column prop="paidAmount" label="实收" min-width="120" />
              <el-table-column prop="arrearsAmount" label="欠费" min-width="120" />
              <el-table-column label="状态" width="120">
                <template #default="{ row }">
                  <StatusTag :status="row.orderStatus" />
                </template>
              </el-table-column>
              <el-table-column fixed="right" label="操作" width="320">
                <template #default="{ row }">
                  <el-space wrap>
                    <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
                    <el-button v-if="hasButtonPermission('BILLING_PAYMENT')" link type="success" @click="openPaymentDialog(row.id)">登记支付</el-button>
                    <el-button v-if="hasButtonPermission('BILLING_REFUND')" link type="danger" @click="openRefundDialog(row.id)">登记退款</el-button>
                  </el-space>
                </template>
              </el-table-column>
            </el-table>
            <div style="display: flex; justify-content: flex-end; margin-top: 16px;">
              <el-pagination
                v-model:current-page="pagination.current"
                v-model:page-size="pagination.size"
                background
                layout="total, prev, pager, next"
                :total="pagination.total"
                @current-change="handleCurrentChange"
              />
            </div>
          </el-tab-pane>

          <el-tab-pane :label="`收银交班 ${shifts.length}`">
            <el-table :data="shifts" v-loading="loading" size="large" stripe empty-text="暂无交班记录">
              <el-table-column prop="shiftNo" label="班次号" min-width="130" />
              <el-table-column prop="cashierName" label="收银员" min-width="120" />
              <el-table-column prop="shiftDate" label="日期" min-width="120" />
              <el-table-column prop="paidAmount" label="收款" min-width="120" />
              <el-table-column prop="refundAmount" label="退款" min-width="120" />
              <el-table-column prop="netAmount" label="净额" min-width="120" />
              <el-table-column label="状态" width="120">
                <template #default="{ row }">
                  <StatusTag :status="row.shiftStatus" />
                </template>
              </el-table-column>
              <el-table-column fixed="right" label="操作" width="120">
                <template #default="{ row }">
                  <el-button v-if="row.shiftStatus === 'OPEN' && hasButtonPermission('BILLING_PAYMENT')" link type="primary" @click="closeShift(row.id)">关班</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <el-dialog v-model="chargeDialogVisible" title="新建收费单" width="820px">
      <el-form :model="chargeForm" label-position="top" class="form-grid">
        <el-form-item label="门诊"><el-select v-model="chargeForm.clinicId"><el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="患者">
          <EntityRemoteSelect v-model="chargeForm.patientId" :request="fetchPatients" label-key="label" value-key="id" :selected-label="selectedLabels.patient" placeholder="搜索患者" />
        </el-form-item>
        <el-form-item label="病历">
          <EntityRemoteSelect v-model="chargeForm.medicalRecordId" :request="fetchRecords" label-key="label" value-key="id" :selected-label="selectedLabels.record" placeholder="搜索病历" />
        </el-form-item>
        <el-form-item label="治疗计划">
          <EntityRemoteSelect v-model="chargeForm.treatmentPlanId" :request="fetchPlans" label-key="label" value-key="id" :selected-label="selectedLabels.plan" placeholder="搜索治疗计划" />
        </el-form-item>
        <el-form-item label="收银员">
          <EntityRemoteSelect v-model="chargeForm.cashierId" :request="fetchCashiers" label-key="label" value-key="id" :selected-label="selectedLabels.cashier" placeholder="搜索收银员" />
        </el-form-item>
        <el-form-item label="收费时间"><el-date-picker v-model="chargeForm.chargeTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%;" /></el-form-item>
        <el-form-item class="full-span" label="项目名称"><el-input v-model.trim="chargeForm.itemName" /></el-form-item>
        <el-form-item label="项目编码"><el-input v-model.trim="chargeForm.itemCode" /></el-form-item>
        <el-form-item label="分类"><el-input v-model.trim="chargeForm.itemCategory" /></el-form-item>
        <el-form-item label="单价"><el-input-number v-model="chargeForm.unitPrice" :min="0" style="width: 100%;" /></el-form-item>
        <el-form-item label="数量"><el-input-number v-model="chargeForm.quantity" :min="1" style="width: 100%;" /></el-form-item>
        <el-form-item label="优惠"><el-input-number v-model="chargeForm.discountAmount" :min="0" style="width: 100%;" /></el-form-item>
        <el-form-item label="医生">
          <EntityRemoteSelect v-model="chargeForm.doctorId" :request="fetchDoctors" label-key="label" value-key="id" placeholder="搜索医生" />
        </el-form-item>
        <el-form-item class="full-span" label="备注"><el-input v-model.trim="chargeForm.remark" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="chargeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createCharge">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="paymentDialogVisible" title="登记支付" width="620px">
      <el-form :model="paymentForm" label-position="top">
        <el-form-item label="支付金额"><el-input-number v-model="paymentForm.amount" :min="0.01" :max="paymentMaxAmount" style="width: 100%;" /></el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="paymentForm.paymentMethod">
            <el-option label="现金" value="CASH" />
            <el-option label="微信" value="WECHAT" />
            <el-option label="支付宝" value="ALIPAY" />
            <el-option label="银行卡" value="CARD" />
          </el-select>
        </el-form-item>
        <el-form-item label="收银员">
          <EntityRemoteSelect v-model="paymentForm.cashierId" :request="fetchCashiers" label-key="label" value-key="id" :selected-label="selectedLabels.cashier" placeholder="搜索收银员" />
        </el-form-item>
        <el-form-item label="付款人"><el-input v-model.trim="paymentForm.payerName" /></el-form-item>
        <el-form-item label="流水号"><el-input v-model.trim="paymentForm.transactionNo" /></el-form-item>
        <el-form-item label="备注"><el-input v-model.trim="paymentForm.remark" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="paymentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPayment">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="refundDialogVisible" title="登记退款" width="620px">
      <el-form :model="refundForm" label-position="top">
        <el-form-item label="关联支付记录">
          <el-select v-model="refundForm.paymentRecordId" clearable>
            <el-option v-for="item in detail?.payments || []" :key="item.id" :label="`${item.paymentNo} / ${item.amount}`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="退款金额"><el-input-number v-model="refundForm.refundAmount" :min="0.01" :max="refundMaxAmount" style="width: 100%;" /></el-form-item>
        <el-form-item label="退款方式">
          <el-select v-model="refundForm.refundMethod">
            <el-option label="原路返回" value="ORIGINAL" />
            <el-option label="现金" value="CASH" />
            <el-option label="微信" value="WECHAT" />
            <el-option label="支付宝" value="ALIPAY" />
          </el-select>
        </el-form-item>
        <el-form-item label="退款原因"><el-input v-model.trim="refundForm.refundReason" /></el-form-item>
        <el-form-item label="备注"><el-input v-model.trim="refundForm.remark" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRefund">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="shiftDialogVisible" title="开班" width="680px">
      <el-form :model="shiftForm" label-position="top" class="form-grid">
        <el-form-item label="门诊"><el-select v-model="shiftForm.clinicId"><el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="收银员">
          <EntityRemoteSelect v-model="shiftForm.cashierId" :request="fetchCashiers" label-key="label" value-key="id" :selected-label="selectedLabels.cashier" placeholder="搜索收银员" />
        </el-form-item>
        <el-form-item label="班次日期"><el-date-picker v-model="shiftForm.shiftDate" type="date" value-format="YYYY-MM-DD" style="width: 100%;" /></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="shiftForm.startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%;" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shiftDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="openShift">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" title="收费详情" size="760px">
      <template v-if="detail">
        <div class="summary-grid" style="grid-template-columns: repeat(3, minmax(0, 1fr));">
          <div class="summary-card">
            <div class="summary-label">收费单号</div>
            <div class="summary-value" style="font-size: 22px;">{{ detail.chargeNo }}</div>
            <div class="summary-meta">{{ detail.patientName }} / {{ detail.patientMobile || '-' }}</div>
          </div>
          <div class="summary-card">
            <div class="summary-label">应收 / 实收</div>
            <div class="summary-value" style="font-size: 22px;">¥{{ detail.receivableAmount || 0 }}</div>
            <div class="summary-meta">实收 ¥{{ detail.paidAmount || 0 }} / 欠费 ¥{{ detail.arrearsAmount || 0 }}</div>
          </div>
          <div class="summary-card">
            <div class="summary-label">状态</div>
            <div class="summary-value" style="font-size: 22px;">{{ detail.orderStatus }}</div>
            <div class="summary-meta">收费时间 {{ detail.chargeTime || '-' }}</div>
          </div>
        </div>

        <div class="page-card" style="box-shadow: none; margin-top: 20px;">
          <div class="el-card__body">
            <div class="page-header">
              <div>
                <h3 class="page-title" style="font-size: 18px;">收费明细</h3>
              </div>
            </div>
            <el-table :data="detail.items || []" size="large" style="margin-top: 12px;">
              <el-table-column prop="itemName" label="项目" min-width="160" />
              <el-table-column prop="unitPrice" label="单价" min-width="100" />
              <el-table-column prop="quantity" label="数量" min-width="80" />
              <el-table-column prop="discountAmount" label="优惠" min-width="100" />
              <el-table-column prop="receivableAmount" label="应收" min-width="100" />
            </el-table>
          </div>
        </div>

        <div class="split-grid" style="margin-top: 20px;">
          <div class="page-card" style="box-shadow: none;">
            <div class="el-card__body">
              <div class="page-header">
                <div>
                  <h3 class="page-title" style="font-size: 18px;">支付记录</h3>
                </div>
              </div>
              <el-table :data="detail.payments || []" size="large" style="margin-top: 12px;">
                <el-table-column prop="paymentNo" label="支付单号" min-width="140" />
                <el-table-column prop="paymentMethod" label="方式" min-width="100" />
                <el-table-column prop="amount" label="金额" min-width="100" />
                <el-table-column prop="paidAt" label="时间" min-width="160" />
              </el-table>
            </div>
          </div>
          <div class="page-card" style="box-shadow: none;">
            <div class="el-card__body">
              <div class="page-header">
                <div>
                  <h3 class="page-title" style="font-size: 18px;">退款记录</h3>
                </div>
              </div>
              <el-table :data="detail.refunds || []" size="large" style="margin-top: 12px;">
                <el-table-column prop="refundNo" label="退款单号" min-width="140" />
                <el-table-column prop="refundMethod" label="方式" min-width="100" />
                <el-table-column prop="refundAmount" label="金额" min-width="100" />
                <el-table-column prop="refundedAt" label="时间" min-width="160" />
              </el-table>
            </div>
          </div>
        </div>
      </template>
    </el-drawer>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'
import { api } from '@/api/service'
import EntityRemoteSelect from '@/components/EntityRemoteSelect.vue'
import StatusTag from '@/components/StatusTag.vue'
import { usePermission } from '@/composables/usePermission'
import { useRouteQueryState } from '@/composables/useRouteQueryState'

const loading = ref(false)
const { hasButtonPermission } = usePermission()
const chargeOrders = ref<any[]>([])
const shifts = ref<any[]>([])
const clinics = ref<any[]>([])
const detail = ref<any>(null)
const detailVisible = ref(false)
const currentChargeId = ref<number | null>(null)
const chargeDialogVisible = ref(false)
const shiftDialogVisible = ref(false)
const paymentDialogVisible = ref(false)
const refundDialogVisible = ref(false)
const chargeDateRange = ref<[string, string] | []>([])

const selectedLabels = reactive<Record<string, string>>({
  patient: '',
  record: '',
  plan: '',
  cashier: '',
})

const filters = reactive({
  clinicId: undefined as number | undefined,
  orderStatus: '',
  keyword: '',
  chargeDateFrom: '',
  chargeDateTo: '',
  current: 1,
})
useRouteQueryState(filters, { current: 1 })
const billingFilterState = filters as Record<string, any>
if (billingFilterState.clinicId !== undefined && billingFilterState.clinicId !== '') {
  billingFilterState.clinicId = Number(billingFilterState.clinicId)
}
const pagination = reactive({ current: 1, size: 10, total: 0 })

const chargeForm = reactive<Record<string, any>>({
  clinicId: undefined,
  patientId: undefined,
  medicalRecordId: undefined,
  treatmentPlanId: undefined,
  cashierId: undefined,
  chargeTime: dayjs().format('YYYY-MM-DDTHH:mm:ss'),
  itemCode: '',
  itemName: '',
  itemCategory: '',
  unitPrice: 0,
  quantity: 1,
  discountAmount: 0,
  doctorId: undefined,
  remark: '',
})

const paymentForm = reactive<Record<string, any>>({
  paymentMethod: 'CASH',
  amount: 0,
  transactionNo: '',
  payerName: '',
  cashierId: undefined,
  remark: '',
})

const refundForm = reactive<Record<string, any>>({
  paymentRecordId: undefined,
  refundAmount: 0,
  refundMethod: 'ORIGINAL',
  refundReason: '',
  remark: '',
})

const shiftForm = reactive<Record<string, any>>({
  clinicId: undefined,
  cashierId: undefined,
  shiftDate: dayjs().format('YYYY-MM-DD'),
  startTime: dayjs().format('YYYY-MM-DDTHH:mm:ss'),
})

const paymentMaxAmount = computed(() => Number(detail.value?.arrearsAmount || 0))
const refundMaxAmount = computed(() => Number(detail.value?.paidAmount || 0))
const pageReceivableAmount = computed(() => chargeOrders.value.reduce((total, item) => total + Number(item.receivableAmount || 0), 0))
const pagePaidAmount = computed(() => chargeOrders.value.reduce((total, item) => total + Number(item.paidAmount || 0), 0))
const pageArrearsAmount = computed(() => chargeOrders.value.reduce((total, item) => total + Number(item.arrearsAmount || 0), 0))
const openShiftCount = computed(() => shifts.value.filter((item) => item.shiftStatus === 'OPEN').length)

function formatCurrency(value: number | string) {
  return `¥${Number(value || 0).toFixed(2)}`
}

async function loadData() {
  loading.value = true
  filters.chargeDateFrom = chargeDateRange.value?.[0] || ''
  filters.chargeDateTo = chargeDateRange.value?.[1] || ''
  try {
    const [clinicRes, chargeRes, shiftRes] = await Promise.all([
      api.org.clinics(),
      api.billing.chargeOrders({
        clinicId: filters.clinicId,
        orderStatus: filters.orderStatus || undefined,
        keyword: filters.keyword || undefined,
        chargeDateFrom: filters.chargeDateFrom || undefined,
        chargeDateTo: filters.chargeDateTo || undefined,
        current: Number(filters.current || pagination.current),
        size: pagination.size,
      }),
      api.billing.shifts({ current: 1, size: 20 }),
    ])
    clinics.value = clinicRes.data || []
    chargeOrders.value = chargeRes.data.records || []
    shifts.value = shiftRes.data.records || []
    pagination.total = chargeRes.data.total || 0
    pagination.current = Number(filters.current || 1)
    chargeForm.clinicId ||= clinics.value[0]?.id
    shiftForm.clinicId ||= clinics.value[0]?.id
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  filters.clinicId = undefined
  filters.orderStatus = ''
  filters.keyword = ''
  filters.chargeDateFrom = ''
  filters.chargeDateTo = ''
  filters.current = 1
  chargeDateRange.value = []
  loadData()
}

function handleCurrentChange(value: number) {
  filters.current = value
  loadData()
}

function openChargeDialog() {
  Object.assign(chargeForm, {
    clinicId: clinics.value[0]?.id,
    patientId: undefined,
    medicalRecordId: undefined,
    treatmentPlanId: undefined,
    cashierId: undefined,
    chargeTime: dayjs().format('YYYY-MM-DDTHH:mm:ss'),
    itemCode: '',
    itemName: '',
    itemCategory: '',
    unitPrice: 0,
    quantity: 1,
    discountAmount: 0,
    doctorId: undefined,
    remark: '',
  })
  chargeDialogVisible.value = true
}

async function createCharge() {
  if (!chargeForm.clinicId) {
    ElMessage.warning('请选择门诊')
    return
  }
  if (!chargeForm.patientId) {
    ElMessage.warning('请选择患者')
    return
  }
  if (!chargeForm.itemCode || !chargeForm.itemName) {
    ElMessage.warning('请填写项目编码和项目名称')
    return
  }
  if (Number(chargeForm.unitPrice || 0) < 0 || Number(chargeForm.quantity || 0) < 1) {
    ElMessage.warning('请填写正确的单价和数量')
    return
  }
  if (Number(chargeForm.discountAmount || 0) < 0) {
    ElMessage.warning('优惠金额不能小于 0')
    return
  }

  await api.billing.createCharge({
    clinicId: chargeForm.clinicId,
    patientId: chargeForm.patientId,
    medicalRecordId: chargeForm.medicalRecordId,
    treatmentPlanId: chargeForm.treatmentPlanId,
    cashierId: chargeForm.cashierId,
    chargeTime: chargeForm.chargeTime,
    remark: chargeForm.remark,
    items: [
      {
        itemCode: chargeForm.itemCode,
        itemName: chargeForm.itemName,
        itemCategory: chargeForm.itemCategory,
        unitPrice: chargeForm.unitPrice,
        quantity: chargeForm.quantity,
        discountAmount: chargeForm.discountAmount,
        doctorId: chargeForm.doctorId,
      },
    ],
  })
  ElMessage.success('收费单创建成功')
  chargeDialogVisible.value = false
  await loadData()
}

async function openDetail(id: number) {
  const response = await api.billing.chargeDetail(id)
  detail.value = response.data
  detailVisible.value = true
}

async function openPaymentDialog(id: number) {
  currentChargeId.value = id
  await openDetail(id)
  Object.assign(paymentForm, {
    paymentMethod: 'CASH',
    amount: Number(detail.value?.arrearsAmount || 0),
    transactionNo: '',
    payerName: detail.value?.patientName || '',
    cashierId: detail.value?.cashierId,
    remark: '',
  })
  paymentDialogVisible.value = true
}

async function submitPayment() {
  if (!currentChargeId.value) {
    return
  }
  if (!paymentForm.paymentMethod) {
    ElMessage.warning('请选择支付方式')
    return
  }
  if (Number(paymentForm.amount || 0) <= 0) {
    ElMessage.warning('支付金额必须大于 0')
    return
  }
  if (Number(paymentForm.amount || 0) > paymentMaxAmount.value) {
    ElMessage.warning('支付金额不能大于欠费金额')
    return
  }
  await api.billing.pay(currentChargeId.value, paymentForm)
  ElMessage.success('支付登记成功')
  paymentDialogVisible.value = false
  await loadData()
  await openDetail(currentChargeId.value)
}

async function openRefundDialog(id: number) {
  currentChargeId.value = id
  await openDetail(id)
  Object.assign(refundForm, {
    paymentRecordId: detail.value?.payments?.[0]?.id,
    refundAmount: Number(detail.value?.paidAmount || 0),
    refundMethod: 'ORIGINAL',
    refundReason: '',
    remark: '',
  })
  refundDialogVisible.value = true
}

async function submitRefund() {
  if (!currentChargeId.value) {
    return
  }
  if (!refundForm.refundMethod) {
    ElMessage.warning('请选择退款方式')
    return
  }
  if (!refundForm.refundReason) {
    ElMessage.warning('请填写退款原因')
    return
  }
  if (Number(refundForm.refundAmount || 0) <= 0) {
    ElMessage.warning('退款金额必须大于 0')
    return
  }
  if (Number(refundForm.refundAmount || 0) > refundMaxAmount.value) {
    ElMessage.warning('退款金额不能大于已支付金额')
    return
  }
  await api.billing.refund(currentChargeId.value, refundForm)
  ElMessage.success('退款登记成功')
  refundDialogVisible.value = false
  await loadData()
  await openDetail(currentChargeId.value)
}

function openShiftDialog() {
  Object.assign(shiftForm, {
    clinicId: clinics.value[0]?.id,
    cashierId: undefined,
    shiftDate: dayjs().format('YYYY-MM-DD'),
    startTime: dayjs().format('YYYY-MM-DDTHH:mm:ss'),
  })
  shiftDialogVisible.value = true
}

async function openShift() {
  if (!shiftForm.clinicId || !shiftForm.cashierId) {
    ElMessage.warning('请完善门诊和收银员')
    return
  }
  await api.billing.openShift(shiftForm)
  ElMessage.success('开班成功')
  shiftDialogVisible.value = false
  await loadData()
}

async function closeShift(id: number) {
  await api.billing.closeShift(id, { endTime: dayjs().format('YYYY-MM-DDTHH:mm:ss'), remark: 'PC 后台关班' })
  ElMessage.success('关班成功')
  await loadData()
}

async function fetchPatients(keyword: string) {
  const response = await api.patient.page({ keyword, current: 1, size: 20 })
  return (response.data.records || []).map((item: any) => ({
    id: item.id,
    label: `${item.patientName} / ${item.mobile || '-'}`,
  }))
}

async function fetchRecords(keyword: string) {
  const response = await api.emr.records({ keyword, current: 1, size: 20 })
  return (response.data.records || []).map((item: any) => ({
    id: item.id,
    label: `${item.recordNo} / ${item.patientName}`,
  }))
}

async function fetchPlans(keyword: string) {
  const response = await api.emr.treatmentPlans({ keyword, current: 1, size: 20 })
  return (response.data.records || []).map((item: any) => ({
    id: item.id,
    label: `${item.planNo} / ${item.planName}`,
  }))
}

async function fetchCashiers(keyword: string) {
  const response = await api.org.staff({ keyword, staffType: 'FRONT_DESK' })
  return (response.data || []).map((item: any) => ({
    id: item.id,
    label: `${item.staffName} / ${item.mobile || '-'}`,
  }))
}

async function fetchDoctors(keyword: string) {
  const response = await api.patient.doctorOptions()
  return (response.data || [])
    .filter((item: any) => !keyword || item.doctorName?.includes(keyword))
    .map((item: any) => ({
      id: item.doctorId,
      label: `${item.doctorName} / ${item.clinicName || '-'}`,
    }))
}

onMounted(loadData)
</script>

<style scoped>
.billing-page {
  gap: 14px;
}

.billing-toolbar-card {
  border: 1px solid rgba(8, 145, 178, 0.1);
}

.billing-toolbar-card .el-card__body {
  padding: 18px 20px 16px;
}

.billing-toolbar-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
}

.billing-toolbar-copy {
  min-width: 0;
}

.billing-title {
  margin: 0;
  color: #0f3f52;
  font-size: 24px;
  line-height: 1.2;
}

.billing-desc {
  max-width: 520px;
  margin: 6px 0 0;
  color: #5b7086;
  font-size: 13px;
  line-height: 1.5;
}

.billing-hero-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.billing-stat-list {
  display: grid;
  grid-template-columns: repeat(4, minmax(110px, 1fr));
  gap: 10px;
  margin-top: 14px;
}

.billing-stat-chip {
  display: grid;
  gap: 3px;
  min-width: 0;
  padding: 10px 14px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 12px;
  background: #f8fbfc;
}

.billing-stat-chip-primary {
  background: linear-gradient(135deg, rgba(8, 145, 178, 0.12) 0%, rgba(34, 211, 238, 0.06) 100%);
  border-color: rgba(8, 145, 178, 0.18);
}

.billing-stat-label {
  color: #64748b;
  font-size: 12px;
}

.billing-stat-value {
  color: #0f3f52;
  font-size: 18px;
  font-weight: 700;
  line-height: 1.2;
}

.billing-filter-grid {
  display: grid;
  grid-template-columns: 180px 180px minmax(280px, 360px) minmax(220px, 1fr) auto;
  gap: 12px 16px;
  align-items: end;
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid rgba(226, 232, 240, 0.9);
}

:deep(.billing-filter-grid .el-form-item) {
  margin: 0;
}

:deep(.billing-filter-grid .el-form-item__label) {
  padding: 0 0 6px;
  color: #60758a;
  font-size: 12px;
  font-weight: 600;
  line-height: 1.2;
}

.billing-filter-keyword {
  min-width: 0;
}

.billing-filter-actions {
  justify-self: end;
  display: flex;
  align-items: center;
  gap: 10px;
}

:deep(.billing-filter-grid .el-input__wrapper),
:deep(.billing-filter-grid .el-select__wrapper),
:deep(.billing-filter-grid .el-textarea__inner),
:deep(.billing-filter-grid .el-range-editor.el-input__wrapper) {
  min-height: 44px;
  border-radius: 14px;
  box-shadow: none;
}

:deep(.billing-page .el-tabs__header) {
  margin-bottom: 12px;
}

:deep(.billing-page .el-table) {
  --el-table-header-bg-color: #f4fafb;
  --el-table-row-hover-bg-color: rgba(8, 145, 178, 0.05);
}

@media (max-width: 1440px) {
  .billing-toolbar-head {
    flex-direction: column;
  }

  .billing-stat-list {
    width: 100%;
  }

  .billing-filter-grid {
    grid-template-columns: repeat(2, minmax(220px, 1fr));
  }

  .billing-filter-actions {
    justify-self: start;
  }
}
</style>
