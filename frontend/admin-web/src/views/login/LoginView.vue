<template>
  <div style="min-height: 100vh; display: grid; place-items: center; padding: 24px;">
    <div class="page-card" style="width: 420px; max-width: 100%;">
      <div style="padding: 36px 32px;">
        <div style="display: grid; gap: 10px; margin-bottom: 24px;">
          <div class="page-title" style="font-size: 30px;">牙科系统管理后台</div>
          <div class="page-desc">使用现有 Spring Boot 后端接口，面向门诊运营与接诊流程的 PC Web 管理端。</div>
        </div>

        <el-form ref="formRef" :model="form" label-position="top" @submit.prevent="handleSubmit">
          <el-form-item label="用户名" prop="username">
            <el-input v-model.trim="form.username" autocomplete="username" spellcheck="false" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" show-password autocomplete="current-password" />
          </el-form-item>
          <el-form-item>
            <el-button :loading="loading" type="primary" style="width: 100%;" @click="handleSubmit">登录</el-button>
          </el-form-item>
        </el-form>

        <div class="page-desc" style="margin-top: 12px;">
          演示账号请使用管理员分配的测试账户。
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
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
