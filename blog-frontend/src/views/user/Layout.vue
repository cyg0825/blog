<template>
  <div class="layout">
    <header class="header">
      <div class="header-inner">
        <router-link to="/" class="logo tech-btn">
          <el-icon size="24" :color="themeStore.isDark ? '#0ea5e9' : '#3b82f6'"><Edit /></el-icon>
          <span class="logo-text">技术博客</span>
        </router-link>
        <!-- 桌面端导航 -->
        <nav class="nav desktop-nav"> 
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/articles" class="nav-link">文章</router-link>
          <template v-if="authStore.isLoggedIn">
            <router-link to="/write" class="nav-link">写文章</router-link>
            <router-link to="/my-articles" class="nav-link">我的文章</router-link>
          </template>
        </nav>

        <!-- 移动端头像菜单 -->
        <div class="mobile-menu">
          <!-- 移动端菜单面板 -->
          <transition name="slide-down">
            <div v-show="showMobileMenu" class="mobile-menu-panel card">
              <!-- 导航菜单部分 -->
              <div class="menu-section">
                <div class="menu-header">
                  <span class="menu-title">导航</span>
                </div>
                <div class="menu-items">
                  <router-link 
                    to="/" 
                    class="menu-item"
                    @click="showMobileMenu = false"
                  >
                    <el-icon><HomeFilled /></el-icon>
                    <span>首页</span>
                  </router-link>
                  
                  <router-link 
                    to="/articles" 
                    class="menu-item"
                    @click="showMobileMenu = false"
                  >
                    <el-icon><Document /></el-icon>
                    <span>文章</span>
                  </router-link>
                  
                  <template v-if="authStore.isLoggedIn">
                    <router-link 
                      to="/write" 
                      class="menu-item"
                      @click="showMobileMenu = false"
                    >
                      <el-icon><Edit /></el-icon>
                      <span>写文章</span>
                    </router-link>
                    
                    <router-link 
                      to="/my-articles" 
                      class="menu-item"
                      @click="showMobileMenu = false"
                    >
                      <el-icon><Collection /></el-icon>
                      <span>我的文章</span>
                    </router-link>
                  </template>
                </div>
              </div>
              
              <!-- 个人信息部分 -->
              <div class="menu-section" v-if="authStore.isLoggedIn">
                <div class="menu-header">
                  <span class="menu-title">个人</span>
                </div>
                <div class="menu-items">
                  <router-link 
                    to="/profile" 
                    class="menu-item"
                    @click="showMobileMenu = false"
                  >
                    <el-icon><User /></el-icon>
                    <span>个人资料</span>
                  </router-link>
                  
                  <div 
                    v-if="authStore.isAdmin" 
                    class="menu-item"
                    @click="$router.push('/admin'); showMobileMenu = false"
                  >
                    <el-icon><Setting /></el-icon>
                    <span>管理后台</span>
                  </div>
                  
                  <div 
                    class="menu-item logout-item"
                    @click="handleLogout(); showMobileMenu = false"
                  >
                    <el-icon><SwitchButton /></el-icon>
                    <span>退出登录</span>
                  </div>
                </div>
              </div>
              
              <div class="menu-footer">
                <el-button @click="showMobileMenu = false" size="small" class="close-btn">
                  关闭
                </el-button>
              </div>
            </div>
          </transition>
        </div>
        <div class="header-right">
          <!-- 主题切换按钮 -->
          <el-button 
            @click="themeStore.toggleTheme" 
            :icon="themeStore.isDark ? 'Sunny' : 'Moon'"
            circle 
            class="theme-toggle-btn tech-btn"
          >
            <component :is="themeStore.isDark ? Sunny : Moon" />
          </el-button>
          
          <template v-if="authStore.isLoggedIn">
            <!-- 桌面端下拉菜单 -->
            <el-dropdown class="desktop-dropdown">
              <div class="user-info tech-btn">
                <el-avatar :src="authStore.user?.avatar" :size="34" />
                <span class="username">{{ authStore.user?.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/profile')">个人资料</el-dropdown-item>
                  <el-dropdown-item v-if="authStore.isAdmin" @click="$router.push('/admin')">管理后台</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            
            <!-- 移动端头像点击 -->
            <div class="mobile-avatar" @click="showMobileMenu = !showMobileMenu">
              <el-avatar :src="authStore.user?.avatar" :size="34" />
            </div>
          </template>
          <template v-else>
            <el-button @click="$router.push('/login')" class="tech-btn">登录</el-button>
            <el-button type="primary" @click="$router.push('/register')" class="tech-btn">注册</el-button>
          </template>
        </div>
      </div>
    </header>
    <main class="main">
      <router-view />
    </main>
    <footer class="footer">
      <div class="footer-content">
        <p>© 2026 技术博客平台. All Rights Reserved. | 分享技术，记录生活</p>
        <div class="footer-links">
          <a href="#" class="footer-link">关于我们</a>
          <a href="#" class="footer-link">隐私政策</a>
          <a href="#" class="footer-link">服务条款</a>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/store/auth'
import { useThemeStore } from '@/store/theme'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Sunny, Moon, HomeFilled, Document, Edit, Collection, User, Setting, SwitchButton } from '@element-plus/icons-vue'
import { ref } from 'vue'

const authStore = useAuthStore()
const themeStore = useThemeStore()
const router = useRouter()
const showMobileMenu = ref(false)

function handleLogout() {
  authStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}
</script>

<style scoped>
.layout { 
  display: flex; 
  flex-direction: column; 
  min-height: 100vh; 
  background: linear-gradient(135deg, var(--bg), var(--bg-secondary));
  position: relative;
  overflow: hidden;
}
.layout::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: transparent;
  pointer-events: none;
  z-index: -1;
}

