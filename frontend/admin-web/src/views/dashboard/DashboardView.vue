<template>
  <section class="page-shell">
    <div class="summary-grid">
      <StatCard 
        label="今日预约" 
        :value="workbench.todayAppointmentCount ?? 0" 
        meta="系统工作台统计" 
        :icon="Calendar" 
        icon-class="primary"
      />
      <StatCard 
        label="已到诊" 
        :value="workbench.todayArrivedCount ?? 0" 
        meta="实时到诊状态" 
        :icon="Check" 
        icon-class="success"
      />
      <StatCard 
        label="待接诊" 
        :value="workbench.waitingPatientCount ?? 0" 
        meta="候诊队列" 
        :icon="Timer" 
        icon-class="warning"
      />
      <StatCard 
        label="今日实收" 
        :value="`¥${clinicOverview.paidAmount ?? 0}`" 
        meta="门诊经营概览" 
        :icon="Wallet" 
        icon-class="danger"
      />
    </div>

    <div class="split-grid">
      <div class="page-card">
        <div class="el-card__body">
          <div class="page-header">
            <div>
              <h3 class="page-title" style="font-size: 20px;">今日预约</h3>
              <p class="page-desc">展示最近预约与当前状态</p>
            </div>
            <RouterLink class="app-link" to="/appointments">查看全部</RouterLink>
          </div>
          <el-table v-loading="loading" :data="appointments" style="margin-top: 16px;" size="large">
            <el-table-column prop="appointmentDate" label="日期" min-width="110" />
            <el-table-column prop="patientName" label="患者" min-width="120" />
            <el-table-column prop="doctorName" label="医生" min-width="110" />
            <el-table-column prop="treatmentItemName" label="项目" min-width="140" />
            <el-table-column label="状态" width="120">
              <template #default="{ row }">
                <StatusTag :status="row.status" />
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <div class="page-card">
        <div class="el-card__body">
          <div class="page-header">
            <div>
              <h3 class="page-title" style="font-size: 20px;">候诊与待办</h3>
              <p class="page-desc">叫号流转和短信任务一屏查看</p>
            </div>
          </div>
          <div class="list" style="margin-top: 16px;">
            <div v-for="queue in queues" :key="queue.id" class="list-item">
              <div class="list-main">
                <div class="list-title">{{ queue.queueNo || '-' }} · {{ queue.patientName }}</div>
                <div class="list-desc">{{ queue.doctorName }} · {{ queue.remark || '候诊中' }}</div>
              </div>
              <StatusTag :status="queue.queueStatus" />
            </div>
            <div v-if="queues.length === 0" class="empty-block">
              <div>暂无候诊记录</div>
            </div>
          </div>
          <el-divider />
          <div class="list">
            <div v-for="task in smsTasks" :key="task.id" class="list-item">
              <div class="list-main">
                <div class="list-title">{{ task.templateName }}</div>
                <div class="list-desc">{{ task.patientName || task.mobile }} · {{ task.bizType }}</div>
              </div>
              <StatusTag :status="task.taskStatus" />
            </div>
            <div v-if="smsTasks.length === 0" class="empty-block">
              <div>暂无短信任务</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="page-card">
      <div class="el-card__body">
        <div class="page-header">
          <div>
            <h3 class="page-title" style="font-size: 20px;">医生业绩</h3>
            <p class="page-desc">首版用表格呈现，后续可升级成 Bullet Chart</p>
          </div>
          <RouterLink class="app-link" to="/reports">查看报表中心</RouterLink>
        </div>
        <el-table v-loading="loading" :data="doctorPerformance" style="margin-top: 16px;" size="large">
          <el-table-column prop="doctorName" label="医生" min-width="120" />
          <el-table-column prop="appointmentCount" label="预约量" width="100" />
          <el-table-column prop="arrivedCount" label="到诊量" width="100" />
          <el-table-column prop="treatmentCount" label="接诊量" width="100" />
          <el-table-column prop="performanceAmount" label="业绩金额" min-width="140" />
        </el-table>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { Calendar, Check, Timer, Wallet } from '@element-plus/icons-vue'
import { api } from '@/api/service'
import StatCard from '@/components/StatCard.vue'
import StatusTag from '@/components/StatusTag.vue'

const loading = ref(false)
const workbench = reactive<Record<string, number>>({})
const clinicOverview = reactive<Record<string, any>>({})
const appointments = ref<any[]>([])
const queues = ref<any[]>([])
const smsTasks = ref<any[]>([])
const doctorPerformance = ref<any[]>([])

async function loadDashboard() {
  loading.value = true
  const today = dayjs().format('YYYY-MM-DD')
  try {
    const [workbenchRes, overviewRes, appointmentsRes, queuesRes, smsRes, doctorRes] = await Promise.all([
      api.system.workbench(),
      api.report.clinicOverview({ statDate: today }),
      api.appointment.page({ current: 1, size: 6, appointmentDate: today }),
      api.appointment.queues({ queueDate: today }),
      api.sms.tasks({ current: 1, size: 5 }),
      api.report.doctorPerformance({ statDate: today }),
    ])
    Object.assign(workbench, workbenchRes.data || {})
    Object.assign(clinicOverview, overviewRes.data || {})
    appointments.value = appointmentsRes.data.records || []
    queues.value = queuesRes.data || []
    smsTasks.value = smsRes.data.records || []
    doctorPerformance.value = doctorRes.data || []
  } finally {
    loading.value = false
  }
}

onMounted(loadDashboard)
</script>
