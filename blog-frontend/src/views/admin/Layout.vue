<template>
  <div class="admin-layout">
    <!-- 侧边栏遮罩层 -->
    <div v-if="showSidebar" class="sidebar-mask" @click="showSidebar = false"></div>
    
    <!-- 侧边栏 -->
    <div class="sidebar" :class="{ 'sidebar-mobile': showSidebar }">
      <div class="sidebar-header">
        <div class="sidebar-logo">
          <el-icon size="24" color="#fff"><Setting /></el-icon>
          <span>管理后台</span>
        </div>
        <!-- 移动端关闭按钮 -->
        <el-button v-if="showSidebar" @click="showSidebar = false" circle class="close-btn">
          <el-icon><Close /></el-icon>
        </el-button>
      </div>
      <nav class="sidebar-nav">
        <router-link v-for="item in navItems" :key="item.path" :to="item.path" class="nav-item" @click="closeMobileSidebar">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <el-avatar :src="authStore.user?.avatar" :size="32" />
        <span>{{ authStore.user?.username }}</span>
        <el-button link @click="handleLogout" style="color:#ccc;margin-left:auto"><el-icon><SwitchButton /></el-icon></el-button>
      </div>
    </div>
    
    <div class="main">
      <div class="topbar">
        <!-- 移动端汉堡菜单按钮 - 集成在面包屑中 -->
        <div class="topbar-content">
          <el-button 
            @click="showSidebar = !showSidebar" 
            circle 
            class="menu-btn"
            v-if="isMobile"
          >
            <el-icon><Menu /></el-icon>
          </el-button>
          <el-breadcrumb>
            <el-breadcrumb-item :to="{ path: '/' }">前台</el-breadcrumb-item>
            <el-breadcrumb-item>管理后台</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </div>
      <div class="content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/store/auth'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Menu, Close } from '@element-plus/icons-vue'
import { ref, onMounted, onUnmounted } from 'vue'

const authStore = useAuthStore()
const router = useRouter()
const showSidebar = ref(false)
const isMobile = ref(false)

const navItems = [
  { path: '/admin/dashboard', label: '数据概览', icon: 'DataAnalysis' },
  { path: '/admin/users', label: '用户管理', icon: 'User' },
  { path: '/admin/articles', label: '文章管理', icon: 'Document' },
  { path: '/admin/comments', label: '评论管理', icon: 'ChatLineRound' },
  { path: '/admin/configs', label: '系统配置', icon: 'Setting' }
]

function handleLogout() {
  authStore.logout()
  ElMessage.success('已退出')
  router.push('/login')
}

function closeMobileSidebar() {
  showSidebar.value = false
}

function checkMobile() {
  isMobile.value = window.innerWidth <= 768
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<style scoped>
.admin-layout { display: flex; height: 100vh; overflow: hidden; position: relative; }

/* 顶部导航栏 */
.topbar {
  padding: 14px 24px;
  background: white;
  border-bottom: 1px solid #ebeef5;
}

.topbar-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.menu-btn {
  background: white !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15) !important;
}

/* 侧边栏遮罩 */
.sidebar-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
}

/* 侧边栏 */
.sidebar {
  width: 220px; flex-shrink: 0;
  background: linear-gradient(180deg, #1a1a2e 0%, #5b71b6 100%);
  display: flex; flex-direction: column; overflow-y: auto;
  transition: transform 0.3s ease;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid rgba(142, 140, 140, 0.1);
}

.sidebar-logo {
  display: flex; align-items: center; gap: 10px; padding: 24px 20px;
  font-size: 18px; font-weight: 700; color: white;
  flex: 1;
}

.close-btn {
  display: none;
  margin-right: 16px;
  color: white !important;
}

.sidebar-nav { flex: 1; padding: 12px 0; }
.nav-item {
  display: flex; align-items: center; gap: 10px; padding: 12px 20px;
  color: #a0aec0; text-decoration: none; transition: all 0.2s;
  font-size: 14px;
}
.nav-item:hover { color: white; background: rgba(255,255,255,0.08); }
.nav-item.router-link-active { color: white; background: rgba(193, 214, 237, 0.2); border-right: 3px solid #acc6e4; }
.sidebar-footer {
  display: flex; align-items: center; gap: 8px; padding: 16px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1); color: #e6eef6; font-size: 14px;
}

.main { flex: 1; display: flex; flex-direction: column; overflow: hidden; background: white; }
.content { flex: 1; overflow-y: auto; padding: 24px; background: #ffffff; }

/* 移动端响应式 */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    top: 0;
    left: 0;
    bottom: 0;
    z-index: 1001;
    transform: translateX(-100%);
  }
  
  .sidebar.sidebar-mobile {
    transform: translateX(0);
  }
  
  .close-btn {
    display: block;
  }
  
  .main {
    margin-left: 0;
  }
}

@media (min-width: 769px) {
  .sidebar-mask {
    display: none;
  }
  
  .menu-btn {
    display: none;
  }
}

</style>