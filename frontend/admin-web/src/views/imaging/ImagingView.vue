<template>
  <section class="page-shell">
    <div class="page-card">
      <div class="el-card__body">
        <PageHeader title="影像资料" description="覆盖附件上传、影像记录和术前术后对比三条链路。">
          <el-button v-if="hasButtonPermission('IMAGING_EDIT')" type="primary" @click="attachmentDialogVisible = true">上传附件</el-button>
          <el-button v-if="hasButtonPermission('IMAGING_EDIT')" type="primary" plain @click="imageDialogVisible = true">新增影像</el-button>
          <el-button v-if="hasButtonPermission('IMAGING_EDIT')" type="primary" plain @click="compareDialogVisible = true">新增对比组</el-button>
        </PageHeader>
      </div>
    </div>

    <div class="page-card tab-shell">
      <div class="el-card__body">
        <el-tabs>
          <el-tab-pane label="附件列表">
            <div class="toolbar" style="margin-bottom: 16px;">
              <EntityRemoteSelect v-model="attachmentFilters.patientId" :request="fetchPatients" label-key="label" value-key="id" :selected-label="selectedLabels.patient" placeholder="搜索患者" />
              <EntityRemoteSelect v-model="attachmentFilters.medicalRecordId" :request="fetchRecords" label-key="label" value-key="id" :selected-label="selectedLabels.record" placeholder="搜索病历" />
              <el-button type="primary" @click="loadAttachments">查询</el-button>
            </div>
            <el-table :data="attachments" v-loading="loading" size="large">
              <el-table-column prop="fileName" label="文件名" min-width="180" />
              <el-table-column prop="bizType" label="业务类型" min-width="120" />
              <el-table-column prop="patientName" label="患者" min-width="120" />
              <el-table-column prop="medicalRecordNo" label="病历号" min-width="120" />
              <el-table-column prop="fileUrl" label="访问地址" min-width="220" />
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="影像记录">
            <el-table :data="images" v-loading="loading" size="large">
              <el-table-column prop="fileName" label="文件名" min-width="180" />
              <el-table-column prop="patientName" label="患者" min-width="120" />
              <el-table-column prop="imageType" label="影像类型" min-width="120" />
              <el-table-column prop="imageGroupType" label="分组类型" min-width="120" />
              <el-table-column prop="shotTime" label="拍摄时间" min-width="160" />
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="术前术后对比">
            <el-table :data="compareGroups" v-loading="loading" size="large">
              <el-table-column prop="groupName" label="对比组名称" min-width="180" />
              <el-table-column prop="patientName" label="患者" min-width="120" />
              <el-table-column prop="medicalRecordNo" label="病历号" min-width="120" />
              <el-table-column prop="compareDesc" label="说明" min-width="220" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <el-dialog v-model="attachmentDialogVisible" title="上传附件" width="720px">
      <el-form :model="attachmentForm" label-position="top" class="form-grid">
        <el-form-item label="门诊">
          <el-select v-model="attachmentForm.clinicId"><el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" /></el-select>
        </el-form-item>
        <el-form-item label="业务类型">
          <el-select v-model="attachmentForm.bizType"><el-option label="病历" value="MEDICAL_RECORD" /><el-option label="签名" value="SIGNATURE" /><el-option label="患者" value="PATIENT" /></el-select>
        </el-form-item>
        <el-form-item label="业务关联">
          <el-input :model-value="attachmentBizDisplay" disabled />
        </el-form-item>
        <el-form-item label="患者">
          <EntityRemoteSelect v-model="attachmentForm.patientId" :request="fetchPatients" label-key="label" value-key="id" :selected-label="selectedLabels.patient" placeholder="搜索患者" />
        </el-form-item>
        <el-form-item label="病历">
          <EntityRemoteSelect v-model="attachmentForm.medicalRecordId" :request="fetchRecords" label-key="label" value-key="id" :selected-label="selectedLabels.record" placeholder="搜索病历" />
        </el-form-item>
        <el-form-item label="上传来源"><el-select v-model="attachmentForm.uploadSource"><el-option label="PC" value="PC" /></el-select></el-form-item>
        <el-form-item class="full-span" label="文件">
          <input type="file" @change="handleFileChange" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="attachmentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAttachment">上传</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="imageDialogVisible" title="新增影像记录" width="720px">
      <el-form :model="imageForm" label-position="top" class="form-grid">
        <el-form-item label="门诊"><el-select v-model="imageForm.clinicId"><el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="患者">
          <EntityRemoteSelect v-model="imageForm.patientId" :request="fetchPatients" label-key="label" value-key="id" :selected-label="selectedLabels.patient" placeholder="搜索患者" />
        </el-form-item>
        <el-form-item label="病历">
          <EntityRemoteSelect v-model="imageForm.medicalRecordId" :request="fetchRecords" label-key="label" value-key="id" :selected-label="selectedLabels.record" placeholder="搜索病历" />
        </el-form-item>
        <el-form-item label="附件文件">
          <EntityRemoteSelect v-model="imageForm.fileId" :request="fetchFiles" label-key="label" value-key="id" placeholder="搜索附件" />
        </el-form-item>
        <el-form-item label="影像类型"><el-select v-model="imageForm.imageType"><el-option label="口内照" value="INTRAORAL" /><el-option label="面像" value="FACE" /><el-option label="X 光" value="XRAY" /></el-select></el-form-item>
        <el-form-item label="分组类型"><el-select v-model="imageForm.imageGroupType"><el-option label="普通" value="NORMAL" /><el-option label="术前" value="PRE_OP" /><el-option label="术后" value="POST_OP" /></el-select></el-form-item>
        <el-form-item label="拍摄时间"><el-date-picker v-model="imageForm.shotTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%;" /></el-form-item>
        <el-form-item label="牙位"><el-input v-model.trim="imageForm.toothPosition" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="imageDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitImage">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="compareDialogVisible" title="新增术前术后对比组" width="720px">
      <el-form :model="compareForm" label-position="top" class="form-grid">
        <el-form-item label="患者">
          <EntityRemoteSelect v-model="compareForm.patientId" :request="fetchPatients" label-key="label" value-key="id" :selected-label="selectedLabels.patient" placeholder="搜索患者" />
        </el-form-item>
        <el-form-item label="病历">
          <EntityRemoteSelect v-model="compareForm.medicalRecordId" :request="fetchRecords" label-key="label" value-key="id" :selected-label="selectedLabels.record" placeholder="搜索病历" />
        </el-form-item>
        <el-form-item class="full-span" label="对比组名称"><el-input v-model.trim="compareForm.groupName" /></el-form-item>
        <el-form-item label="术前图片">
          <EntityRemoteSelect v-model="compareForm.preImageId" :request="fetchImages" label-key="label" value-key="id" placeholder="搜索术前图片" />
        </el-form-item>
        <el-form-item label="术后图片">
          <EntityRemoteSelect v-model="compareForm.postImageId" :request="fetchImages" label-key="label" value-key="id" placeholder="搜索术后图片" />
        </el-form-item>
        <el-form-item class="full-span" label="说明"><el-input v-model.trim="compareForm.compareDesc" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="compareDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCompare">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '@/api/service'
