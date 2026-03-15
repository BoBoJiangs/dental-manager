<template>
  <section class="page-shell">
    <div class="page-card">
      <div class="el-card__body">
        <PageHeader title="预约中心" description="覆盖预约列表、创建、改约、取消、签到、排班和候诊管理。">
          <el-radio-group v-model="filters.mode">
            <el-radio-button value="list">列表视图</el-radio-button>
            <el-radio-button value="calendar">日历视图</el-radio-button>
          </el-radio-group>
          <el-button v-if="hasButtonPermission('APPOINTMENT_EDIT')" type="primary" @click="openCreateDialog">新建预约</el-button>
        </PageHeader>
        <el-form :inline="true" :model="filters" class="toolbar" style="margin-top: 18px;">
          <el-form-item label="预约日期">
            <el-date-picker v-model="filters.appointmentDate" type="date" value-format="YYYY-MM-DD" />
          </el-form-item>
          <el-form-item label="门诊">
            <el-select v-model="filters.clinicId" clearable style="width: 160px;">
              <el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="医生">
            <el-select v-model="filters.doctorId" clearable style="width: 160px;">
              <el-option v-for="item in doctors" :key="item.doctorId" :label="item.doctorName" :value="item.doctorId" />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input v-model.trim="filters.keyword" placeholder="患者/手机号/项目" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadAppointments">查询</el-button>
            <el-button @click="resetFilters">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="page-card">
      <div class="el-card__body">
        <template v-if="filters.mode === 'list'">
          <el-table v-loading="loading" :data="appointments" size="large">
            <el-table-column prop="appointmentNo" label="预约号" min-width="120" />
            <el-table-column prop="patientName" label="患者" min-width="120" />
            <el-table-column prop="doctorName" label="医生" min-width="120" />
            <el-table-column prop="appointmentDate" label="日期" min-width="110" />
            <el-table-column prop="treatmentItemName" label="项目" min-width="140" />
            <el-table-column label="状态" width="110">
              <template #default="{ row }">
                <StatusTag :status="row.status" />
              </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" width="260">
              <template #default="{ row }">
                <el-space wrap>
                  <el-button v-if="hasButtonPermission('APPOINTMENT_EDIT')" link type="primary" @click="openCreateDialog(row)">改约</el-button>
                  <el-button v-if="hasButtonPermission('APPOINTMENT_CHECKIN')" link type="success" @click="checkIn(row.id)">签到</el-button>
                  <el-button v-if="hasButtonPermission('APPOINTMENT_EDIT')" link type="danger" @click="cancelAppointment(row.id)">取消</el-button>
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
        </template>
        <template v-else>
          <el-calendar v-model="calendarDate">
            <template #date-cell="{ data }">
              <div style="display: grid; gap: 6px; min-height: 96px;">
                <div style="font-weight: 700;">{{ data.day.split('-').slice(2).join('') }}</div>
                <div v-for="item in calendarMap[data.day]?.slice(0, 3) || []" :key="item.id" style="display: grid; gap: 4px;">
                  <div class="soft-tag info" style="justify-content: flex-start; font-size: 11px;">
                    {{ item.startTime?.slice(11, 16) || '--:--' }} {{ item.patientName }}
                  </div>
                </div>
                <el-link v-if="(calendarMap[data.day]?.length || 0) > 3" type="primary" :underline="false" @click="openDayList(data.day)">
                  +{{ (calendarMap[data.day]?.length || 0) - 3 }} 条
                </el-link>
              </div>
            </template>
          </el-calendar>
        </template>
      </div>
    </div>

    <div class="split-grid">
      <div class="page-card">
        <div class="el-card__body">
          <div class="page-header">
            <div>
              <h3 class="page-title" style="font-size: 20px;">医生排班</h3>
              <p class="page-desc">当前页面直接支持排班创建</p>
            </div>
            <el-button v-if="hasButtonPermission('APPOINTMENT_EDIT')" type="primary" plain @click="openScheduleDialog">新增排班</el-button>
          </div>
          <el-table :data="schedules" size="large" style="margin-top: 16px;">
            <el-table-column prop="scheduleDate" label="日期" min-width="110" />
            <el-table-column prop="doctorName" label="医生" min-width="120" />
            <el-table-column prop="roomName" label="诊室" min-width="120" />
            <el-table-column prop="scheduleType" label="类型" min-width="110" />
            <el-table-column prop="maxAppointmentCount" label="最大预约" min-width="110" />
          </el-table>
        </div>
      </div>

      <div class="page-card">
        <div class="el-card__body">
          <div class="page-header">
            <div>
              <h3 class="page-title" style="font-size: 20px;">候诊列表</h3>
              <p class="page-desc">支持叫号与治疗状态流转</p>
            </div>
          </div>
          <div class="list" style="margin-top: 16px;">
            <div v-for="queue in queues" :key="queue.id" class="list-item">
              <div class="list-main">
                <div class="list-title">{{ queue.queueNo || '-' }} · {{ queue.patientName }}</div>
                <div class="list-desc">{{ queue.doctorName }} · {{ queue.appointmentDate }}</div>
              </div>
              <div style="display: flex; gap: 8px; align-items: center;">
                <StatusTag :status="queue.queueStatus" />
                <el-dropdown v-if="hasButtonPermission('APPOINTMENT_CHECKIN')" @command="(status: string | number | object) => updateQueue(queue.id, String(status))">
                  <el-button link type="primary">流转</el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="CALLING">叫号</el-dropdown-item>
                      <el-dropdown-item command="IN_TREATMENT">治疗中</el-dropdown-item>
                      <el-dropdown-item command="COMPLETED">已完成</el-dropdown-item>
                      <el-dropdown-item command="SKIPPED">跳过</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="currentAppointmentId ? '改约' : '新建预约'" width="760px">
      <el-form :model="appointmentForm" label-position="top" class="form-grid">
        <el-form-item label="门诊">
          <el-select v-model="appointmentForm.clinicId" style="width: 100%;">
            <el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="患者">
          <EntityRemoteSelect
            v-model="appointmentForm.patientId"
            :request="fetchPatients"
            label-key="label"
            value-key="id"
            :selected-label="selectedLabels.patient"
            placeholder="搜索患者姓名或手机号"
          />
        </el-form-item>
        <el-form-item label="医生">
          <el-select v-model="appointmentForm.doctorId" style="width: 100%;">
            <el-option v-for="item in doctors" :key="item.doctorId" :label="item.doctorName" :value="item.doctorId" />
          </el-select>
        </el-form-item>
        <el-form-item label="诊室">
          <el-select v-model="appointmentForm.roomId" clearable style="width: 100%;">
            <el-option v-for="item in rooms" :key="item.id" :label="item.roomName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约来源">
          <el-select v-model="appointmentForm.sourceType">
            <el-option label="PC" value="PC" />
            <el-option label="小程序" value="MINI_PROGRAM" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约类型">
          <el-select v-model="appointmentForm.appointmentType">
            <el-option label="初诊" value="FIRST" />
            <el-option label="复诊" value="REVISIT" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker v-model="appointmentForm.appointmentDate" type="date" value-format="YYYY-MM-DD" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="appointmentForm.startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="appointmentForm.endTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%;" />
        </el-form-item>
        <el-form-item class="full-span" label="治疗项目">
          <el-input v-model.trim="appointmentForm.treatmentItemName" />
        </el-form-item>
        <el-form-item class="full-span" label="备注">
          <el-input v-model.trim="appointmentForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitAppointment">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="scheduleDialogVisible" title="新增排班" width="720px">
      <el-form :model="scheduleForm" label-position="top" class="form-grid">
        <el-form-item label="门诊">
          <el-select v-model="scheduleForm.clinicId" style="width: 100%;">
            <el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="医生">
          <el-select v-model="scheduleForm.doctorId" style="width: 100%;">
            <el-option v-for="item in doctors" :key="item.doctorId" :label="item.doctorName" :value="item.doctorId" />
          </el-select>
        </el-form-item>
        <el-form-item label="诊室">
          <el-select v-model="scheduleForm.roomId" clearable style="width: 100%;">
            <el-option v-for="item in rooms" :key="item.id" :label="item.roomName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排班日期">
          <el-date-picker v-model="scheduleForm.scheduleDate" type="date" value-format="YYYY-MM-DD" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-time-picker v-model="scheduleForm.startTime" value-format="HH:mm:ss" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-time-picker v-model="scheduleForm.endTime" value-format="HH:mm:ss" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="排班类型">
          <el-select v-model="scheduleForm.scheduleType">
            <el-option label="门诊" value="OUTPATIENT" />
            <el-option label="停诊" value="STOP" />
            <el-option label="请假" value="LEAVE" />
          </el-select>
        </el-form-item>
        <el-form-item label="最大预约数">
          <el-input-number v-model="scheduleForm.maxAppointmentCount" :min="0" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="scheduleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSchedule">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import dayjs from 'dayjs'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '@/api/service'
