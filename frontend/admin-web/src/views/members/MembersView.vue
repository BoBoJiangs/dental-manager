<template>
  <section class="page-shell">
    <div class="page-card">
      <div class="el-card__body">
        <PageHeader title="会员中心" description="支持会员分页、详情、开卡、充值、积分调整和状态维护。">
          <el-button v-if="hasButtonPermission('MEMBER_CREATE')" type="primary" @click="openCreateDialog">开通会员</el-button>
        </PageHeader>
        <el-form :inline="true" :model="filters" class="toolbar" style="margin-top: 18px;">
          <el-form-item label="关键词">
            <el-input v-model.trim="filters.keyword" placeholder="会员号/患者/手机号" clearable />
          </el-form-item>
          <el-form-item label="等级">
            <el-select v-model="filters.levelId" clearable style="width: 160px;">
              <el-option v-for="item in levels" :key="item.id" :label="item.levelName" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="filters.memberStatus" clearable style="width: 160px;">
              <el-option label="正常" value="NORMAL" />
              <el-option label="冻结" value="FROZEN" />
              <el-option label="注销" value="CANCELLED" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button @click="resetFilters">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="page-card">
      <div class="el-card__body">
        <el-table :data="members" v-loading="loading" size="large">
          <el-table-column prop="memberNo" label="会员号" min-width="120" />
          <el-table-column prop="patientName" label="患者" min-width="120" />
          <el-table-column prop="patientMobile" label="手机号" min-width="140" />
          <el-table-column prop="levelName" label="等级" min-width="120" />
          <el-table-column prop="balanceAmount" label="余额" min-width="120" />
          <el-table-column prop="pointsBalance" label="积分" min-width="120" />
          <el-table-column prop="totalRechargeAmount" label="累计充值" min-width="120" />
          <el-table-column label="状态" width="120">
            <template #default="{ row }">
              <StatusTag :status="row.memberStatus" />
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" width="280">
            <template #default="{ row }">
              <el-space wrap>
                <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
                <el-button v-if="hasButtonPermission('MEMBER_OPERATE')" link type="success" @click="openRechargeDialog(row)">充值</el-button>
                <el-button v-if="hasButtonPermission('MEMBER_OPERATE')" link type="primary" @click="openPointsDialog(row)">积分</el-button>
                <el-button v-if="hasButtonPermission('MEMBER_OPERATE')" link type="danger" @click="openStatusDialog(row)">状态</el-button>
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
      </div>
    </div>

    <el-dialog v-model="createDialogVisible" title="开通会员" width="640px">
      <el-form :model="memberForm" label-position="top" class="form-grid">
        <el-form-item label="患者">
          <EntityRemoteSelect v-model="memberForm.patientId" :request="fetchPatients" label-key="label" value-key="id" :selected-label="selectedLabels.patient" placeholder="搜索患者" />
        </el-form-item>
        <el-form-item label="会员等级">
          <el-select v-model="memberForm.levelId">
            <el-option v-for="item in levels" :key="item.id" :label="item.levelName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="会员状态">
          <el-select v-model="memberForm.memberStatus">
            <el-option label="正常" value="NORMAL" />
            <el-option label="冻结" value="FROZEN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createMember">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="rechargeDialogVisible" title="会员充值" width="560px">
      <el-form :model="rechargeForm" label-position="top">
        <el-form-item label="充值金额"><el-input-number v-model="rechargeForm.amount" :min="0.01" :step="100" style="width: 100%;" /></el-form-item>
        <el-form-item label="业务类型"><el-input v-model.trim="rechargeForm.bizType" placeholder="RECHARGE" /></el-form-item>
        <el-form-item label="备注"><el-input v-model.trim="rechargeForm.remark" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rechargeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRecharge">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="pointsDialogVisible" title="积分调整" width="560px">
      <el-form :model="pointsForm" label-position="top">
        <el-form-item label="变动积分"><el-input-number v-model="pointsForm.changePoints" :step="10" style="width: 100%;" /></el-form-item>
        <el-form-item label="业务类型"><el-input v-model.trim="pointsForm.bizType" placeholder="EARN / ADJUST" /></el-form-item>
        <el-form-item label="备注"><el-input v-model.trim="pointsForm.remark" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pointsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPoints">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="statusDialogVisible" title="会员状态" width="560px">
      <el-form :model="statusForm" label-position="top">
        <el-form-item label="会员状态">
          <el-select v-model="statusForm.memberStatus">
            <el-option label="正常" value="NORMAL" />
            <el-option label="冻结" value="FROZEN" />
            <el-option label="注销" value="CANCELLED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStatus">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" title="会员详情" size="720px">
      <template v-if="detail">
        <div class="summary-grid" style="grid-template-columns: repeat(3, minmax(0, 1fr));">
          <div class="summary-card">
            <div class="summary-label">会员号</div>
            <div class="summary-value" style="font-size: 22px;">{{ detail.memberNo }}</div>
            <div class="summary-meta">{{ detail.patientName }} / {{ detail.patientMobile || '-' }}</div>
          </div>
          <div class="summary-card">
            <div class="summary-label">账户余额</div>
            <div class="summary-value" style="font-size: 22px;">¥{{ detail.balanceAmount || 0 }}</div>
            <div class="summary-meta">累计充值 ¥{{ detail.totalRechargeAmount || 0 }}</div>
          </div>
          <div class="summary-card">
            <div class="summary-label">积分余额</div>
            <div class="summary-value" style="font-size: 22px;">{{ detail.pointsBalance || 0 }}</div>
            <div class="summary-meta">状态 {{ detail.memberStatus }}</div>
          </div>
        </div>

        <div class="split-grid" style="margin-top: 20px;">
          <div class="page-card" style="box-shadow: none;">
            <div class="el-card__body">
              <div class="page-header">
                <div>
                  <h3 class="page-title" style="font-size: 18px;">余额流水</h3>
                </div>
              </div>
              <el-table :data="detail.recentBalanceRecords || []" size="large" style="margin-top: 12px;">
                <el-table-column prop="bizType" label="类型" min-width="100" />
                <el-table-column prop="changeAmount" label="变动" min-width="100" />
                <el-table-column prop="afterBalance" label="余额" min-width="100" />
                <el-table-column prop="createdAt" label="时间" min-width="160" />
              </el-table>
            </div>
          </div>
          <div class="page-card" style="box-shadow: none;">
            <div class="el-card__body">
              <div class="page-header">
                <div>
                  <h3 class="page-title" style="font-size: 18px;">积分流水</h3>
                </div>
              </div>
              <el-table :data="detail.recentPointsRecords || []" size="large" style="margin-top: 12px;">
                <el-table-column prop="bizType" label="类型" min-width="100" />
                <el-table-column prop="changePoints" label="变动" min-width="100" />
                <el-table-column prop="afterPoints" label="积分" min-width="100" />
                <el-table-column prop="createdAt" label="时间" min-width="160" />
              </el-table>
            </div>
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
const createDialogVisible = ref(false)
const rechargeDialogVisible = ref(false)
const pointsDialogVisible = ref(false)
const statusDialogVisible = ref(false)
const detailVisible = ref(false)
const currentMemberId = ref<number | null>(null)
const members = ref<any[]>([])
const levels = ref<any[]>([])
const detail = ref<any>(null)
const selectedLabels = reactive<Record<string, string>>({ patient: '' })