import EntityRemoteSelect from '@/components/EntityRemoteSelect.vue'
import PageHeader from '@/components/PageHeader.vue'
import { usePermission } from '@/composables/usePermission'

const loading = ref(false)
const { hasButtonPermission } = usePermission()
const attachments = ref<any[]>([])
const images = ref<any[]>([])
const compareGroups = ref<any[]>([])
const clinics = ref<any[]>([])
const selectedFile = ref<File | null>(null)
const selectedLabels = reactive<Record<string, string>>({
  patient: '',
  record: '',
})

const attachmentDialogVisible = ref(false)
const imageDialogVisible = ref(false)
const compareDialogVisible = ref(false)

const attachmentFilters = reactive({
  patientId: undefined as number | undefined,
  medicalRecordId: undefined as number | undefined,
})

const attachmentForm = reactive<Record<string, any>>({
  clinicId: undefined,
  bizType: 'MEDICAL_RECORD',
  patientId: undefined,
  medicalRecordId: undefined,
  uploadSource: 'PC',
})

const imageForm = reactive<Record<string, any>>({
  clinicId: undefined,
  patientId: undefined,
  medicalRecordId: undefined,
  fileId: undefined,
  imageType: 'INTRAORAL',
  imageGroupType: 'NORMAL',
  shotTime: '',
  toothPosition: '',
})