import PageHeader from '@/components/PageHeader.vue'
import StatusTag from '@/components/StatusTag.vue'
import EntityRemoteSelect from '@/components/EntityRemoteSelect.vue'
import { usePermission } from '@/composables/usePermission'
import { useRouteQueryState } from '@/composables/useRouteQueryState'

const loading = ref(false)
const saving = ref(false)
const { hasButtonPermission } = usePermission()
const appointments = ref<any[]>([])
const schedules = ref<any[]>([])
const queues = ref<any[]>([])
const monthlyAppointments = ref<any[]>([])
const clinics = ref<any[]>([])
const doctors = ref<any[]>([])
const rooms = ref<any[]>([])
const dialogVisible = ref(false)
const scheduleDialogVisible = ref(false)
const currentAppointmentId = ref<number | null>(null)
const calendarDate = ref(new Date())
const selectedLabels = reactive({
  patient: '',
})

const filters = reactive({
  appointmentDate: dayjs().format('YYYY-MM-DD'),
  clinicId: undefined as number | undefined,
  doctorId: undefined as number | undefined,
  keyword: '',
  mode: 'list',
  current: 1,
})
useRouteQueryState(filters)
const filterState = filters as Record<string, any>
if (filterState.clinicId !== undefined && filterState.clinicId !== '') {
  filterState.clinicId = Number(filterState.clinicId)
}
if (filterState.doctorId !== undefined && filterState.doctorId !== '') {
  filterState.doctorId = Number(filterState.doctorId)
}

