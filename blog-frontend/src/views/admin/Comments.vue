<template>
  <div>
    <div class="page-header"><h2>评论管理</h2></div>
    <div class="filters card">
      <el-input v-model="keyword" placeholder="搜索评论内容" prefix-icon="Search" clearable style="width:250px" @keyup.enter="fetch" />
      <el-select v-model="statusFilter" placeholder="状态" clearable style="width:130px">
        <el-option label="正常" :value="1" /><el-option label="待审核" :value="0" /><el-option label="已拦截" :value="2" />
      </el-select>
      <el-button type="primary" @click="fetch">搜索</el-button>
      <el-button @click="keyword='';statusFilter=null;fetch()">重置</el-button>
      <span class="count-tip">共 {{ total }} 条</span>
    </div>
    <div class="card table-card">
      <el-table :data="comments" border v-loading="loading" class="admin-table">
        <el-table-column label="内容" min-width="220">
          <template #default="{ row }">
            <div class="comment-content-cell">{{ row.content }}</div>
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
        <el-table-column label="文章" width="160" show-overflow-tooltip>
          <template #default="{ row }">
            <a :href="`/articles/${row.articleId}`" target="_blank" class="article-link">{{ row.articleTitle }}</a>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status===1?'success':row.status===2?'danger':'warning'" size="small">
              {{ row.status===1?'正常':row.status===2?'已拦截':'待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="AI审查" width="120">
          <template #default="{ row }">
            <el-tooltip :content="row.aiReviewReason||'未审查'" placement="top">
              <el-tag v-if="row.aiReviewResult===1" type="success" size="small">AI 通过</el-tag>
              <el-tag v-else-if="row.aiReviewResult===0" type="danger" size="small"><el-icon><Close /></el-icon> 拦截</el-tag>
              <el-tag v-else type="info" size="small">未审查</el-tag>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="时间" width="160">
          <template #default="{ row }">{{ fmt(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status!==1" link type="success" @click="updateStatus(row.id,1)">通过</el-button>
            <el-button v-if="row.status===1" link type="warning" @click="updateStatus(row.id,2)">拦截</el-button>
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
import { adminApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const comments = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const keyword = ref('')
const statusFilter = ref(null)
const fmt = d => d ? dayjs(d).format('YYYY-MM-DD HH:mm') : ''

async function fetch() {
  loading.value = true
  try {
    const res = await adminApi.comments({ keyword: keyword.value||undefined, status: statusFilter.value, page: page.value-1, size: 10 })
    comments.value = res.data.content
    total.value = res.data.totalElements
  } finally { loading.value = false }
}

async function updateStatus(id, status) {
  await adminApi.updateCommentStatus(id, status)
  ElMessage.success('操作成功')
  fetch()
}

async function del(id) {
  await ElMessageBox.confirm('确认删除该评论？', '警告', { type: 'warning' })
  await adminApi.deleteComment(id)
  ElMessage.success('删除成功')
  fetch()
}

onMounted(fetch)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #303133; }
.filters { 
  display: flex; gap: 12px; padding: 16px 20px; margin-bottom: 16px; 
  flex-wrap: wrap; align-items: center; background: white; 
}
.count-tip { margin-left: auto; color: #909399; font-size: 14px; }
.table-card { padding: 0; overflow: hidden; background: white; }
.pagination { padding: 16px 20px; display: flex; justify-content: flex-end; }
.comment-content-cell { font-size: 13px; line-height: 1.5; max-height: 60px; overflow: hidden; text-overflow: ellipsis; }
.author-cell { display: flex; align-items: center; gap: 6px; }
.author-avatar { flex-shrink: 0; }
.author-name { 
  flex: 1; 
  overflow: hidden; 
  text-overflow: ellipsis; 
  white-space: nowrap; 
  max-width: 60px;
}
.article-link { color: #409eff; font-size: 13px; }
</style>