<template>
  <section class="page-shell">
    <div class="page-card">
      <div class="el-card__body">
        <PageHeader title="短信中心" description="支持模板查看、任务筛选、详情查看和定时创建短信任务。">
          <el-button v-if="hasButtonPermission('SMS_CREATE')" type="primary" @click="openCreateDialog">创建任务</el-button>
        </PageHeader>
        <el-form :inline="true" :model="filters" class="toolbar" style="margin-top: 18px;">
          <el-form-item label="门诊">
            <el-select v-model="filters.clinicId" clearable style="width: 160px;">
              <el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="业务类型">
            <el-select v-model="filters.bizType" clearable style="width: 160px;">
              <el-option label="预约" value="APPOINTMENT" />
              <el-option label="复诊" value="REVISIT" />
              <el-option label="欠费" value="ARREARS" />
              <el-option label="营销" value="MARKETING" />
            </el-select>
          </el-form-item>
          <el-form-item label="任务状态">
            <el-select v-model="filters.taskStatus" clearable style="width: 160px;">
              <el-option label="待发送" value="PENDING" />
              <el-option label="发送中" value="SENDING" />
              <el-option label="成功" value="SUCCESS" />
              <el-option label="失败" value="FAIL" />
            </el-select>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model.trim="filters.mobile" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button @click="resetFilters">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="page-card tab-shell">
      <div class="el-card__body">
        <el-tabs>
          <el-tab-pane label="模板">
            <el-table :data="templates" v-loading="loading" size="large">
              <el-table-column prop="templateCode" label="模板编码" min-width="140" />
              <el-table-column prop="templateName" label="模板名称" min-width="160" />
              <el-table-column prop="bizType" label="业务类型" min-width="120" />
              <el-table-column prop="signName" label="签名" min-width="120" />
              <el-table-column prop="content" label="内容" min-width="320" show-overflow-tooltip />
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="任务">
            <el-table :data="tasks" v-loading="loading" size="large">
              <el-table-column prop="templateName" label="模板" min-width="160" />
              <el-table-column prop="patientName" label="患者" min-width="120" />
              <el-table-column prop="mobile" label="手机号" min-width="130" />
              <el-table-column prop="bizType" label="业务类型" min-width="120" />
              <el-table-column prop="scheduledAt" label="计划时间" min-width="160" />
              <el-table-column label="状态" width="120">
                <template #default="{ row }">
                  <StatusTag :status="row.taskStatus" />
                </template>
              </el-table-column>
              <el-table-column fixed="right" label="操作" width="100">
                <template #default="{ row }">
                  <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
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
        </el-tabs>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" title="创建短信任务" width="760px">
      <el-form :model="smsForm" label-position="top" class="form-grid">
        <el-form-item label="门诊">
          <el-select v-model="smsForm.clinicId">
            <el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="模板">
          <el-select v-model="smsForm.templateId">
            <el-option v-for="item in templates" :key="item.id" :label="item.templateName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务类型">
          <el-select v-model="smsForm.bizType">
            <el-option label="预约" value="APPOINTMENT" />
            <el-option label="复诊" value="REVISIT" />
            <el-option label="欠费" value="ARREARS" />
            <el-option label="营销" value="MARKETING" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务 ID"><el-input-number v-model="smsForm.bizId" :min="1" style="width: 100%;" /></el-form-item>
        <el-form-item label="患者">
          <EntityRemoteSelect v-model="smsForm.patientId" :request="fetchPatients" label-key="label" value-key="id" :selected-label="selectedLabels.patient" placeholder="搜索患者" />
        </el-form-item>
        <el-form-item label="手机号"><el-input v-model.trim="smsForm.mobile" /></el-form-item>
        <el-form-item class="full-span" label="模板参数 JSON">
          <el-input v-model="templateParamsText" type="textarea" :rows="4" placeholder='{"patientName":"张三","appointmentDate":"2026-03-15"}' />
        </el-form-item>
        <el-form-item class="full-span" label="计划发送时间">
          <el-date-picker v-model="smsForm.scheduledAt" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="留空表示立即发送" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createTask">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" title="短信任务详情" size="720px">
      <template v-if="detail">
        <div class="summary-grid" style="grid-template-columns: repeat(3, minmax(0, 1fr));">
          <div class="summary-card">
            <div class="summary-label">模板</div>
            <div class="summary-value" style="font-size: 22px;">{{ detail.templateName }}</div>
            <div class="summary-meta">{{ detail.mobile }}</div>
          </div>
          <div class="summary-card">
            <div class="summary-label">状态</div>
            <div class="summary-value" style="font-size: 22px;">{{ detail.taskStatus }}</div>
            <div class="summary-meta">计划 {{ detail.scheduledAt || '-' }}</div>
          </div>
          <div class="summary-card">
            <div class="summary-label">业务关联</div>
            <div class="summary-value" style="font-size: 22px;">{{ detail.bizType }}</div>
            <div class="summary-meta">业务 ID {{ detail.bizId || '-' }}</div>
          </div>
        </div>

        <div class="page-card" style="box-shadow: none; margin-top: 20px;">
          <div class="el-card__body">
            <div class="page-header">
              <div>
                <h3 class="page-title" style="font-size: 18px;">模板参数</h3>
              </div>
            </div>
            <el-descriptions :column="1" border style="margin-top: 12px;">
              <el-descriptions-item v-for="(value, key) in detail.templateParams || {}" :key="key" :label="String(key)">
                {{ value }}
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </div>

        <div class="page-card" style="box-shadow: none; margin-top: 20px;">
          <div class="el-card__body">
            <div class="page-header">
              <div>
                <h3 class="page-title" style="font-size: 18px;">发送记录</h3>
              </div>
            </div>
            <el-table :data="detail.sendRecords || []" size="large" style="margin-top: 12px;">
              <el-table-column prop="providerName" label="服务商" min-width="120" />
              <el-table-column prop="sendStatus" label="状态" min-width="100" />
              <el-table-column prop="responseCode" label="返回码" min-width="100" />
              <el-table-column prop="responseMsg" label="返回信息" min-width="180" />
              <el-table-column prop="sentAt" label="发送时间" min-width="160" />
            </el-table>
          </div>
        </div>
      </template>
    </el-drawer>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '@/api/service'
