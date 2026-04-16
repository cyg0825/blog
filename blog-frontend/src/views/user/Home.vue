<template>
  <div class="home-container">
    <!-- Hero Section with Tech Aesthetics -->
    <section class="hero-section">
      <div class="hero-content-wrapper">
        <div class="hero-content">
          <h1 class="hero-title glow-effect">探索技术，分享知识</h1>
          <p class="hero-subtitle">汇聚优质技术文章，记录开发历程，连接开发者社区</p>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="$router.push('/articles')" class="tech-btn hero-btn">
              <el-icon><Reading /></el-icon> 浏览文章
            </el-button>
            <el-button size="large" @click="$router.push('/write')" v-if="authStore.isLoggedIn" class="tech-btn hero-btn">
              <el-icon><Edit /></el-icon> 写文章
            </el-button>
          </div>
        </div>
        <div class="hero-stats">
          <div class="stat-card">
            <div class="stat-number">{{ stats.articles }}</div>
            <div class="stat-label">篇文章</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">{{ stats.users }}</div>
            <div class="stat-label">位作者</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">{{ stats.comments }}</div>
            <div class="stat-label">条评论</div>
          </div>
        </div>
      </div>
      <div class="hero-background">
        <div class="grid-lines"></div>
        <div class="floating-elements">
          <div class="floating-element elem-1"></div>
          <div class="floating-element elem-2"></div>
          <div class="floating-element elem-3"></div>
        </div>
      </div>
    </section>

    <div class="container">
      <el-row :gutter="32">
        <!-- 最新文章 -->
        <el-col :xs="24" :md="16">
          <div class="section-header">
            <div class="section-icon">
              <el-icon><Document /></el-icon>
            </div>
            <h2 class="section-title">最新文章</h2>
          </div>
          <div v-loading="loading">
            <ArticleCard
              v-for="article in articles"
              :key="article.id"
              :article="article"
              @click="$router.push(`/articles/${article.id}`)"
            />
            <el-empty v-if="!loading && articles.length === 0" description="暂无文章" class="empty-state"/>
          </div>
          <div class="pagination" v-if="total > pageSize">
            <el-pagination
              v-model:current-page="page"
              :page-size="pageSize"
              :total="total"
              layout="prev, pager, next"
              @current-change="fetchArticles"
            />
          </div>
        </el-col>

        <!-- 侧边栏 -->
        <el-col :xs="24" :md="8">
          <!-- 热门文章 -->
          <div class="card sidebar-card">
            <div class="section-header">
              <div class="section-icon">
                <el-icon><DataLine /></el-icon>
              </div>
              <h3 class="section-title">热门文章</h3>
            </div>
            <div class="hot-list">
              <div
                v-for="(a, i) in hotArticles"
                :key="a.id"
                class="hot-item"
                @click="$router.push(`/articles/${a.id}`)"
              >
                <span class="hot-rank" :class="`rank-${i+1}`">{{ i + 1 }}</span>
                <span class="hot-title">{{ a.title }}</span>
                <span class="hot-views">{{ a.viewCount }}</span>
              </div>
            </div>
          </div>

          <!-- 分类 -->
          <div class="card sidebar-card">
            <div class="section-header">
              <div class="section-icon">
                <el-icon><Collection /></el-icon>
              </div>
              <h3 class="section-title">文章分类</h3>
            </div>
            <div class="category-grid">
              <el-tag
                v-for="cat in categories"
                :key="cat"
                class="category-tag tech-btn"
                @click="$router.push(`/articles?category=${cat}`)"
                style="cursor:pointer"
              >{{ cat }}</el-tag>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { articleApi } from '@/api'
import request from '@/utils/request'
import { useAuthStore } from '@/store/auth'
import ArticleCard from '@/components/ArticleCard.vue'

const authStore = useAuthStore()
const articles = ref([])
const hotArticles = ref([])
const categories = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(8)
const total = ref(0)
const stats = ref({ articles: 0, users: 0, comments: 0 })

async function fetchArticles() {
  loading.value = true
  try {
    const res = await articleApi.list({ status: 1, page: page.value - 1, size: pageSize.value })
    articles.value = res.data.content
    total.value = res.data.totalElements
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await fetchArticles()
  const [hotRes, catRes] = await Promise.all([
    articleApi.hot(),
    articleApi.categories()
  ])
  hotArticles.value = hotRes.data
  categories.value = catRes.data

  try {
    const statsRes = await request.get('/stats')
    stats.value = {
      articles: statsRes.data.totalArticles,
      users: statsRes.data.totalUsers,
      comments: statsRes.data.totalComments
    }
  } catch {}
})
</script>

<style scoped>
.home-container {
  position: relative;
  overflow: hidden;
  background: var(--bg);
}

.hero-section {
  position: relative;
  padding: 80px 0 60px;
  margin-bottom: 40px;
  background: var(--bg-secondary);
  border-radius: 0 0 30px 30px;
  box-shadow: var(--shadow);
}

.hero-background {
  display: none;
}

.hero-content-wrapper {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.hero-content {
  text-align: center;
  margin-bottom: 40px;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 800;
  margin-bottom: 20px;
  color: var(--text-primary);
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 1.2rem;
  color: var(--text-secondary);
  margin-bottom: 30px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.hero-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 40px;
}

.hero-btn {
  padding: 14px 32px;
  font-size: 1.1rem;
  font-weight: 600;
  border-radius: 50px;
  transition: all 0.3s;
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-top: 30px;
}

.stat-card {
  text-align: center;
  padding: 20px;
  border-radius: var(--radius);
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(10px);
  min-width: 120px;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--primary);
  margin-bottom: 5px;
}

.stat-label {
  font-size: 0.9rem;
  color: var(--text-hint);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.section-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.section-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.sidebar-card {
  margin-bottom: 30px;
  padding: 24px;
}

.hot-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hot-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: var(--radius-small);
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.hot-item:hover {
  background: var(--card-bg-hover);
  border-color: var(--primary);
  transform: translateX(5px);
}

.hot-rank {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  font-size: 0.8rem;
  font-weight: 600;
  color: white;
}

.hot-rank.rank-1 { background: var(--warning); }
.hot-rank.rank-2 { background: var(--text-hint); }
.hot-rank.rank-3 { background: var(--danger); }
.hot-rank:not(.rank-1):not(.rank-2):not(.rank-3) { 
  background: var(--border); 
}

.hot-title {
  flex: 1;
  font-size: 0.95rem;
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-views {
  font-size: 0.85rem;
  color: var(--text-hint);
  background: rgba(14, 165, 233, 0.1);
  padding: 4px 10px;
  border-radius: 20px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 12px;
}

.category-tag {
  padding: 10px 16px;
  border-radius: 50px;
  background: var(--card-bg);
  border: 1px solid var(--border);
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
}

.category-tag:hover {
  background: var(--card-bg-hover);
  border-color: var(--primary);
  transform: translateY(-2px);
  box-shadow: var(--shadow-hover);
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

.empty-state {
  margin-top: 40px;
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .hero-stats {
    flex-wrap: wrap;
    gap: 20px;
  }
  
  .stat-card {
    min-width: 100px;
    padding: 15px;
  }
  
  .stat-number {
    font-size: 2rem;
  }
}
</style>