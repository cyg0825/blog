<template>
  <div class="auth-page">
    <div class="auth-bg-grid"></div>
    <div class="auth-bg-glow glow-1"></div>
    <div class="auth-bg-glow glow-2"></div>

    <div class="theme-toggle-wrap">
      <el-button
        @click="themeStore.toggleTheme"
        :icon="themeStore.isDark ? 'Sunny' : 'Moon'"
        circle
        class="theme-btn tech-btn"
      />
    </div>

    <div class="auth-container">
      <div class="auth-brand" @click="$router.push('/')">
        <el-icon size="28" :color="themeStore.isDark ? '#0ea5e9' : '#3b82f6'"><Edit /></el-icon>
        <span>技术博客</span>
      </div>

      <div class="auth-card">
        <div class="auth-header">
          <h1>欢迎回来</h1>
          <p>登录账号，继续你的技术之旅</p>
        </div>

        <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="auth-submit tech-btn"
          >
            登录
          </el-button>
        </el-form>

        <div class="demo-accounts">
          <span class="demo-label">演示账号</span>
          <div class="demo-btns">
            <el-button size="small" @click="fillDemo('admin','admin123')">管理员 admin</el-button>
            <el-button size="small" @click="fillDemo('cc','admin123')">用户 cc</el-button>
          </div>
        </div>

        <div class="auth-footer">
          <span>没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
      </div>

      <div class="auth-copyright">
        <p>© 2026 技术博客平台 · 分享技术，记录生活</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { useThemeStore } from '@/store/theme'
import { ElMessage } from 'element-plus'
import { User, Lock, Edit } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const themeStore = useThemeStore()
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
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg);
  position: relative;
  overflow: hidden;
  padding: 24px;
}

.auth-bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(var(--border) 1px, transparent 1px),
    linear-gradient(90deg, var(--border) 1px, transparent 1px);
  background-size: 40px 40px;
  opacity: 0.3;
  pointer-events: none;
  mask-image: radial-gradient(ellipse at center, black 20%, transparent 70%);
  -webkit-mask-image: radial-gradient(ellipse at center, black 20%, transparent 70%);
}

.auth-bg-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(120px);
  pointer-events: none;
}

.glow-1 {
  width: 500px;
  height: 500px;
  background: var(--primary);
  opacity: 0.15;
  top: -150px;
  left: -100px;
  animation: float 8s ease-in-out infinite;
}

.glow-2 {
  width: 400px;
  height: 400px;
  background: var(--secondary);
  opacity: 0.12;
  bottom: -100px;
  right: -100px;
  animation: float 10s ease-in-out infinite reverse;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(40px, -30px); }
}

.theme-toggle-wrap {
  position: absolute;
  top: 24px;
  right: 24px;
  z-index: 10;
}

.theme-btn {
  background: var(--card-bg);
  border: 1px solid var(--border);
  color: var(--text-primary);
}

.auth-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 440px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.auth-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 22px;
  font-weight: 700;
  color: var(--primary);
  cursor: pointer;
  margin-bottom: 32px;
  transition: all 0.3s;
}

.auth-brand:hover {
  opacity: 0.85;
}

.auth-card {
  width: 100%;
  padding: 44px 40px;
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  box-shadow: var(--shadow-hover);
  position: relative;
}

.auth-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--gradient-primary);
  border-radius: var(--radius) var(--radius) 0 0;
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.auth-header h1 {
  font-size: 26px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.auth-header p {
  font-size: 14px;
  color: var(--text-secondary);
}

.auth-submit {
  width: 100%;
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
  border-radius: var(--radius-small);
}

.auth-submit.el-button--primary {
  background: var(--gradient-primary);
  border: none;
}

.demo-accounts {
  margin-top: 20px;
  padding: 14px;
  background: var(--bg-secondary);
  border: 1px dashed var(--border);
  border-radius: var(--radius-small);
  text-align: center;
}

.demo-label {
  font-size: 12px;
  color: var(--text-hint);
  margin-bottom: 10px;
  display: block;
}

.demo-btns {
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: wrap;
}

.demo-btns .el-button {
  font-size: 12px;
}

.auth-footer {
  text-align: center;
  margin-top: 24px;
  color: var(--text-secondary);
  font-size: 14px;
}

.auth-footer a {
  color: var(--primary);
  text-decoration: none;
  font-weight: 500;
  margin-left: 4px;
  transition: opacity 0.3s;
}

.auth-footer a:hover {
  opacity: 0.8;
}

.auth-copyright {
  margin-top: 32px;
  text-align: center;
  color: var(--text-hint);
  font-size: 12px;
}

@media (max-width: 480px) {
  .auth-card {
    padding: 32px 24px;
  }

  .auth-header h1 {
    font-size: 22px;
  }
}
</style>