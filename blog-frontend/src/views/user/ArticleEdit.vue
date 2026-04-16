<template>
  <div class="container">
    <div class="page-header">
      <h1>{{ isEdit ? '编辑文章' : '写新文章' }}</h1>
      <div class="header-actions">
        <el-button @click="save(0)">保存草稿</el-button>
        <el-button type="primary" @click="save(1)" :loading="saving">发布文章</el-button>
      </div>
    </div>

    <div class="editor-layout">
      <div class="main-editor card">
        <el-input
          v-model="form.title"
          placeholder="请输入文章标题..."
          class="title-input"
          maxlength="200"
          show-word-limit
        />
        <el-divider />
        <!-- 富文本编辑器 -->
        <div class="editor-container">
          <RichEditor
            v-model="form.content"
            height="500px"
            placeholder="请输入文章内容..."
          />
        </div>
      </div>

      <!-- 侧边配置 -->
      <div class="sidebar-editor">
        <div class="card config-card">
          <h3>文章设置</h3>
          <el-form :model="form" label-position="top" size="small">
            <el-form-item label="文章摘要">
              <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="文章摘要（不填则自动截取）" maxlength="200" />
            </el-form-item>
            <el-form-item label="文章分类">
              <el-select v-model="form.category" placeholder="选择或输入分类" allow-create filterable style="width:100%">
                <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
              </el-select>
            </el-form-item>
            <el-form-item label="文章标签">
              <el-input v-model="form.tags" placeholder="多个标签用逗号分隔，如: Java,Spring" />
            </el-form-item>
            <el-form-item label="封面图片URL">
              <el-input v-model="form.coverImage" placeholder="https://..." />
              <div class="cover-preview" v-if="form.coverImage">
                <img :src="form.coverImage" alt="封面预览" />
              </div>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { articleApi } from '@/api'
import { ElMessage } from 'element-plus'
import RichEditor from '@/components/RichEditor.vue'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const saving = ref(false)
const categories = ref([])

const form = reactive({
  title: '', content: '', summary: '', coverImage: '',
  category: '', tags: '', status: 1
})

async function save(status) {
  if (!form.title.trim()) return ElMessage.warning('请输入文章标题')
  if (!form.content.trim()) return ElMessage.warning('请输入文章内容')
  saving.value = true
  try {
    const payload = { ...form, status }
    if (isEdit.value) {
      await articleApi.update(route.params.id, payload)
      ElMessage.success(status === 1 ? '更新并发布成功' : '草稿已保存')
      router.push(`/articles/${route.params.id}`)
    } else {
      const res = await articleApi.create(payload)
      ElMessage.success(status === 1 ? '发布成功' : '草稿已保存')
      router.push(`/articles/${res.data.id}`)
    }
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  const catRes = await articleApi.categories()
  categories.value = catRes.data
  if (isEdit.value) {
    const res = await articleApi.detail(route.params.id)
    Object.assign(form, {
      title: res.data.title,
      content: res.data.content,
      summary: res.data.summary,
      coverImage: res.data.coverImage,
      category: res.data.category,
      tags: res.data.tags,
      status: res.data.status
    })
  }
})
</script>

<style scoped>
.container { max-width: 1400px; margin: 32px auto; padding: 0 24px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-header h1 { font-size: 26px; font-weight: 700; }
.editor-layout { display: grid; grid-template-columns: 1fr 300px; gap: 24px; }
.main-editor { padding: 24px; }
.title-input :deep(.el-input__inner) { font-size: 22px; font-weight: 600; border: none; padding: 0; }
.editor-toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; flex-wrap: wrap; gap: 8px; }
.editor-container { height: 500px; border: 1px solid var(--border); border-radius: 8px; overflow: hidden; }
.md-editor { height: 100%; }
.md-editor :deep(.el-textarea__inner) { height: 100% !important; border: none; resize: none; font-family: 'Fira Code', monospace; font-size: 14px; line-height: 1.7; }
.md-preview { height: 100%; padding: 16px; overflow-y: auto; }
.sidebar-editor {}
.config-card { padding: 20px; }
.config-card h3 { font-size: 16px; font-weight: 600; margin-bottom: 16px; }
.cover-preview { margin-top: 8px; border-radius: 6px; overflow: hidden; }
.cover-preview img { width: 100%; height: 120px; object-fit: cover; }

@media (max-width: 900px) {
  .editor-layout { grid-template-columns: 1fr; }
}
</style>