import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

export function usePermission() {
  const authStore = useAuthStore()

  const menuPermissions = computed<string[]>(() => authStore.permissions?.menuPermissions || [])
  const buttonPermissions = computed<string[]>(() => authStore.permissions?.buttonPermissions || [])

  function hasMenuPermission(permission?: string) {
    if (!permission) {
      return true
    }
    return menuPermissions.value.includes(permission)
  }

  function hasButtonPermission(permission?: string) {
    if (!permission) {
      return true
    }
    return buttonPermissions.value.includes(permission)
  }

  return {
    menuPermissions,
    buttonPermissions,
    hasMenuPermission,
    hasButtonPermission,
  }
}
