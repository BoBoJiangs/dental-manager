import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { api } from '@/api/service'
import type { LoginResponse, PermissionSnapshot } from '@/types'

const TOKEN_KEY = 'dental_admin_token'
const USER_KEY = 'dental_admin_user'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>(localStorage.getItem(TOKEN_KEY) || '')
  const user = ref<LoginResponse | null>(JSON.parse(localStorage.getItem(USER_KEY) || 'null'))
  const permissions = ref<PermissionSnapshot | null>(null)

  const isAuthenticated = computed(() => Boolean(token.value))

  function persistAuth(payload: LoginResponse) {
    token.value = payload.accessToken
    user.value = payload
    localStorage.setItem(TOKEN_KEY, payload.accessToken)
    localStorage.setItem(USER_KEY, JSON.stringify(payload))
  }

  function clearAuth() {
    token.value = ''
    user.value = null
    permissions.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  async function login(payload: { username: string; password: string }) {
    const response = await api.auth.login(payload)
    persistAuth(response.data)
    return response.data
  }

  async function loadCurrentUser() {
    if (!token.value) {
      return null
    }
    const response = await api.auth.me()
    user.value = response.data
    return response.data
  }

  async function loadPermissions() {
    if (!token.value) {
      return null
    }
    const response = await api.auth.permissions()
    permissions.value = response.data
    return response.data
  }

  async function logout() {
    try {
      if (token.value) {
        await api.auth.logout()
      }
    } finally {
      clearAuth()
    }
  }

  return {
    token,
    user,
    permissions,
    isAuthenticated,
    login,
    logout,
    loadCurrentUser,
    loadPermissions,
    clearAuth,
  }
})