.header {
  background: var(--glass-bg);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid var(--glass-border);
  position: fixed; 
  top: 0; 
  left: 0;
  right: 0;
  z-index: 1000;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 0 10px;
  height: 70px;
}
.header-inner {
  max-width: 1200px; 
  margin: 0 auto;
  padding: 0 24px; 
  height: 70px;
  display: flex; 
  align-items: center; 
  gap: 40px;
}
.logo {
  display: flex; 
  align-items: center; 
  gap: 10px;
  font-size: 22px; 
  font-weight: 700; 
  color: var(--primary);
  text-decoration: none;
  padding: 8px 12px;
  border-radius: var(--radius-small);
  transition: all 0.3s;
}
.logo:hover {
  background: rgba(14, 165, 233, 0.1);
  color: var(--primary);
}

.logo-text {
  transition: opacity 0.3s ease;
}
.nav { 
  display: flex; 
  gap: 30px; 
  flex: 1; 
}
.nav-link {
  color: var(--text-secondary); 
  font-size: 16px; 
  text-decoration: none;
  padding: 8px 0; 
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
  position: relative;
}
.nav-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--primary);
  transition: width 0.3s;
}
.nav-link:hover, .nav-link.router-link-exact-active { 
  color: var(--primary); 
}
.nav-link:hover::after, .nav-link.router-link-exact-active::after { 
  width: 100%; 
}
.header-right { 
  display: flex; 
  align-items: center; 
  gap: 16px; 
  margin-left: auto; 
}
.user-info { 
  display: flex; 
  align-items: center; 
  gap: 10px; 
  cursor: pointer; 
  padding: 6px 12px;
  border-radius: var(--radius-small);
  transition: all 0.3s;
}
.user-info:hover {
  background: rgba(14, 165, 233, 0.1);
}
.username { 
  font-size: 15px; 
  color: var(--text-primary); 
  font-weight: 500;
}
.main { 
  flex: 1; 
  padding: 100px 0 120px 0;  /* 增加上下边距以避免内容被固定头部和页脚遮挡 */
  background: linear-gradient(to bottom, var(--bg) 0%, var(--bg-secondary) 100%);
  margin-top: 70px;  /* 确保内容不会被固定头部遮挡 */
}
.footer {
  background: var(--glass-bg);
  backdrop-filter: blur(10px);
  border-top: 1px solid var(--glass-border);
  text-align: center; 
  padding: 40px 20px 20px;
  color: var(--text-hint);
  font-size: 14px;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}
.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}
.footer-links {
  display: flex;
  gap: 20px;
  margin-top: 10px;
}
.footer-link {
  color: var(--text-hint);
  text-decoration: none;
  transition: color 0.3s;
}
.footer-link:hover {
  color: var(--primary);
}

/* 移动端样式 */
@media (max-width: 768px) {
  .desktop-nav {
    display: none;
  }
  
  .desktop-dropdown {
    display: none;
  }
  
  .mobile-menu {
    display: block;
  }
  
  .mobile-avatar {
    display: block;
    cursor: pointer;
    margin-left: 12px;
  }
  
  .header-inner {
    padding: 0 16px;
    gap: 20px;
  }
  
  .logo {
    font-size: 18px;
  }
  
  .logo-text {
    opacity: 0;
    width: 0;
    overflow: hidden;
  }
  
  .header-right {
    gap: 12px;
  }
}

@media (min-width: 769px) {
  .mobile-menu {
    display: none;
  }
  
  .mobile-avatar {
    display: none;
  }
}

/* 移动端菜单面板 */
.mobile-menu-panel {
  position: absolute;
  top: 100%;
  right: 16px;
  width: 280px;
  max-width: 90vw;
  margin-top: 8px;
  z-index: 1001;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  background: var(--card-bg);
  color: var(--text-primary);
}

.menu-section {
  border-bottom: 1px solid var(--border);
}

.menu-section:last-child {
  border-bottom: none;
}

.menu-header {
  padding: 12px 20px;
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.menu-title {
  font-weight: 600;
  font-size: 14px;
  color: var(--text-primary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.menu-items {
  padding: 4px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  color: var(--text-primary);
  text-decoration: none;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
  cursor: pointer;
}

.menu-item:hover {
  background: var(--bg-secondary);
  color: var(--primary);
  border-left-color: var(--primary);
}

.menu-item.router-link-exact-active {
  background: var(--primary-light) !important;
  color: var(--primary) !important;
  border-left-color: var(--primary) !important;
}

.menu-item .el-icon {
  font-size: 18px;
  width: 24px;
  color: inherit;
}

.logout-item {
  color: var(--danger);
}

.logout-item:hover {
  color: var(--danger);
  border-left-color: var(--danger);
}

.menu-footer {
  padding: 12px 20px;
  text-align: center;
  border-top: 1px solid var(--border);
  background: var(--bg-secondary);
}

/* 菜单动画 */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}

.slide-down-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

</style>