const appointmentForm = reactive<Record<string, any>>({
  clinicId: undefined,
  patientId: undefined,
  doctorId: undefined,
  roomId: undefined,
  sourceType: 'PC',
  appointmentType: 'FIRST',
  appointmentDate: dayjs().format('YYYY-MM-DD'),
  startTime: `${dayjs().format('YYYY-MM-DD')}T09:30:00`,
  endTime: `${dayjs().format('YYYY-MM-DD')}T10:00:00`,
  treatmentItemName: '',
  remark: '',
})

const scheduleForm = reactive<Record<string, any>>({
  clinicId: undefined,
  doctorId: undefined,
  roomId: undefined,
  scheduleDate: dayjs().format('YYYY-MM-DD'),
  startTime: '09:00:00',
  endTime: '18:00:00',
  scheduleType: 'OUTPATIENT',
  maxAppointmentCount: 8,
})
const pagination = reactive({ current: 1, size: 10, total: 0 })

const calendarMap = computed(() => {
  return monthlyAppointments.value.reduce<Record<string, any[]>>((acc, item) => {
    const key = item.appointmentDate
    if (!acc[key]) {
      acc[key] = []
    }
    acc[key].push(item)
    return acc
  }, {})
})

async function loadLookup() {
  const [clinicsRes, doctorsRes, roomsRes] = await Promise.all([
    api.org.clinics(),
    api.patient.doctorOptions(),
    api.org.rooms(),
  ])
  clinics.value = clinicsRes.data || []
  doctors.value = doctorsRes.data || []
  rooms.value = roomsRes.data || []
  appointmentForm.clinicId ||= clinics.value[0]?.id
  scheduleForm.clinicId ||= clinics.value[0]?.id
}

async function loadAppointments() {
  loading.value = true
  try {
    const [appointmentRes, scheduleRes, queueRes] = await Promise.all([
      api.appointment.page({
        appointmentDate: filters.appointmentDate,
        clinicId: filters.clinicId ? Number(filters.clinicId) : undefined,
        doctorId: filters.doctorId ? Number(filters.doctorId) : undefined,
        keyword: filters.keyword,
        current: Number(filters.current || pagination.current),
        size: pagination.size,
      }),
      api.appointment.schedules({
        scheduleDate: filters.appointmentDate,
        clinicId: filters.clinicId ? Number(filters.clinicId) : undefined,
        doctorId: filters.doctorId ? Number(filters.doctorId) : undefined,
      }),
      api.appointment.queues({
        queueDate: filters.appointmentDate,
        clinicId: filters.clinicId ? Number(filters.clinicId) : undefined,
        doctorId: filters.doctorId ? Number(filters.doctorId) : undefined,
      }),
    ])
    appointments.value = appointmentRes.data.records || []
    pagination.total = appointmentRes.data.total || 0
    pagination.current = Number(filters.current || 1)
    schedules.value = scheduleRes.data || []
    queues.value = queueRes.data || []
  } finally {
    loading.value = false
  }
}

