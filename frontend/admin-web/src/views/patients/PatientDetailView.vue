<template>
  <section class="page-shell">
    <div class="page-card">
      <div class="el-card__body">
        <PageHeader title="患者详情" description="独立详情页支持刷新恢复与路由分享。">
          <el-button @click="router.back()">返回</el-button>
          <el-button v-if="hasButtonPermission('PATIENT_EDIT')" type="primary" @click="router.push('/patients')">返回患者列表编辑</el-button>
        </PageHeader>
      </div>
    </div>
    <PatientDetailContent :detail-data="detailData" :visits="detailVisits" :images="detailImages" :member="detailMember" :clinic-name="clinicName" />
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '@/api/service'
import PageHeader from '@/components/PageHeader.vue'
import PatientDetailContent from '@/components/patient/PatientDetailContent.vue'
import { usePermission } from '@/composables/usePermission'

const route = useRoute()
const router = useRouter()
const { hasButtonPermission } = usePermission()

const detailData = ref<any>(null)
const detailVisits = ref<any[]>([])
const detailImages = ref<any[]>([])
const detailMember = ref<any>(null)
const clinics = ref<any[]>([])
const clinicName = computed(() => clinics.value.find((item) => item.id === detailData.value?.firstClinicId)?.clinicName || detailData.value?.firstClinicId || '-')

async function loadDetail() {
  const id = Number(route.params.id)
  const [detailRes, visitsRes, imagesRes, memberRes, clinicsRes] = await Promise.all([
    api.patient.detail(id),
    api.patient.visitRecords(id),
    api.patient.images(id),
    api.patient.member(id),
    api.org.clinics(),
  ])
  detailData.value = detailRes.data
  detailVisits.value = visitsRes.data || []
  detailImages.value = imagesRes.data || []
  detailMember.value = memberRes.data || null
  clinics.value = clinicsRes.data || []
}

onMounted(loadDetail)
</script>
