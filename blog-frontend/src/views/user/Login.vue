<template>
  <div class="auth-page">
    <div class="auth-card card">
      <div class="auth-header">
        <el-icon size="40" color="#409eff"><Edit /></el-icon>
        <h1>欢迎回来</h1>
        <p>登录技术博客平台</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large"
            prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-button type="primary" size="large" :loading="loading" @click="handleLogin" style="width:100%">
          登录
        </el-button>
      </el-form>
      <div class="demo-accounts">
        <p>演示账号：</p>
        <el-button size="small" @click="fillDemo('admin','admin123')">管理员 admin</el-button>
        <el-button size="small" @click="fillDemo('zhangwei','admin123')">用户 zhangwei</el-button>
      </div>
      <div class="auth-footer">
        没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }]
}

function fillDemo(username, password) {
  form.username = username
  form.password = password
}

async function handleLogin() {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  loading.value = true
  try {
    await authStore.login(form)
    ElMessage.success('登录成功！')
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    ElMessage.error(e.message || '用户名或密码错误')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh; display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 24px;
}
.auth-card { width: 100%; max-width: 420px; padding: 40px; }
.auth-header { text-align: center; margin-bottom: 32px; }
.auth-header h1 { font-size: 26px; font-weight: 700; margin: 12px 0 8px; }

.el-form-item { margin-bottom: 20px; }
.demo-accounts { margin-top: 16px; padding: 12px; background: #f5f7fa; border-radius: 8px; }
.auth-header p { color: var(--text-hint); }
.demo-accounts p { font-size: 12px; color: var(--text-hint); margin-bottom: 8px; }
.auth-footer { text-align: center; margin-top: 20px; color: var(--text-secondary); font-size: 14px; }
.auth-footer a { color: var(--primary); }
</style>