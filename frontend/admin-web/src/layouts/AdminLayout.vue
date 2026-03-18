<template>
  <div class="showcase-page">
    <div class="pc-shell">
      <aside class="sidebar">
        <div class="brand">
          <div class="brand-mark"></div>
          <div>
            <div class="list-title">Dental Manager</div>
            <div class="list-desc">{{ authStore.user?.username || 'Admin' }}</div>
          </div>
        </div>
        <nav class="menu">
          <RouterLink
            v-for="item in visibleMenus"
            :key="item.path"
            class="menu-item"
            :class="{ active: route.path === item.path || route.path.startsWith(`${item.path}/`) }"
            :to="item.path"
          >
            <el-icon class="menu-icon" :size="20" style="margin-right: 12px;">
              <component :is="item.icon" />
            </el-icon>
            <span>{{ item.title }}</span>
          </RouterLink>
        </nav>
      </aside>

      <div class="content">
        <header class="topbar">
          <div class="topbar-main">
            <div class="topbar-kicker">Dental Manager</div>
            <div class="topbar-title">{{ currentTitle }}</div>
          </div>
          <div class="topbar-actions">
            <el-tag type="info" effect="plain" round>{{ authStore.user?.roles?.join(' / ') || '未加载角色' }}</el-tag>
            <el-button type="danger" plain round size="small" @click="handleLogout">退出登录</el-button>
          </div>
        </header>
        <div class="main-content">
          <RouterView />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { menuItems } from '@/config/menu'
import { usePermission } from '@/composables/usePermission'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { hasMenuPermission } = usePermission()

const currentTitle = computed(() => (route.meta.title as string) || '管理后台')
const visibleMenus = computed(() => menuItems.filter((item) => hasMenuPermission(item.permission)))

async function handleLogout() {
  await authStore.logout()
  ElMessage.success('已退出登录')
  router.replace('/login')
}
</script>
