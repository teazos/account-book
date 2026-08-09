<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <template #header><h2 style="margin:0">注册</h2></template>
      <el-form :model="form" label-width="60px" @keyup.enter="submit">
        <el-form-item label="邮箱"><el-input v-model="form.email" placeholder="请输入邮箱" /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.nickname" placeholder="请输入昵称（可选）" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" show-password placeholder="请输入密码（6-32位）" /></el-form-item>
        <el-form-item label="确认密码"><el-input v-model="form.confirm" type="password" show-password placeholder="请再次输入密码" /></el-form-item>
        <el-form-item><el-button type="primary" style="width:100%" :loading="loading" @click="submit">注册</el-button></el-form-item>
      </el-form>
      <div style="text-align:center;color:#909399">已有账号？<router-link to="/login">去登录</router-link></div>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/authStore'
const router = useRouter(); const auth = useAuthStore()
const form = reactive({ email: '', nickname: '', password: '', confirm: '' }); const loading = ref(false)
async function submit() {
  if (!form.email || !form.password) { ElMessage.warning('请输入邮箱和密码'); return }
  if (form.password.length < 6) { ElMessage.warning('密码长度至少6位'); return }
  if (form.password !== form.confirm) { ElMessage.warning('两次输入的密码不一致'); return }
  loading.value = true
  try {
    await auth.register({ email: form.email, password: form.password, nickname: form.nickname || undefined })
    ElMessage.success('注册成功')
    router.push('/books')
  } finally { loading.value = false }
}
</script>
<style scoped>
.auth-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: #f5f7fb; padding: 16px; }
.auth-card { width: min(380px, 92vw); }
</style>
