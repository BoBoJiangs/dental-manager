<template>
  <div class="summary-card">
    <div class="summary-header">
      <div class="summary-label">{{ label }}</div>
      <el-icon v-if="icon" class="summary-icon" :class="iconClass">
        <component :is="icon" />
      </el-icon>
    </div>
    <div class="summary-value">{{ value }}</div>
    <div v-if="meta" class="summary-meta">
      <el-icon v-if="trend === 'up'" color="#22C55E"><Top /></el-icon>
      <el-icon v-else-if="trend === 'down'" color="#EF4444"><Bottom /></el-icon>
      <span>{{ meta }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Top, Bottom } from '@element-plus/icons-vue'

defineProps<{
  label: string
  value: string | number
  meta?: string
  icon?: any
  iconClass?: string
  trend?: 'up' | 'down' | 'neutral'
}>()
</script>

<style scoped>
.summary-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.summary-icon {
  font-size: 20px;
  color: var(--app-muted);
  background: #F1F5F9;
  padding: 8px;
  border-radius: 8px;
  box-sizing: content-box;
}

.summary-icon.primary { color: var(--app-primary); background: rgba(8, 145, 178, 0.1); }
.summary-icon.success { color: var(--app-cta); background: rgba(34, 197, 94, 0.1); }
.summary-icon.warning { color: var(--el-color-warning); background: rgba(245, 158, 11, 0.1); }
.summary-icon.danger { color: var(--el-color-danger); background: rgba(239, 68, 68, 0.1); }
</style>
