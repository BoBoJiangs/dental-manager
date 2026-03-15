<template>
  <div class="summary-card">
    <div class="summary-label">{{ label }}</div>
    <div class="summary-value">{{ value }}</div>
    <div v-if="meta" class="summary-meta">{{ meta }}</div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  label: string
  value: string | number
  meta?: string
}>()
</script>
