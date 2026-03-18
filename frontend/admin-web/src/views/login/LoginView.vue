<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <div class="brand-mark-large"></div>
        <h1 class="login-title">牙科管理系统</h1>
        <p class="login-subtitle">专业的口腔门诊运营管理平台</p>
      </div>

      <el-form 
        ref="formRef" 
        :model="form" 
        class="login-form" 
        size="large"
        @submit.prevent="handleSubmit"
      >
        <el-form-item prop="username">
          <el-input 
            v-model.trim="form.username" 
            placeholder="请输入用户名" 
            :prefix-icon="User"
            autocomplete="username" 
            spellcheck="false" 
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password 
            autocomplete="current-password" 
          />
        </el-form-item>
        <el-button 
          :loading="loading" 
          type="primary" 
          class="login-button" 
          @click="handleSubmit"
        >
          登录系统
        </el-button>
      </el-form>

      <div class="login-footer">
        <p>演示账号请使用管理员分配的测试账户</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
})

async function handleSubmit() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    await authStore.login(form)
    await Promise.allSettled([authStore.loadCurrentUser(), authStore.loadPermissions()])
    ElMessage.success('登录成功')
    const redirect = (route.query.redirect as string) || '/dashboard'
    router.replace(redirect)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--app-bg);
  background-image: 
    radial-gradient(at 0% 0%, rgba(8, 145, 178, 0.1) 0px, transparent 50%),
    radial-gradient(at 100% 100%, rgba(34, 211, 238, 0.1) 0px, transparent 50%);
  padding: 24px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: white;
  border-radius: 24px;
  padding: 48px;
  box-shadow: 
    0 20px 25px -5px rgba(0, 0, 0, 0.05),
    0 8px 10px -6px rgba(0, 0, 0, 0.01);
  border: 1px solid var(--app-border);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.brand-mark-large {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: linear-gradient(135deg, var(--app-primary), var(--app-secondary));
  margin-bottom: 24px;
  box-shadow: 0 10px 20px rgba(8, 145, 178, 0.2);
}

.login-title {
  font-size: 28px;
  font-weight: 800;
  color: var(--app-text);
  margin: 0 0 8px 0;
  letter-spacing: -0.02em;
}

.login-subtitle {
  font-size: 14px;
  color: var(--app-muted);
  margin: 0;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  margin-top: 8px;
  transition: all 0.2s;
}

.login-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(8, 145, 178, 0.2);
}

.login-footer {
  margin-top: 32px;
  text-align: center;
  font-size: 12px;
  color: var(--app-muted);
}
</style>