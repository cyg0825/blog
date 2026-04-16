<template>
  <div class="container">
    <div class="page-title">
      <h1>个人资料</h1>
    </div>
    <el-row :gutter="32">
      <el-col :xs="24" :md="8">
        <div class="card profile-card">
          <div class="avatar-section">
            <el-avatar :src="form.avatar" :size="100" />
            <h3>{{ authStore.user?.username }}</h3>
            <el-tag>{{ authStore.user?.role === 'ADMIN' ? '管理员' : '普通用户' }}</el-tag>
            <p>{{ form.bio || '这个人很懒，什么都没写...' }}</p>
          </div>
          <div class="stats">
            <div class="stat-item">
              <span class="num">{{ myStats.articles }}</span>
              <span>篇文章</span>
            </div>
            <div class="stat-item">
              <span class="num">{{ myStats.likes }}</span>
              <span>获赞</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :md="16">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="编辑资料" name="profile">
            <div class="card form-card">
              <el-form :model="form" label-width="80px">
                <el-form-item label="头像">
                  <div class="avatar-upload">
                    <el-upload
                      class="avatar-uploader"
                      :show-file-list="false"
                      :before-upload="beforeAvatarUpload"
                      :http-request="uploadAvatar"
                      :on-success="handleAvatarSuccess"
                      :on-error="handleAvatarError"
                    >
                      <img v-if="form.avatar" :src="form.avatar" class="avatar" />
                      <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                    </el-upload>
                    <div class="avatar-desc">点击上传头像 (支持JPG/PNG，最大5MB)</div>
                  </div>
                </el-form-item>
                <el-form-item label="邮箱">
                  <el-input v-model="form.email" placeholder="邮箱地址" />
                </el-form-item>
                <el-form-item label="个人简介">
                  <el-input v-model="form.bio" type="textarea" :rows="4" placeholder="介绍一下自己" maxlength="200" show-word-limit />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="saveProfile" :loading="saving">保存修改</el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>
          <el-tab-pane label="修改密码" name="password">
            <div class="card form-card">
              <el-form :model="pwdForm" :rules="pwdRules" ref="pwdRef" label-width="80px">
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input v-model="pwdForm.oldPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="pwdForm.newPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="changePassword" :loading="pwdSaving">修改密码</el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/store/auth'
import { authApi, articleApi, fileApi } from '@/api'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const authStore = useAuthStore()
const activeTab = ref('profile')
const saving = ref(false)
const pwdSaving = ref(false)
const pwdRef = ref()
const myStats = ref({ articles: 0, likes: 0 })

const form = reactive({
  avatar: authStore.user?.avatar || '',
  email: authStore.user?.email || '',
  bio: authStore.user?.bio || ''
})

const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码' }],
  newPassword: [{ required: true, min: 6, message: '密码至少6位' }],
  confirmPassword: [{ validator: (r, v, cb) => v !== pwdForm.newPassword ? cb(new Error('两次密码不一致')) : cb() }]
}

async function saveProfile() {
  saving.value = true
  try {
    await authStore.updateProfile(form)
    ElMessage.success('资料更新成功')
  } catch (e) {
    ElMessage.error(e.message || '更新失败')
  } finally {
    saving.value = false
  }
}

async function changePassword() {
  try {
    await pwdRef.value.validate()
  } catch {
    return
  }
  pwdSaving.value = true
  try {
    await authApi.changePassword({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    ElMessage.success('密码修改成功')
    Object.assign(pwdForm, { oldPassword: '', newPassword: '', confirmPassword: '' })
  } catch (e) {
    ElMessage.error(e.message || '密码修改失败')
  } finally {
    pwdSaving.value = false
  }
}

// 头像上传相关函数
function beforeAvatarUpload(file) {
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/bmp', 'image/webp']
  const isAllowedType = allowedTypes.includes(file.type)
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isAllowedType) {
    ElMessage.error('头像图片只能是 JPG/PNG/GIF/BMP/WEBP 格!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('头像图片大小不能超过 5MB!')
    return false
  }
  return isAllowedType && isLt5M
}

async function uploadAvatar(options) {
  try {
    const res = await fileApi.uploadAvatar(options.file)
    if (res.success) {
      form.avatar = res.data
      // 更新全局用户信息
      await authStore.fetchMe()  // 从服务器获取最新用户信息
      ElMessage.success('头像上传成功!')
    } else {
      ElMessage.error(res.message || '头像上传失败')
    }
  } catch (error) {
    console.error('头像上传错误:', error)
    ElMessage.error(error.message || '头像上传失败')
  }
}

function handleAvatarSuccess(response, file, fileList) {
  // 在http-request模式下，此函数可能不会被调用，因为我们已经在uploadAvatar中处理了
}

function handleAvatarError(error, file, fileList) {
  console.error('头像上传错误:', error)
  ElMessage.error('头像上传失败')
}

onMounted(async () => {
  const res = await articleApi.list({ authorId: authStore.user?.id, page: 0, size: 100 })
  const articles = res.data.content
  myStats.value.articles = res.data.totalElements
  myStats.value.likes = articles.reduce((s, a) => s + (a.likeCount || 0), 0)
})
</script>

<style scoped>
.container { max-width: 1000px; margin: 40px auto; padding: 0 24px; }
.page-title { margin-bottom: 32px; }
.page-title h1 { font-size: 28px; font-weight: 700; }
.profile-card { padding: 32px; text-align: center; }
.avatar-section { display: flex; flex-direction: column; align-items: center; gap: 12px; margin-bottom: 24px; }
.avatar-section h3 { font-size: 20px; font-weight: 700; }
.avatar-section p { color: var(--text-secondary); font-size: 14px; }
.stats { display: flex; justify-content: center; gap: 40px; }
.stat-item { display: flex; flex-direction: column; align-items: center; gap: 4px; }
.num { font-size: 24px; font-weight: 700; color: var(--primary); }
.form-card { padding: 24px; }
.avatar-uploader { width: 100px; height: 100px; border: 1px dashed #d9d9d9; border-radius: 6px; cursor: pointer; position: relative; overflow: hidden; }
.avatar-uploader:hover { border-color: var(--primary); }
.avatar-uploader-icon { font-size: 28px; color: var(--text-hint); width: 100px; height: 100px; display: flex; align-items: center; justify-content: center; }
.avatar { width: 100px; height: 100px; display: block; object-fit: cover; }
.avatar-upload { display: flex; flex-direction: column; align-items: flex-start; gap: 10px; }
.avatar-desc { font-size: 12px; color: var(--text-hint); }
</style>