<template>
  <div v-if="detailData" class="page-shell">
    <div class="page-card">
      <div class="el-card__body">
        <h3 class="page-title" style="font-size: 24px;">{{ detailData.patientName }}</h3>
        <p class="page-desc">{{ detailData.patientCode }} · {{ detailData.mobile }} · 首诊门诊 {{ clinicName }}</p>
        <div class="toolbar" style="margin-top: 12px;">
          <StatusTag :status="detailData.patientStatus === 1 ? 'NORMAL' : 'FAIL'" />
          <el-tag>{{ tagSummary }}</el-tag>
          <el-tag type="success">{{ doctorSummary }}</el-tag>
        </div>
      </div>
    </div>
    <div class="page-card tab-shell">
      <div class="el-card__body">
        <el-tabs>
          <el-tab-pane label="历史就诊">
            <el-table :data="visits" size="large">
              <el-table-column prop="recordNo" label="病历号" min-width="120" />
              <el-table-column prop="doctorName" label="医生" min-width="100" />
              <el-table-column prop="visitDate" label="就诊日期" min-width="150" />
              <el-table-column prop="signedFlag" label="签署" min-width="90" />
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="影像资料">
            <el-table :data="images" size="large">
              <el-table-column prop="fileName" label="文件名" min-width="160" />
              <el-table-column prop="imageType" label="类型" min-width="100" />
              <el-table-column prop="shotTime" label="拍摄时间" min-width="150" />
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="会员信息">
            <el-descriptions :column="2" border v-if="member">
              <el-descriptions-item label="会员号">{{ member.memberNo }}</el-descriptions-item>
              <el-descriptions-item label="等级">{{ member.levelName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="余额">{{ member.balanceAmount }}</el-descriptions-item>
              <el-descriptions-item label="积分">{{ member.pointsBalance }}</el-descriptions-item>
            </el-descriptions>
            <div v-else class="empty-block">
              <div>当前患者暂无会员信息</div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import StatusTag from '@/components/StatusTag.vue'

const props = defineProps<{
  detailData: any
  visits: any[]
  images: any[]
  member: any
  clinicName?: string
}>()

const tagSummary = computed(() => {
  const tags = props.detailData?.tags || []
  if (!tags.length) {
    return '标签：未设置'
  }
  return `标签：${tags.map((item: any) => item.tagName).join('、')}`
})

const doctorSummary = computed(() => {
  const doctors = props.detailData?.primaryDoctors || []
  if (!doctors.length) {
    return '主治医生：未设置'
  }
  return `主治医生：${doctors.map((item: any) => item.doctorName).join('、')}`
})
</script>