const compareForm = reactive<Record<string, any>>({
  patientId: undefined,
  medicalRecordId: undefined,
  groupName: '',
  preImageId: undefined,
  postImageId: undefined,
  compareDesc: '',
})

const attachmentBizId = computed(() => {
  if (attachmentForm.bizType === 'PATIENT') {
    return attachmentForm.patientId
  }
  return attachmentForm.medicalRecordId
})

const attachmentBizDisplay = computed(() => {
  if (!attachmentBizId.value) {
    return '请先选择对应患者或病历'
  }
  if (attachmentForm.bizType === 'PATIENT') {
    return `PATIENT / ${attachmentBizId.value}`
  }
  return `${attachmentForm.bizType} / ${attachmentBizId.value}`
})

async function loadClinics() {
  const res = await api.org.clinics()
  clinics.value = res.data || []
  attachmentForm.clinicId ||= clinics.value[0]?.id
  imageForm.clinicId ||= clinics.value[0]?.id
}

async function loadAttachments() {
  loading.value = true
  try {
    const [attachmentRes, imageRes, compareRes] = await Promise.all([
      api.file.attachments(attachmentFilters),
      api.imaging.images({ patientId: attachmentFilters.patientId, medicalRecordId: attachmentFilters.medicalRecordId }),
      api.imaging.compareGroups({ patientId: attachmentFilters.patientId, medicalRecordId: attachmentFilters.medicalRecordId }),
    ])
    attachments.value = attachmentRes.data || []
    images.value = imageRes.data || []
    compareGroups.value = compareRes.data || []
  } finally {
    loading.value = false
  }
}

function handleFileChange(event: Event) {
  const target = event.target as HTMLInputElement
  selectedFile.value = target.files?.[0] || null
}

async function submitAttachment() {
  if (!selectedFile.value) {
    ElMessage.warning('请选择要上传的文件')
    return
  }
  if (!attachmentBizId.value) {
    ElMessage.warning(attachmentForm.bizType === 'PATIENT' ? '请选择患者' : '请选择病历')
    return
  }
  const formData = new FormData()
  const payload = {
    ...attachmentForm,
    bizId: attachmentBizId.value,
  }
  Object.entries(payload).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      formData.append(key, String(value))
    }
  })
  formData.append('file', selectedFile.value)
  const response = await api.file.uploadAttachment(formData)
  ElMessage.success(`附件上传成功，文件 ID：${response.data.id}`)
  attachmentDialogVisible.value = false
  imageForm.fileId = response.data.id
  await loadAttachments()
}

async function submitImage() {
  await api.imaging.createImage(imageForm)
  ElMessage.success('影像记录创建成功')
  imageDialogVisible.value = false
  await loadAttachments()
}

async function submitCompare() {
  await api.imaging.createCompareGroup(compareForm)
  ElMessage.success('对比组创建成功')
  compareDialogVisible.value = false
  await loadAttachments()
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

async function fetchFiles(_keyword: string) {
  if (!imageForm.patientId && !imageForm.medicalRecordId) {
    return []
  }
  const response = await api.file.attachments({
    patientId: imageForm.patientId,
    medicalRecordId: imageForm.medicalRecordId,
  })
  return (response.data || []).map((item: any) => ({
    id: item.id,
    label: `${item.fileName} / ${item.bizType}`,
  }))
}

async function fetchImages(_keyword: string) {
  if (!compareForm.patientId) {
    return []
  }
  const response = await api.imaging.images({
    patientId: compareForm.patientId,
    medicalRecordId: compareForm.medicalRecordId,
  })
  return (response.data || []).map((item: any) => ({
    id: item.id,
    label: `${item.fileName} / ${item.imageGroupType || item.imageType}`,
  }))
}

onMounted(async () => {
  await loadClinics()
  await loadAttachments()
})
</script>
