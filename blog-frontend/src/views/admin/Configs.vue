<template>
  <div>
    <div class="page-header">
      <h2>系统配置</h2>
      <el-button type="primary" @click="openAdd"><el-icon><Plus /></el-icon> 新增配置</el-button>
    </div>
    <div class="card table-card">
      <el-table :data="configs" border v-loading="loading" class="admin-table">
        <el-table-column prop="configKey" label="配置键" width="200" />
        <el-table-column prop="configValue" label="配置值" min-width="200" />
        <el-table-column prop="description" label="说明" min-width="200" />
        <el-table-column label="更新时间" width="160">
          <template #default="{ row }">{{ fmt(row.updatedAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="visible" :title="isEdit?'编辑配置':'新增配置'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="配置键" v-if="!isEdit">
          <el-input v-model="form.configKey" placeholder="如: site_name" />
        </el-form-item>
        <el-form-item label="配置键" v-else>
          <el-input :value="form.configKey" disabled />
        </el-form-item>
        <el-form-item label="配置值">
          <el-input v-model="form.configValue" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="form.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible=false">取消</el-button>
        <el-button type="primary" @click="save" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { adminApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const configs = ref([])
const loading = ref(false)
const visible = ref(false)
const saving = ref(false)
const isEdit = ref(false)
const form = reactive({ id: null, configKey: '', configValue: '', description: '' })
const fmt = d => d ? dayjs(d).format('YYYY-MM-DD HH:mm') : ''

async function fetch() {
  loading.value = true
  try {
    const res = await adminApi.configs()
    configs.value = res.data
  } finally { loading.value = false }
}

function openAdd() {
  isEdit.value = false
  Object.assign(form, { id: null, configKey: '', configValue: '', description: '' })
  visible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, row)
  visible.value = true
}

async function save() {
  if (!form.configKey) return ElMessage.warning('配置键不能为空')
  saving.value = true
  try {
    if (isEdit.value) {
      await adminApi.updateConfig(form.id, form)
    } else {
      await adminApi.createConfig(form)
    }
    ElMessage.success('保存成功')
    visible.value = false
    fetch()
  } finally { saving.value = false }
}

async function del(id) {
  await ElMessageBox.confirm('确认删除该配置？', '警告', { type: 'warning' })
  await adminApi.deleteConfig(id)
  ElMessage.success('删除成功')
  fetch()
}

onMounted(fetch)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #303133; }
.table-card { padding: 0; overflow: hidden; background: white; }
</style>