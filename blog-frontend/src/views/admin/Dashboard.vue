<template>
  <div class="dashboard-container">
    <!-- 页面头部 -->
    <div class="dashboard-header">
      <div class="header-content">
        <h1 class="dashboard-title">数据概览</h1>
        <p class="dashboard-subtitle">系统运行状态和关键指标监控</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="$router.push('/')" class="back-btn">
          <el-icon><House /></el-icon>
          <span>返回前台</span>
        </el-button>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="metrics-section">
      <h2 class="section-title">核心指标</h2>
      <div class="metrics-grid">
        <div v-for="item in statItems" :key="item.label" class="metric-card" :style="{ '--metric-color': item.color }">
          <div class="metric-icon">
            <el-icon :size="28"><component :is="item.icon" /></el-icon>
          </div>
          <div class="metric-content">
            <div class="metric-value">{{ stats[item.key] ?? '0' }}</div>
            <div class="metric-label">{{ item.label }}</div>
          </div>
          <div class="metric-badge" :style="{ backgroundColor: item.color + '20', color: item.color }">
            <el-icon :size="12"><Top /></el-icon>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据统计区域 -->
    <div class="stats-section">
      <div class="stats-grid">
        <!-- AI审查统计 -->
        <div class="stats-card">
          <div class="stats-header">
            <h3 class="stats-title">AI评论审查</h3>
            <div class="stats-subtitle">智能审核系统运行情况</div>
          </div>
          <div class="stats-content">
            <div class="review-metrics">
              <div class="metric-item">
                <div class="metric-number" style="color:#67c23a">{{ stats.totalComments - (stats.aiBlockedComments||0) }}</div>
                <div class="metric-name">AI通过</div>
              </div>
              <div class="metric-item">
                <div class="metric-number" style="color:#f56c6c">{{ stats.aiBlockedComments || 0 }}</div>
                <div class="metric-name">AI拦截</div>
              </div>
              <div class="metric-item">
                <div class="metric-number" style="color:#e6a23c">{{ stats.pendingComments || 0 }}</div>
                <div class="metric-name">待审核</div>
              </div>
            </div>
            <el-progress
              :percentage="aiPassRate"
              :format="() => `${aiPassRate}% 通过率`"
              status="success"
              class="review-progress"
            />
          </div>
        </div>

        <!-- 快捷操作 -->
        <div class="stats-card">
          <div class="stats-header">
            <h3 class="stats-title">快捷操作</h3>
            <div class="stats-subtitle">快速访问管理功能</div>
          </div>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/admin/users')" class="quick-btn">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </el-button>
            <el-button type="success" @click="$router.push('/admin/articles')" class="quick-btn">
              <el-icon><Document /></el-icon>
              <span>文章管理</span>
            </el-button>
            <el-button type="warning" @click="$router.push('/admin/comments')" class="quick-btn">
              <el-icon><ChatLineRound /></el-icon>
              <span>评论审核</span>
            </el-button>
            <el-button type="info" @click="$router.push('/admin/configs')" class="quick-btn">
              <el-icon><Setting /></el-icon>
              <span>系统配置</span>
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { adminApi } from '@/api'
import { Top } from '@element-plus/icons-vue'

const stats = ref({})

const statItems = [
  { key: 'totalUsers', label: '注册用户', icon: 'User', color: '#409eff' },
  { key: 'totalArticles', label: '文章总数', icon: 'Document', color: '#67c23a' },
  { key: 'totalComments', label: '评论总数', icon: 'ChatLineRound', color: '#e6a23c' },
  { key: 'publishedArticles', label: '已发布文章', icon: 'Reading', color: '#9b59b6' }
]

const aiPassRate = computed(() => {
  const total = stats.value.totalComments || 0
  const blocked = stats.value.aiBlockedComments || 0
  return total === 0 ? 100 : Math.round(((total - blocked) / total) * 100)
})

onMounted(async () => {
  const res = await adminApi.dashboard()
  stats.value = res.data
})
</script>

<style scoped>
.dashboard-container { 
  padding: 0 24px 24px; 
  background: #f8fafc;
  min-height: 100vh;
}

/* 页面头部 */
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 32px 0 24px;
  margin-bottom: 24px;
  border-bottom: 1px solid #e2e8f0;
}

.header-content {
  flex: 1;
}

.dashboard-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
  color: #1e293b;
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.dashboard-subtitle {
  font-size: 16px;
  color: #64748b;
  line-height: 1.5;
}

.header-actions {
  flex-shrink: 0;
}

.back-btn {
  height: 44px !important;
  padding: 0 20px !important;
  font-weight: 500 !important;
  border-radius: 8px !important;
}

/* 核心指标区域 */
.metrics-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 20px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.metric-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  border: 1px solid #f1f5f9;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.metric-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.metric-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--metric-color);
}

.metric-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: rgba(59, 130, 246, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  color: var(--metric-color);
}

.metric-content {
  flex: 1;
}

.metric-value {
  font-size: 36px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1;
  margin-bottom: 8px;
}

.metric-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.metric-badge {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 24px;
  height: 24px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

/* 数据统计区域 */
.stats-section {
  margin-bottom: 32px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

.stats-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  border: 1px solid #f1f5f9;
  transition: all 0.3s ease;
}

.stats-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

.stats-header {
  margin-bottom: 20px;
}

.stats-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.stats-subtitle {
  font-size: 14px;
  color: #64748b;
}

/* AI审查统计 */
.review-metrics {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.metric-item {
  text-align: center;
}

.metric-number {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
  line-height: 1;
}

.metric-name {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.review-progress {
  margin-top: 0;
}

/* 快捷操作 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.quick-btn {
  height: 80px !important;
  display: flex !important;
  flex-direction: column !important;
  align-items: center !important;
  justify-content: center !important;
  gap: 8px !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  border-radius: 12px !important;
  transition: all 0.3s ease !important;
  border: none !important;
}

.quick-btn:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15) !important;
}

.quick-btn .el-icon {
  font-size: 24px !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard-container {
    padding: 0 16px 16px;
  }
  
  .dashboard-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .metrics-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .review-metrics {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .quick-actions {
    grid-template-columns: 1fr;
  }
}
</style>