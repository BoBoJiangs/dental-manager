<template>
  <section class="page-shell">
    <div class="page-card">
      <div class="el-card__body">
        <PageHeader title="统计分析" description="聚焦门诊经营概览、医生业绩和日报导出。">
          <el-date-picker v-model="filters.statDate" type="date" value-format="YYYY-MM-DD" />
          <el-select v-model="filters.clinicId" clearable style="width: 180px;">
            <el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" />
          </el-select>
          <el-button type="primary" @click="loadData">刷新</el-button>
          <el-button plain @click="exportDoctorPerformance">导出 CSV</el-button>
        </PageHeader>
      </div>
    </div>

    <div class="summary-grid" style="grid-template-columns: repeat(6, minmax(0, 1fr));">
      <StatCard label="预约量" :value="overview.appointmentCount || 0" meta="当日预约" />
      <StatCard label="到诊量" :value="overview.arrivedCount || 0" meta="实时到诊" />
      <StatCard label="初诊量" :value="overview.firstVisitCount || 0" meta="首诊转化" />
      <StatCard label="复诊量" :value="overview.revisitCount || 0" meta="复诊跟进" />
      <StatCard label="应收" :value="`¥${overview.receivableAmount || 0}`" meta="收费日报" />
      <StatCard label="实收" :value="`¥${overview.paidAmount || 0}`" meta="退款已单独核算" />
    </div>

    <div class="split-grid">
      <div class="page-card">
        <div class="el-card__body">
          <div class="page-header">
            <div>
              <h3 class="page-title" style="font-size: 20px;">经营概览</h3>
              <p class="page-desc">用横向条图快速看预约、到诊、收费和欠费分布。</p>
            </div>
          </div>
          <div class="list" style="margin-top: 16px;">
            <div v-for="item in overviewBars" :key="item.label" class="list-item" style="align-items: center;">
              <div class="list-main" style="min-width: 120px; max-width: 120px;">
                <div class="list-title">{{ item.label }}</div>
                <div class="list-desc">{{ item.valueText }}</div>
              </div>
              <div style="flex: 1; display: grid; gap: 6px;">
                <div style="height: 10px; border-radius: 999px; background: rgba(148, 163, 184, 0.16); overflow: hidden;">
                  <div :style="{ width: `${item.percent}%`, height: '100%', borderRadius: '999px', background: item.color }"></div>
                </div>
              </div>
              <div class="list-desc" style="min-width: 54px; text-align: right;">{{ item.percent }}%</div>
            </div>
          </div>
          <el-divider />
          <el-descriptions :column="2" border>
            <el-descriptions-item label="退款金额">¥{{ overview.refundAmount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="欠费金额">¥{{ overview.arrearsAmount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="到诊率">{{ arrivalRate }}%</el-descriptions-item>
            <el-descriptions-item label="实收率">{{ collectionRate }}%</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>

      <div class="page-card">
        <div class="el-card__body">
          <div class="page-header">
            <div>
              <h3 class="page-title" style="font-size: 20px;">医生业绩排行</h3>
              <p class="page-desc">按业绩金额降序展示，便于快速做经营复盘。</p>
            </div>
          </div>
          <div class="list" style="margin-top: 16px;">
            <div v-for="item in rankedDoctors" :key="item.doctorName" class="list-item" style="align-items: center;">
              <div class="list-main" style="min-width: 96px; max-width: 96px;">
                <div class="list-title">{{ item.doctorName }}</div>
                <div class="list-desc">预约 {{ item.appointmentCount }} · 到诊 {{ item.arrivedCount }}</div>
              </div>
              <div style="flex: 1; display: grid; gap: 6px;">
                <div style="height: 10px; border-radius: 999px; background: rgba(148, 163, 184, 0.16); overflow: hidden;">
                  <div :style="{ width: `${item.percent}%`, height: '100%', borderRadius: '999px', background: 'linear-gradient(90deg, #0891b2, #22d3ee)' }"></div>
                </div>
              </div>
              <div class="list-desc" style="min-width: 90px; text-align: right;">¥{{ item.performanceAmount || 0 }}</div>
            </div>
            <div v-if="rankedDoctors.length === 0" class="empty-block">
              <div>当前筛选条件下暂无医生业绩数据</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="page-card">
      <div class="el-card__body">
        <div class="page-header">
          <div>
            <h3 class="page-title" style="font-size: 20px;">医生业绩明细</h3>
            <p class="page-desc">保留表格明细，方便导出和核对。</p>
          </div>
        </div>
        <el-table :data="doctorPerformance" v-loading="loading" style="margin-top: 16px;" size="large">
          <el-table-column prop="doctorName" label="医生" min-width="120" />
          <el-table-column prop="appointmentCount" label="预约量" min-width="100" />
          <el-table-column prop="arrivedCount" label="到诊量" min-width="100" />
          <el-table-column prop="firstVisitCount" label="初诊量" min-width="100" />
          <el-table-column prop="revisitCount" label="复诊量" min-width="100" />
          <el-table-column prop="treatmentCount" label="接诊量" min-width="100" />
          <el-table-column prop="performanceAmount" label="业绩金额" min-width="140" />
        </el-table>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { api } from '@/api/service'
import PageHeader from '@/components/PageHeader.vue'
import StatCard from '@/components/StatCard.vue'
import { useRouteQueryState } from '@/composables/useRouteQueryState'

const loading = ref(false)
const clinics = ref<any[]>([])
const doctorPerformance = ref<any[]>([])
const overview = reactive<Record<string, any>>({})
const filters = reactive({
  statDate: dayjs().format('YYYY-MM-DD'),
  clinicId: undefined as number | undefined,
})
useRouteQueryState(filters)
const reportFilterState = filters as Record<string, any>
if (reportFilterState.clinicId !== undefined && reportFilterState.clinicId !== '') {
  reportFilterState.clinicId = Number(reportFilterState.clinicId)
}

const arrivalRate = computed(() => {
  const appointmentCount = Number(overview.appointmentCount || 0)
  const arrivedCount = Number(overview.arrivedCount || 0)
  if (!appointmentCount) {
    return 0
  }
  return Math.round((arrivedCount / appointmentCount) * 100)
})

const collectionRate = computed(() => {
  const receivableAmount = Number(overview.receivableAmount || 0)
  const paidAmount = Number(overview.paidAmount || 0)
  if (!receivableAmount) {
    return 0
  }
  return Math.round((paidAmount / receivableAmount) * 100)
})

const overviewBars = computed(() => {
  const items = [
    { label: '预约量', value: Number(overview.appointmentCount || 0), valueText: String(overview.appointmentCount || 0), color: '#0891b2' },
    { label: '到诊量', value: Number(overview.arrivedCount || 0), valueText: String(overview.arrivedCount || 0), color: '#22c55e' },
    { label: '应收', value: Number(overview.receivableAmount || 0), valueText: `¥${overview.receivableAmount || 0}`, color: '#f59e0b' },
    { label: '实收', value: Number(overview.paidAmount || 0), valueText: `¥${overview.paidAmount || 0}`, color: '#059669' },
    { label: '退款', value: Number(overview.refundAmount || 0), valueText: `¥${overview.refundAmount || 0}`, color: '#ef4444' },
    { label: '欠费', value: Number(overview.arrearsAmount || 0), valueText: `¥${overview.arrearsAmount || 0}`, color: '#8b5cf6' },
  ]
  const maxValue = Math.max(...items.map((item) => item.value), 1)
  return items.map((item) => ({
    ...item,
    percent: Math.round((item.value / maxValue) * 100),
  }))
})

const rankedDoctors = computed(() => {
  const rows = [...doctorPerformance.value].sort((left, right) => Number(right.performanceAmount || 0) - Number(left.performanceAmount || 0))
  const maxValue = Math.max(...rows.map((item) => Number(item.performanceAmount || 0)), 1)
  return rows.map((item) => ({
    ...item,
    percent: Math.round((Number(item.performanceAmount || 0) / maxValue) * 100),
  }))
})

async function loadData() {
  loading.value = true
  try {
    const [clinicRes, overviewRes, doctorRes] = await Promise.all([
      api.org.clinics(),
      api.report.clinicOverview(filters),
      api.report.doctorPerformance(filters),
    ])
    clinics.value = clinicRes.data || []
    Object.assign(overview, overviewRes.data || {})
    doctorPerformance.value = doctorRes.data || []
  } finally {
    loading.value = false
  }
}

function exportDoctorPerformance() {
  const rows = [
    ['统计日期', filters.statDate || '', '门诊', clinics.value.find((item) => item.id === filters.clinicId)?.clinicName || '全部门诊'],
    [],
    ['医生', '预约量', '到诊量', '初诊量', '复诊量', '接诊量', '业绩金额'],
    ...doctorPerformance.value.map((item) => [
      item.doctorName || '',
      item.appointmentCount || 0,
      item.arrivedCount || 0,
      item.firstVisitCount || 0,
      item.revisitCount || 0,
      item.treatmentCount || 0,
      item.performanceAmount || 0,
    ]),
  ]
  const csvContent = `\uFEFF${rows.map((row) => row.map((cell) => `"${String(cell ?? '').replace(/"/g, '""')}"`).join(',')).join('\n')}`
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `doctor-performance-${filters.statDate || dayjs().format('YYYY-MM-DD')}.csv`
  link.click()
  URL.revokeObjectURL(url)
}

onMounted(loadData)
</script>