import EntityRemoteSelect from '@/components/EntityRemoteSelect.vue'
import PageHeader from '@/components/PageHeader.vue'
import StatusTag from '@/components/StatusTag.vue'
import { usePermission } from '@/composables/usePermission'
import { useRouteQueryState } from '@/composables/useRouteQueryState'

const loading = ref(false)
const { hasButtonPermission } = usePermission()
const dialogVisible = ref(false)
const detailVisible = ref(false)
const templates = ref<any[]>([])
const tasks = ref<any[]>([])
const clinics = ref<any[]>([])
const detail = ref<any>(null)
const templateParamsText = ref('{}')
const selectedLabels = reactive<Record<string, string>>({ patient: '' })

const filters = reactive({
  clinicId: undefined as number | undefined,
  bizType: '',
  taskStatus: '',
  mobile: '',
  current: 1,
})
useRouteQueryState(filters, { current: 1 })
const smsFilterState = filters as Record<string, any>
if (smsFilterState.clinicId !== undefined && smsFilterState.clinicId !== '') {
  smsFilterState.clinicId = Number(smsFilterState.clinicId)
}
const pagination = reactive({ current: 1, size: 10, total: 0 })

const smsForm = reactive({
  clinicId: undefined as number | undefined,
  templateId: undefined as number | undefined,
  bizType: 'APPOINTMENT',
  bizId: undefined as number | undefined,
  patientId: undefined as number | undefined,
  mobile: '',
  scheduledAt: '',
})

async function loadData() {
  loading.value = true
  try {
    const [templateRes, taskRes, clinicRes] = await Promise.all([
      api.sms.templates(),
      api.sms.tasks({
        clinicId: filters.clinicId,
        bizType: filters.bizType || undefined,
        taskStatus: filters.taskStatus || undefined,
        mobile: filters.mobile || undefined,
        current: Number(filters.current || pagination.current),
        size: pagination.size,
      }),
      api.org.clinics(),
    ])
    templates.value = templateRes.data || []
    tasks.value = taskRes.data.records || []
    clinics.value = clinicRes.data || []
    pagination.total = taskRes.data.total || 0
    pagination.current = Number(filters.current || 1)
    smsForm.clinicId ||= clinics.value[0]?.id
    smsForm.templateId ||= templates.value[0]?.id
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  filters.clinicId = undefined
  filters.bizType = ''
  filters.taskStatus = ''
  filters.mobile = ''
  filters.current = 1
  loadData()
}

function handleCurrentChange(value: number) {
  filters.current = value
  loadData()
}

function openCreateDialog() {
  Object.assign(smsForm, {
    clinicId: clinics.value[0]?.id,
    templateId: templates.value[0]?.id,
    bizType: 'APPOINTMENT',
    bizId: undefined,
    patientId: undefined,
    mobile: '',
    scheduledAt: '',
  })
  templateParamsText.value = '{}'
  dialogVisible.value = true
}

async function createTask() {
  let templateParams = {}
  if (templateParamsText.value.trim()) {
    try {
      templateParams = JSON.parse(templateParamsText.value)
    } catch {
      ElMessage.warning('模板参数 JSON 格式不正确')
      return
    }
  }
  await api.sms.createTask({
    ...smsForm,
    templateParams,
  })
  ElMessage.success('短信任务创建成功')
  dialogVisible.value = false
  await loadData()
}

async function openDetail(id: number) {
  const response = await api.sms.taskDetail(id)
  detail.value = response.data
  detailVisible.value = true
}

async function fetchPatients(keyword: string) {
  const response = await api.patient.page({ keyword, current: 1, size: 20 })
  return (response.data.records || []).map((item: any) => ({
    id: item.id,
    label: `${item.patientName} / ${item.mobile || '-'}`,
  }))
}

onMounted(loadData)
</script>
