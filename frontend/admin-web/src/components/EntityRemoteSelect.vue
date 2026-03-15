<template>
  <el-select
    :model-value="modelValue"
    filterable
    remote
    clearable
    reserve-keyword
    :placeholder="placeholder"
    :remote-method="remoteMethod"
    :loading="loading"
    style="width: 100%;"
    @focus="loadOptions('')"
    @update:model-value="(value: string | number | undefined) => emit('update:modelValue', value)"
  >
    <el-option
      v-for="item in mergedOptions"
      :key="String(item[valueKey])"
      :label="resolveLabel(item)"
      :value="item[valueKey]"
    />
  </el-select>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

const props = withDefaults(
  defineProps<{
    modelValue?: string | number
    request: (keyword: string) => Promise<any[]>
    placeholder?: string
    labelKey?: string
    valueKey?: string
    selectedLabel?: string
  }>(),
  {
    placeholder: '请选择',
    labelKey: 'label',
    valueKey: 'value',
    selectedLabel: '',
  },
)

const emit = defineEmits<{
  (e: 'update:modelValue', value: string | number | undefined): void
}>()

const loading = ref(false)
const options = ref<any[]>([])

const mergedOptions = computed(() => {
  if (props.modelValue == null || props.selectedLabel === '') {
    return options.value
  }
  const exists = options.value.some((item) => item[props.valueKey] === props.modelValue)
  if (exists) {
    return options.value
  }
  return [
    {
      [props.valueKey]: props.modelValue,
      [props.labelKey]: props.selectedLabel,
    },
    ...options.value,
  ]
})

function resolveLabel(item: any) {
  return item?.[props.labelKey] ?? ''
}

async function loadOptions(keyword: string) {
  loading.value = true
  try {
    options.value = await props.request(keyword)
  } finally {
    loading.value = false
  }
}

function remoteMethod(query: string) {
  loadOptions(query)
}
</script>
