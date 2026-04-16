<template>
  <div class="articles-container">
    <div class="page-header">
      <div class="page-title-section">
        <h2 class="page-title">文章管理</h2>
        <div class="page-subtitle">管理博客文章内容和状态</div>
      </div>
      <el-button type="primary" @click="$router.push('/write')" class="create-btn">
        <el-icon><Plus /></el-icon>
        <span>写文章</span>
      </el-button>
    </div>
    
    <!-- 筛选区域 -->
    <div class="filters card">
      <div class="filter-group">
        <el-input 
          v-model="keyword" 
          placeholder="搜索文章标题" 
          prefix-icon="Search" 
          clearable 
          class="search-input"
          @keyup.enter="fetch" 
        />
        <el-select v-model="catFilter" placeholder="分类" clearable class="filter-select">
          <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
        </el-select>
        <el-select v-model="statusFilter" placeholder="状态" clearable class="filter-select">
          <el-option label="已发布" :value="1" /><el-option label="草稿" :value="0" />
        </el-select>
      </div>
      <div class="action-group">
        <el-button type="primary" @click="fetch" class="search-btn">搜索</el-button>
        <el-button @click="keyword='';catFilter=null;statusFilter=null;fetch()" class="reset-btn">重置</el-button>
        <span class="count-tip">共 {{ total }} 篇</span>
      </div>
    </div>
    
    <!-- 表格区域 -->
    <div class="card table-card">
      <el-table :data="articles" border v-loading="loading" class="admin-table">
        <el-table-column label="标题" min-width="200">
          <template #default="{ row }">
            <a :href="`/articles/${row.id}`" target="_blank" class="article-link">{{ row.title }}</a>
          </template>
        </el-table-column>
        <el-table-column label="作者" width="110">
          <template #default="{ row }">
            <el-tooltip :content="row.author?.username" placement="top">
              <div class="author-cell">
                <el-avatar :src="row.author?.avatar" :size="30" class="author-avatar" />
                <span class="author-name">{{ row.author?.username }}</span>
              </div>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status===1?'success':'info'" size="small">{{ row.status===1?'已发布':'草稿' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="浏览/点赞" width="100">
          <template #default="{ row }"><span class="small-text">{{ row.viewCount }}/{{ row.likeCount }}</span></template>
        </el-table-column>
        <el-table-column label="发布时间" width="160">
          <template #default="{ row }">{{ fmt(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link :type="row.status===1?'warning':'success'" @click="toggleStatus(row)">
              {{ row.status===1?'下架':'上架' }}
            </el-button>
            <el-button link type="danger" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="page" :page-size="10" :total="total" layout="total, prev, pager, next" @current-change="fetch" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi, articleApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const articles = ref([])
const categories = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const keyword = ref('')
const catFilter = ref(null)
const statusFilter = ref(null)
const fmt = d => d ? dayjs(d).format('YYYY-MM-DD HH:mm') : ''

async function fetch() {
  loading.value = true
  try {
    const res = await adminApi.articles({ keyword: keyword.value||undefined, category: catFilter.value||undefined, status: statusFilter.value, page: page.value-1, size: 10 })
    articles.value = res.data.content
    total.value = res.data.totalElements
  } finally { loading.value = false }
}

async function toggleStatus(row) {
  await adminApi.updateArticleStatus(row.id, row.status === 1 ? 0 : 1)
  ElMessage.success('操作成功')
  fetch()
}

async function del(id) {
  await ElMessageBox.confirm('确认删除该文章？', '警告', { type: 'warning' })
  await adminApi.deleteArticle(id)
  ElMessage.success('删除成功')
  fetch()
}

onMounted(async () => {
  const res = await articleApi.categories()
  categories.value = res.data
  fetch()
})
</script>

<style scoped>
.articles-container { padding: 0 20px; }

/* 页面头部 */
.page-header { 
  display: flex; 
  justify-content: space-between; 
  align-items: flex-start; 
  margin-bottom: 24px; 
}
.page-title-section { flex: 1; }
.page-title { 
  font-size: 24px; 
  font-weight: 700; 
  margin-bottom: 8px; 
  color: #303133; 
}
.page-subtitle {
  font-size: 14px;
  color: #909399;
  line-height: 1.5;
}
.create-btn {
  height: 40px !important;
  padding: 0 20px !important;
  font-weight: 500 !important;
}

/* 筛选区域 */
.filters { 
  padding: 24px; 
  margin-bottom: 24px; 
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}
.filter-group {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 16px;
}
.search-input { width: 300px !important; }
.filter-select { width: 140px !important; }
.action-group {
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: space-between;
}
.search-btn, .reset-btn {
  height: 36px !important;
  padding: 0 20px !important;
}
.count-tip { 
  color: #909399; 
  font-size: 14px; 
  font-weight: 500;
}

/* 表格区域 */
.table-card { 
  padding: 0; 
  overflow: hidden; 
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}
.pagination { 
  padding: 20px; 
  display: flex; 
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #ebeef5;
}

/* 表格内容样式 */
.article-link { 
  color: #303133 !important; 
  font-weight: 500; 
  text-decoration: none; 
  line-height: 1.5;
}
.article-link:hover { 
  color: #409eff !important; 
  text-decoration: underline;
}
.author-cell { 
  display: flex; 
  align-items: center; 
  gap: 8px; 
}
.author-avatar { 
  flex-shrink: 0; 
  border-radius: 50%;
}
.author-name { 
  flex: 1; 
  overflow: hidden; 
  text-overflow: ellipsis; 
  white-space: nowrap; 
  max-width: 80px;
  font-weight: 500;
}
.small-text { 
  font-size: 13px; 
  color: #909399; 
  line-height: 1.4;
}

/* 状态标签样式 */
.status-published {
  color: #67c23a;
  background: rgba(103, 194, 58, 0.1);
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}
.status-draft {
  color: #e6a23c;
  background: rgba(230, 162, 60, 0.1);
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}
</style>