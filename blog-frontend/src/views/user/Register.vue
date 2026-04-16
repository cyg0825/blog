<template>
  <div class="auth-page">
    <div class="auth-card card">
      <div class="auth-header">
        <el-icon size="40" color="#67c23a"><UserFilled /></el-icon>
        <h1>加入我们</h1>
        <p>创建你的技术博客账号</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名 (3-20位)" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="邮箱地址" size="large" prefix-icon="Message" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码 (至少6位)"
            size="large" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码"
            size="large" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-button type="primary" size="large" :loading="loading" @click="handleRegister" style="width:100%">
          注册
        </el-button>
      </el-form>
      <div class="auth-footer">
        已有账号？<router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', email: '', password: '', confirmPassword: '' })

const rules = {
  username: [{ required: true, min: 3, max: 20, message: '用户名3-20位' }],
  email: [{ type: 'email', message: '请输入正确邮箱' }],
  password: [{ required: true, min: 6, message: '密码至少6位' }],
  confirmPassword: [{
    validator: (rule, value, callback) => {
      value !== form.password ? callback(new Error('两次密码不一致')) : callback()
    }
  }]
}

async function handleRegister() {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  loading.value = true
  try {
    await authApi.register({ username: form.username, email: form.email, password: form.password })
    ElMessage.success('注册成功，请登录！')
    router.push('/login')
  } catch (e) {
    ElMessage.error(e.message || '注册失败，请检查输入信息')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh; display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); padding: 24px;
}
.auth-card { width: 100%; max-width: 420px; padding: 40px; }
.auth-header { text-align: center; margin-bottom: 32px; }
.auth-header h1 { font-size: 26px; font-weight: 700; margin: 12px 0 8px; }
.auth-header p { color: var(--text-hint); }
.el-form-item { margin-bottom: 20px; }
.auth-footer { text-align: center; margin-top: 20px; color: var(--text-secondary); font-size: 14px; }
.auth-footer a { color: var(--primary); }
</style>