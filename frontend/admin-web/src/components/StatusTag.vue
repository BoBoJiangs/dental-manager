<template>
  <span class="soft-tag" :class="themeClass">{{ label }}</span>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  status?: string | number | null
}>()

const mapping: Record<string, { label: string; theme: string }> = {
  CONFIRMED: { label: '已确认', theme: 'info' },
  ARRIVED: { label: '已到诊', theme: 'success' },
  CANCELLED: { label: '已取消', theme: 'danger' },
  WAITING: { label: '待接诊', theme: 'warning' },
  CALLING: { label: '叫号中', theme: 'info' },
  IN_TREATMENT: { label: '治疗中', theme: 'success' },
  COMPLETED: { label: '已完成', theme: 'success' },
  PENDING: { label: '待处理', theme: 'warning' },
  SUCCESS: { label: '成功', theme: 'success' },
  FAIL: { label: '失败', theme: 'danger' },
  PAID: { label: '已结清', theme: 'success' },
  PART_PAID: { label: '部分支付', theme: 'warning' },
  REFUNDED: { label: '已退款', theme: 'danger' },
  OPEN: { label: '开班中', theme: 'warning' },
  CLOSED: { label: '已关班', theme: 'success' },
  NORMAL: { label: '正常', theme: 'success' },
  DRAFT: { label: '草稿', theme: 'warning' },
  SIGNED: { label: '已签署', theme: 'success' },
  ARCHIVED: { label: '已归档', theme: 'info' },
}

const config = computed(() => mapping[String(props.status ?? '').toUpperCase()] || { label: String(props.status || '-'), theme: 'info' })
const label = computed(() => config.value.label)
const themeClass = computed(() => config.value.theme)
</script>