const filters = reactive({
  keyword: '',
  levelId: undefined as number | undefined,
  memberStatus: '',
  current: 1,
})
useRouteQueryState(filters, { current: 1 })
const memberFilterState = filters as Record<string, any>
if (memberFilterState.levelId !== undefined && memberFilterState.levelId !== '') {
  memberFilterState.levelId = Number(memberFilterState.levelId)
}
const pagination = reactive({ current: 1, size: 10, total: 0 })

const memberForm = reactive({
  patientId: undefined as number | undefined,
  levelId: undefined as number | undefined,
  memberStatus: 'NORMAL',
})

const rechargeForm = reactive({
  amount: 100,
  bizType: 'RECHARGE',
  remark: '',
})

const pointsForm = reactive({
  changePoints: 10,
  bizType: 'EARN',
  remark: '',
})

const statusForm = reactive({
  memberStatus: 'NORMAL',
})

async function loadData() {
  loading.value = true
  try {
    const [memberRes, levelRes] = await Promise.all([
      api.member.page({
        keyword: filters.keyword || undefined,
        levelId: filters.levelId,
        memberStatus: filters.memberStatus || undefined,
        current: Number(filters.current || pagination.current),
        size: pagination.size,
      }),
      api.member.levels(),
    ])
    members.value = memberRes.data.records || []
    levels.value = levelRes.data || []
    pagination.total = memberRes.data.total || 0
    pagination.current = Number(filters.current || 1)
    memberForm.levelId ||= levels.value[0]?.id
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  filters.keyword = ''
  filters.levelId = undefined
  filters.memberStatus = ''
  filters.current = 1
  loadData()
}

function handleCurrentChange(value: number) {
  filters.current = value
  loadData()
}

function openCreateDialog() {
  Object.assign(memberForm, {
    patientId: undefined,
    levelId: levels.value[0]?.id,
    memberStatus: 'NORMAL',
  })
  createDialogVisible.value = true
}

async function createMember() {
  await api.member.create(memberForm)
  ElMessage.success('会员开通成功')
  createDialogVisible.value = false
  await loadData()
}

async function openDetail(id: number) {
  const response = await api.member.detail(id)
  detail.value = response.data
  detailVisible.value = true
}

function openRechargeDialog(row: any) {
  currentMemberId.value = row.id
  Object.assign(rechargeForm, { amount: 100, bizType: 'RECHARGE', remark: '' })
  rechargeDialogVisible.value = true
}

async function submitRecharge() {
  if (!currentMemberId.value) {
    return
  }
  await api.member.recharge(currentMemberId.value, rechargeForm)
  ElMessage.success('会员充值成功')
  rechargeDialogVisible.value = false
  await loadData()
  await openDetail(currentMemberId.value)
}

function openPointsDialog(row: any) {
  currentMemberId.value = row.id
  Object.assign(pointsForm, { changePoints: 10, bizType: 'EARN', remark: '' })
  pointsDialogVisible.value = true
}

async function submitPoints() {
  if (!currentMemberId.value) {
    return
  }
  await api.member.points(currentMemberId.value, pointsForm)
  ElMessage.success('积分调整成功')
  pointsDialogVisible.value = false
  await loadData()
  await openDetail(currentMemberId.value)
}

function openStatusDialog(row: any) {
  currentMemberId.value = row.id
  Object.assign(statusForm, { memberStatus: row.memberStatus || 'NORMAL' })
  statusDialogVisible.value = true
}

async function submitStatus() {
  if (!currentMemberId.value) {
    return
  }
  await api.member.status(currentMemberId.value, statusForm)
  ElMessage.success('会员状态已更新')
  statusDialogVisible.value = false
  await loadData()
  await openDetail(currentMemberId.value)
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
