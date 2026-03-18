<template>
  <div class="page-header" :aria-label="title">
    <div>
      <h3 class="page-title">{{ title }}</h3>
      <p v-if="description" class="page-desc">{{ description }}</p>
    </div>
    <div style="display: flex; gap: 12px; align-items: center;">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  description?: string
}>()
</script>
