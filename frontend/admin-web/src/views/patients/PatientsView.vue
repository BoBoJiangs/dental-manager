<template>
  <section class="page-shell">
    <div class="page-card">
      <div class="el-card__body">
        <PageHeader title="患者中心" description="支持患者档案分页、详情查看、建档编辑，以及就诊/影像/会员聚合信息。">
          <el-button v-if="hasButtonPermission('PATIENT_EDIT')" type="primary" @click="openCreateDialog">新建患者</el-button>
        </PageHeader>
        <el-form :inline="true" :model="filters" class="toolbar" style="margin-top: 18px;">
          <el-form-item label="关键词">
            <el-input v-model.trim="filters.keyword" placeholder="姓名/编号/手机号" clearable />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model.trim="filters.mobile" placeholder="手机号" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="filters.patientStatus" clearable style="width: 140px;">
              <el-option label="正常" :value="1" />
              <el-option label="停用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadPatients">查询</el-button>
            <el-button @click="resetFilters">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="page-card">
      <div class="el-card__body">
        <el-table v-loading="loading" :data="patients" size="large">
          <el-table-column prop="patientCode" label="患者编号" min-width="130" />
          <el-table-column prop="patientName" label="姓名" min-width="110" />
          <el-table-column prop="mobile" label="手机号" min-width="130" />
          <el-table-column prop="birthday" label="生日" min-width="120" />
          <el-table-column label="首诊门诊" min-width="110">
            <template #default="{ row }">
              {{ clinicNameMap[row.firstClinicId] || row.firstClinicId || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="120">
            <template #default="{ row }">
              <StatusTag :status="row.patientStatus === 1 ? 'NORMAL' : 'FAIL'" />
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" width="220">
            <template #default="{ row }">
              <el-space>
                <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
                <el-button v-if="hasButtonPermission('PATIENT_EDIT')" link type="primary" @click="openEditDialog(row)">编辑</el-button>
                <el-button v-if="hasButtonPermission('PATIENT_DELETE')" link type="danger" @click="removePatient(row.id)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'create' ? '新建患者' : '编辑患者'" width="760px">
      <el-form :model="patientForm" label-position="top" class="form-grid">
        <el-form-item class="full-span" label="患者姓名">
          <el-input v-model.trim="patientForm.patientName" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="patientForm.gender" clearable>
            <el-option label="男" :value="1" />
            <el-option label="女" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="生日">
          <el-date-picker v-model="patientForm.birthday" type="date" value-format="YYYY-MM-DD" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model.trim="patientForm.mobile" />
        </el-form-item>
        <el-form-item label="证件号">
          <el-input v-model.trim="patientForm.idNo" />
        </el-form-item>
        <el-form-item label="来源">
          <el-select v-model="patientForm.sourceCode">
            <el-option label="前台登记" value="FRONT_DESK" />
            <el-option label="小程序预约" value="MINI_PROGRAM" />
            <el-option label="线上推广" value="ONLINE" />
          </el-select>
        </el-form-item>
        <el-form-item label="首诊门诊">
          <el-select v-model="patientForm.firstClinicId" style="width: 100%;">
            <el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="患者状态">
          <el-select v-model="patientForm.patientStatus">
            <el-option label="正常" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="会员状态">
          <el-select v-model="patientForm.memberStatus">
            <el-option label="非会员" :value="0" />
            <el-option label="会员" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="patientForm.tagIds" multiple collapse-tags style="width: 100%;">
            <el-option v-for="item in tagOptions" :key="item.id" :label="item.tagName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item class="full-span" label="备注">
          <el-input v-model.trim="patientForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitPatient">保存</el-button>
      </template>
    </el-dialog>

  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '@/api/service'
import PageHeader from '@/components/PageHeader.vue'
import StatusTag from '@/components/StatusTag.vue'
import { usePermission } from '@/composables/usePermission'
import { useRouteQueryState } from '@/composables/useRouteQueryState'

const loading = ref(false)
const saving = ref(false)
const router = useRouter()
const { hasButtonPermission } = usePermission()
const patients = ref<any[]>([])
const clinics = ref<any[]>([])
const tagOptions = ref<any[]>([])
const dialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit'>('create')
const currentPatientId = ref<number | null>(null)
const clinicNameMap = computed(() =>
  Object.fromEntries(clinics.value.map((item: any) => [item.id, item.clinicName])),
)

const filters = reactive({
  keyword: '',
  mobile: '',
  patientStatus: undefined as number | string | undefined,
  current: 1,
})
useRouteQueryState(filters, { current: 1 })
const pagination = reactive({ current: 1, size: 10, total: 0 })

const patientForm = reactive<Record<string, any>>({
  patientName: '',
  gender: undefined,
  birthday: '',
  mobile: '',
  idNo: '',
  sourceCode: 'FRONT_DESK',
  firstClinicId: undefined,
  memberStatus: 0,
  patientStatus: 1,
  tagIds: [],
  remark: '',
})

async function loadPatients() {
  loading.value = true
  try {
    const response = await api.patient.page({
      ...filters,
      patientStatus: filters.patientStatus ? Number(filters.patientStatus) : undefined,
      current: Number(filters.current || pagination.current),
      size: pagination.size,
    })
    patients.value = response.data.records || []
    pagination.total = response.data.total || 0
    pagination.current = Number(filters.current || 1)
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  filters.keyword = ''
  filters.mobile = ''
  filters.patientStatus = undefined
  filters.current = 1
  loadPatients()
}

async function loadLookup() {
  const [clinicsRes, tagsRes] = await Promise.all([api.org.clinics(), api.patient.tagOptions()])
  clinics.value = clinicsRes.data || []
  tagOptions.value = tagsRes.data || []
}

function resetForm() {
  Object.assign(patientForm, {
    patientName: '',
    gender: undefined,
    birthday: '',
    mobile: '',
    idNo: '',
    sourceCode: 'FRONT_DESK',
    firstClinicId: clinics.value[0]?.id,
    memberStatus: 0,
    patientStatus: 1,
    tagIds: [],
    remark: '',
  })
}

function openCreateDialog() {
  dialogMode.value = 'create'
  currentPatientId.value = null
  resetForm()
  dialogVisible.value = true
}

async function openEditDialog(row: any) {
  dialogMode.value = 'edit'
  currentPatientId.value = row.id
  const detailRes = await api.patient.detail(row.id)
  const detail = detailRes.data
  Object.assign(patientForm, {
    patientName: detail.patientName,
    gender: detail.gender,
    birthday: detail.birthday,
    mobile: detail.mobile,
    idNo: detail.idNo,
    sourceCode: detail.sourceCode || 'FRONT_DESK',
    firstClinicId: detail.firstClinicId,
    memberStatus: detail.memberStatus ?? 0,
    patientStatus: detail.patientStatus ?? 1,
    tagIds: detail.tags?.map((item: any) => item.id) || [],
    remark: detail.remark || '',
  })
  dialogVisible.value = true
}

async function submitPatient() {
  if (!patientForm.patientName) {
    ElMessage.warning('请输入患者姓名')
    return
  }
  if (!patientForm.mobile || !/^1\d{10}$/.test(patientForm.mobile)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  if (!patientForm.sourceCode) {
    ElMessage.warning('请选择患者来源')
    return
  }
  if (!patientForm.firstClinicId) {
    ElMessage.warning('请选择首诊门诊')
    return
  }

  saving.value = true
  try {
    const payload = { ...patientForm }
    if (dialogMode.value === 'create') {
      await api.patient.create(payload)
      ElMessage.success('患者建档成功')
    } else if (currentPatientId.value) {
      await api.patient.update(currentPatientId.value, payload)
      ElMessage.success('患者更新成功')
    }
    dialogVisible.value = false
    await loadPatients()
  } finally {
    saving.value = false
  }
}

function openDetail(id: number) {
  router.push(`/patients/${id}`)
}

async function removePatient(id: number) {
  await ElMessageBox.confirm('删除患者后不可恢复，确认继续？', '删除确认', { type: 'warning' })
  await api.patient.remove(id)
  ElMessage.success('删除成功')
  await loadPatients()
}

function handleCurrentChange(value: number) {
  filters.current = value
  loadPatients()
}

onMounted(async () => {
  await loadLookup()
  resetForm()
  await loadPatients()
})
</script>
