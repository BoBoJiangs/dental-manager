<template>
  <div class="page-intro" :aria-label="title">
    <div class="page-intro-text">
      <p v-if="description" class="page-intro-desc">{{ description }}</p>
    </div>
    <div class="toolbar">
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
