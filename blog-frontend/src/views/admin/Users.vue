<template>
  <div class="users-container">
    <div class="page-header">
      <div class="page-title-section">
        <h2 class="page-title">用户管理</h2>
        <div class="page-subtitle">管理系统用户账号和权限</div>
      </div>
    </div>
    
    <!-- 筛选区域 -->
    <div class="filters card">
      <div class="filter-group">
        <el-input 
          v-model="keyword" 
          placeholder="搜索用户名或邮箱" 
          prefix-icon="Search" 
          clearable 
          class="search-input"
          @keyup.enter="fetch" 
        />
        <el-select v-model="roleFilter" placeholder="角色" clearable class="filter-select">
          <el-option label="普通用户" :value="0" /><el-option label="管理员" :value="1" />
        </el-select>
      </div>
      <div class="action-group">
        <el-button type="primary" @click="fetch" class="search-btn">搜索</el-button>
        <el-button @click="keyword='';roleFilter=null;fetch()" class="reset-btn">重置</el-button>
        <span class="count-tip">共 {{ total }} 人</span>
      </div>
    </div>
    
    <!-- 表格区域 -->
    <div class="card table-card">
      <el-table :data="users" border v-loading="loading" class="admin-table">
        <el-table-column label="用户" min-width="160">
          <template #default="{ row }">
            <el-tooltip :content="row.username" placement="top">
              <div class="user-cell">
                <el-avatar :src="row.avatar" :size="36" />
                <div>
                  <div class="user-name">{{ row.username }}</div>
                  <div class="user-email">{{ row.email }}</div>
                </div>
              </div>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="90">
          <template #default="{ row }">
            <el-tag :type="row.role==='ADMIN'?'danger':''" size="small">{{ row.role==='ADMIN'?'管理员':'用户' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status===1?'success':'info'" size="small">{{ row.status===1?'正常':'禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="bio" label="简介" min-width="200" show-overflow-tooltip />
        <el-table-column label="注册时间" width="160">
          <template #default="{ row }">{{ fmt(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link :type="row.status===1?'warning':'success'" @click="toggleStatus(row)">
              {{ row.status===1?'禁用':'启用' }}
            </el-button>
            <el-button link type="danger" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="page" :page-size="10" :total="total" layout="total, prev, pager, next" @current-change="fetch" />
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="editVisible" title="编辑用户" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="用户名"><el-input :value="editForm.username" disabled /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="editForm.email" /></el-form-item>
        <el-form-item label="简介"><el-input v-model="editForm.bio" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.role">
            <el-option label="普通用户" value="USER" /><el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status">
            <el-option label="正常" :value="1" /><el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible=false">取消</el-button>
        <el-button type="primary" @click="saveEdit" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { adminApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const users = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const keyword = ref('')
const statusFilter = ref(null)
const editVisible = ref(false)
const saving = ref(false)
const editForm = reactive({})
const fmt = d => d ? dayjs(d).format('YYYY-MM-DD HH:mm') : ''

async function fetch() {
  loading.value = true
  try {
    const res = await adminApi.users({ keyword: keyword.value||undefined, status: statusFilter.value, page: page.value-1, size: 10 })
    users.value = res.data.content
    total.value = res.data.totalElements
  } finally { loading.value = false }
}

function openEdit(row) {
  Object.assign(editForm, row)
  editVisible.value = true
}

async function saveEdit() {
  saving.value = true
  try {
    await adminApi.updateUser(editForm.id, editForm)
    ElMessage.success('更新成功')
    editVisible.value = false
    fetch()
  } finally { saving.value = false }
}

async function toggleStatus(row) {
  await adminApi.toggleUserStatus(row.id)
  ElMessage.success('操作成功')
  fetch()
}

async function del(id) {
  await ElMessageBox.confirm('确认删除该用户？', '警告', { type: 'warning' })
  await adminApi.deleteUser(id)
  ElMessage.success('删除成功')
  fetch()
}

onMounted(fetch)
</script>

<style scoped>
.users-container { padding: 0 20px; }

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

/* 用户信息样式 */
.user-cell { 
  display: flex; 
  align-items: center; 
  gap: 12px; 
}
.user-avatar {
  flex-shrink: 0;
  border-radius: 50%;
}
.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.user-name { 
  font-weight: 600; 
  font-size: 14px; 
  color: #303133;
}
.user-email { 
  font-size: 12px; 
  color: #909399; 
  line-height: 1.4;
}

/* 角色标签样式 */
.role-admin {
  color: #e6a23c;
  background: rgba(230, 162, 60, 0.1);
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}
.role-user {
  color: #67c23a;
  background: rgba(103, 194, 58, 0.1);
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

/* 状态标签样式 */
.status-active {
  color: #67c23a;
  background: rgba(103, 194, 58, 0.1);
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}
.status-inactive {
  color: #f56c6c;
  background: rgba(245, 108, 108, 0.1);
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}
</style>