<template>
  <div class="container">
    <div class="page-header">
      <h1>全部文章</h1>
      <p>探索优质技术内容，汲取开发经验</p>
    </div>

    <!-- 搜索过滤 -->
    <div class="filters card">
      <el-input
        v-model="filters.keyword"
        placeholder="搜索文章标题..."
        prefix-icon="Search"
        clearable
        @keyup.enter="search"
        style="width:280px"
      />
      <el-select v-model="filters.category" placeholder="选择分类" clearable style="width:150px">
        <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
      </el-select>
      <el-button type="primary" @click="search">搜索</el-button>
      <el-button @click="resetFilter">重置</el-button>
      <span class="total-count">共 {{ total }} 篇文章</span>
    </div>

    <el-row :gutter="24">
      <el-col :xs="24" :md="16">
        <div v-loading="loading">
          <ArticleCard
            v-for="article in articles"
            :key="article.id"
            :article="article"
            @click="$router.push(`/articles/${article.id}`)"
          />
          <el-empty v-if="!loading && articles.length === 0" description="暂无文章" />
        </div>
        <div class="pagination">
          <el-pagination
            v-model:current-page="page"
            :page-size="10"
            :total="total"
            layout="total, prev, pager, next"
            @current-change="fetchArticles"
          />
        </div>
      </el-col>

      <el-col :xs="24" :md="8">
        <div class="card sidebar-card">
          <h3 class="sidebar-title">文章分类</h3>
          <div class="cat-list">
            <div
              v-for="cat in categories"
              :key="cat"
              class="cat-item"
              :class="{ active: filters.category === cat }"
              @click="selectCategory(cat)"
            >
              <span>{{ cat }}</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { articleApi } from '@/api'
import ArticleCard from '@/components/ArticleCard.vue'

const route = useRoute()
const articles = ref([])
const categories = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const filters = ref({ keyword: '', category: route.query.category || '' })

async function fetchArticles() {
  loading.value = true
  try {
    const res = await articleApi.list({
      keyword: filters.value.keyword || undefined,
      category: filters.value.category || undefined,
      status: 1,
      page: page.value - 1,
      size: 10
    })
    articles.value = res.data.content
    total.value = res.data.totalElements
  } finally {
    loading.value = false
  }
}

function search() { page.value = 1; fetchArticles() }
function resetFilter() { filters.value = { keyword: '', category: '' }; search() }
function selectCategory(cat) {
  filters.value.category = filters.value.category === cat ? '' : cat
  search()
}

watch(() => route.query.category, val => {
  filters.value.category = val || ''
  fetchArticles()
})

onMounted(async () => {
  await fetchArticles()
  const res = await articleApi.categories()
  categories.value = res.data
})
</script>

<style scoped>
.container { max-width: 1200px; margin: 40px auto; padding: 0 24px; }
.page-header { text-align: center; margin-bottom: 32px; }
.page-header h1 { font-size: 32px; font-weight: 700; color: var(--text-primary); }
.page-header p { color: var(--text-hint); margin-top: 8px; }
.filters { display: flex; align-items: center; gap: 12px; padding: 16px 20px; margin-bottom: 24px; flex-wrap: wrap; }
.total-count { margin-left: auto; color: var(--text-hint); font-size: 14px; }
.pagination { margin-top: 24px; display: flex; justify-content: center; }
.sidebar-card { padding: 20px; }
.sidebar-title { font-size: 16px; font-weight: 600; margin-bottom: 16px; }
.cat-list { display: flex; flex-direction: column; gap: 4px; }
.cat-item { display: flex; align-items: center; justify-content: space-between; padding: 10px 12px;
  border-radius: 8px; cursor: pointer; transition: all 0.2s; color: var(--text-secondary); }
.cat-item:hover, .cat-item.active { background: var(--primary-light); color: var(--primary); }
</style>