<template>
  <div class="container">
    <div class="page-header">
      <h1>我的文章</h1>
      <el-button type="primary" @click="$router.push('/write')">
        <el-icon><Plus /></el-icon> 写新文章
      </el-button>
    </div>

    <div class="filters card">
      <el-input v-model="keyword" placeholder="搜索我的文章" prefix-icon="Search" clearable style="width:250px" @keyup.enter="fetch" />
      <el-select v-model="statusFilter" placeholder="状态" clearable style="width:120px">
        <el-option label="已发布" :value="1" />
        <el-option label="草稿" :value="0" />
      </el-select>
      <el-button type="primary" @click="fetch">搜索</el-button>
    </div>

    <div v-loading="loading">
      <el-table :data="articles" stripe style="width:100%" border>
        <el-table-column label="标题" min-width="200">
          <template #default="{ row }">
            <router-link :to="`/articles/${row.id}`" class="article-link">{{ row.title }}</router-link>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status===1?'success':'info'" size="small">
              {{ row.status===1?'已发布':'草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="浏览/点赞" width="110">
          <template #default="{ row }">
            <span style="color:#909399;font-size:13px">{{ row.viewCount }} / {{ row.likeCount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/write/${row.id}`)">编辑</el-button>
            <el-button link type="danger" @click="confirmDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          :page-size="10"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetch"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { articleApi } from '@/api'
import { useAuthStore } from '@/store/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const authStore = useAuthStore()
const articles = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const keyword = ref('')
const statusFilter = ref(null)

const formatDate = d => d ? dayjs(d).format('YYYY-MM-DD HH:mm') : ''

async function fetch() {
  loading.value = true
  try {
    const res = await articleApi.list({
      keyword: keyword.value || undefined,
      status: statusFilter.value,
      authorId: authStore.user?.id,
      page: page.value - 1,
      size: 10
    })
    articles.value = res.data.content
    total.value = res.data.totalElements
  } finally {
    loading.value = false
  }
}

async function confirmDelete(id) {
  try {
    await ElMessageBox.confirm('确认删除此文章？删除后不可恢复！', '警告', { type: 'warning' })
  } catch {
    return // 用户取消
  }
  try {
    await articleApi.delete(id)
    ElMessage.success('删除成功')
    fetch()
  } catch (e) {
    ElMessage.error(e.message || '删除失败')
  }
}

onMounted(fetch)
</script>

<style scoped>
.container { max-width: 1200px; margin: 40px auto; padding: 0 24px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-header h1 { font-size: 26px; font-weight: 700; }
.filters { display: flex; gap: 12px; padding: 16px 20px; margin-bottom: 24px; flex-wrap: wrap; }
.article-link { color: var(--text-primary); font-weight: 500; }
.article-link:hover { color: var(--primary); }
.pagination { margin-top: 24px; display: flex; justify-content: flex-end; }
</style>