<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <template #header><h2 style="margin:0">登录</h2></template>
      <el-form :model="form" label-width="60px" @keyup.enter="submit">
        <el-form-item label="邮箱"><el-input v-model="form.email" placeholder="请输入邮箱" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" show-password placeholder="请输入密码" /></el-form-item>
        <el-form-item><el-button type="primary" style="width:100%" :loading="loading" @click="submit">登录</el-button></el-form-item>
      </el-form>
      <div style="text-align:center;color:#909399">还没有账号？<router-link to="/register">去注册</router-link></div>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/authStore'
const route = useRoute(); const router = useRouter(); const auth = useAuthStore()
const form = reactive({ email: '', password: '' }); const loading = ref(false)
async function submit() {
  if (!form.email || !form.password) { ElMessage.warning('请输入邮箱和密码'); return }
  loading.value = true
  try {
    await auth.login({ email: form.email, password: form.password })
    ElMessage.success('登录成功')
    router.push(String(route.query.redirect || '/books'))
  } finally { loading.value = false }
}
</script>
<style scoped>
.auth-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: #f5f7fb; padding: 16px; }
.auth-card { width: min(380px, 92vw); }
</style>