async function loadMonthlyAppointments() {
  const monthStart = dayjs(calendarDate.value).startOf('month').format('YYYY-MM-DD')
  const monthEnd = dayjs(calendarDate.value).endOf('month').format('YYYY-MM-DD')
  try {
    const response = await api.appointment.page({
      clinicId: filters.clinicId ? Number(filters.clinicId) : undefined,
      doctorId: filters.doctorId ? Number(filters.doctorId) : undefined,
      keyword: filters.keyword,
      dateFrom: monthStart,
      dateTo: monthEnd,
      current: 1,
      size: 100,
    })
    monthlyAppointments.value = response.data.records || []
  } catch {
    monthlyAppointments.value = []
  }
}

function resetFilters() {
  filters.appointmentDate = dayjs().format('YYYY-MM-DD')
  filters.clinicId = undefined
  filters.doctorId = undefined
  filters.keyword = ''
  filters.mode = 'list'
  filters.current = 1
  loadAppointments()
  loadMonthlyAppointments()
}

function openCreateDialog(row?: any) {
  if (row) {
    currentAppointmentId.value = row.id
    Object.assign(appointmentForm, {
      clinicId: row.clinicId,
      patientId: row.patientId,
      doctorId: row.doctorId,
      roomId: row.roomId,
      sourceType: row.sourceType,
      appointmentType: row.appointmentType,
      appointmentDate: row.appointmentDate,
      startTime: row.startTime,
      endTime: row.endTime,
      treatmentItemName: row.treatmentItemName,
      remark: row.remark,
    })
    selectedLabels.patient = row.patientName ? `${row.patientName} / ${row.patientMobile || ''}` : ''
  } else {
    currentAppointmentId.value = null
    selectedLabels.patient = ''
  }
  dialogVisible.value = true
}

async function submitAppointment() {
  saving.value = true
  try {
    if (currentAppointmentId.value) {
      await api.appointment.reschedule(currentAppointmentId.value, appointmentForm)
      ElMessage.success('改约成功')
    } else {
      await api.appointment.create(appointmentForm)
      ElMessage.success('预约创建成功')
    }
    dialogVisible.value = false
    await loadAppointments()
  } finally {
    saving.value = false
  }
}

async function cancelAppointment(id: number) {
  const { value } = await ElMessageBox.prompt('请输入取消原因', '取消预约', { inputValue: '时间冲突' })
  await api.appointment.cancel(id, { cancelReason: value, remark: value })
  ElMessage.success('已取消预约')
  await loadAppointments()
}

async function checkIn(id: number) {
  await api.appointment.checkIn(id, { remark: '后台签到' })
  ElMessage.success('签到成功')
  await loadAppointments()
}

function openScheduleDialog() {
  scheduleDialogVisible.value = true
}

async function submitSchedule() {
  await api.appointment.createSchedule(scheduleForm)
  ElMessage.success('排班创建成功')
  scheduleDialogVisible.value = false
  await loadAppointments()
}

async function updateQueue(id: number, status: string) {
  await api.appointment.updateQueue(id, { queueStatus: status, remark: '后台流转' })
  ElMessage.success('候诊状态已更新')
  await loadAppointments()
}

async function fetchPatients(keyword: string) {
  const response = await api.patient.page({
    keyword,
    current: 1,
    size: 20,
  })
  return (response.data.records || []).map((item: any) => ({
    id: item.id,
    label: `${item.patientName} / ${item.mobile || '-'}`,
  }))
}

function openDayList(day: string) {
  filters.appointmentDate = day
  filters.mode = 'list'
  filters.current = 1
  loadAppointments()
}

function handleCurrentChange(value: number) {
  filters.current = value
  loadAppointments()
}

watch(calendarDate, () => {
  if (filters.mode === 'calendar') {
    loadMonthlyAppointments()
  }
})

watch(
  () => [filters.clinicId, filters.doctorId, filters.keyword, filters.mode],
  ([, , , mode]) => {
    if (mode === 'calendar') {
      loadMonthlyAppointments()
    }
  },
)

onMounted(async () => {
  await loadLookup()
  await loadAppointments()
  await loadMonthlyAppointments()
})
</script>
