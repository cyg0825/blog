<template>
  <div class="container">
    <div v-if="loading" class="loading-wrap">
      <el-skeleton :rows="10" animated />
    </div>

    <div v-else-if="article">
      <!-- 文章头部 -->
      <div class="article-header">
        <div class="breadcrumb">
          <router-link to="/">首页</router-link>
          <el-icon><ArrowRight /></el-icon>
          <router-link to="/articles">文章</router-link>
          <el-icon><ArrowRight /></el-icon>
          <span>{{ article.category }}</span>
        </div>
        <el-tag v-if="article.category" size="large" class="category-tag">{{ article.category }}</el-tag>
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="meta-bar">
          <el-avatar :src="article.author?.avatar" :size="40" />
          <div class="meta-info">
            <span class="author-name">{{ article.author?.username }}</span>
            <span class="date">{{ formatDate(article.createdAt) }}</span>
          </div>
          <div class="meta-stats">
            <span><el-icon><View /></el-icon>{{ article.viewCount }}</span>
            <span><el-icon><ChatLineRound /></el-icon>{{ comments.length }}</span>
            <span><el-icon><Star /></el-icon>{{ article.likeCount }}</span>
          </div>
        </div>
        <div class="tags" v-if="article.tags">
          <el-tag
            v-for="tag in article.tags.split(',')"
            :key="tag" type="info" size="small" effect="plain"
          >{{ tag.trim() }}</el-tag>
        </div>
      </div>

      <!-- 封面图 -->
      <div class="cover-wrap" v-if="article.coverImage">
        <img :src="article.coverImage" :alt="article.title" />
      </div>

      <!-- ✨ AI 智能摘要区域 -->
      <div class="ai-summary-block">
        <!-- 已有摘要 -->
        <transition name="fade-slide">
          <div v-if="aiSummary" class="ai-summary-card">
            <div class="ai-card-header">
              <div class="ai-card-title">
                <span class="ai-icon-wrap"><el-icon><MagicStick /></el-icon></span>
                <span>AI 智能摘要</span>
                <el-tag size="small" type="success" effect="dark">DeepSeek</el-tag>
              </div>
              <el-button link size="small" @click="regenerateSummary" :loading="aiLoading" style="color:#909399">
                <el-icon><Refresh /></el-icon> 重新生成
              </el-button>
            </div>
            <p class="ai-summary-text">{{ aiSummary }}</p>
          </div>
        </transition>

        <!-- 生成中 -->
        <div v-if="aiLoading && !aiSummary" class="ai-generating-card">
          <div class="ai-card-header">
            <div class="ai-card-title">
              <span class="ai-icon-wrap"><el-icon class="is-loading"><Loading /></el-icon></span>
              <span>AI 正在生成智能摘要...</span>
            </div>
          </div>
          <div class="ai-skeleton">
            <div class="skeleton-line long"></div>
            <div class="skeleton-line medium"></div>
            <div class="skeleton-line short"></div>
          </div>
        </div>

        <!-- 未生成，显示触发按钮 -->
        <div v-if="!aiSummary && !aiLoading" class="ai-trigger-card">
          <div class="ai-trigger-content">
            <div class="ai-trigger-icon"><el-icon><MagicStick /></el-icon></div>
            <div>
              <div class="ai-trigger-title">AI 智能摘要</div>
              <div class="ai-trigger-desc">由 DeepSeek 大模型自动提炼文章核心要点</div>
            </div>
            <el-button type="primary" @click="generateSummary" round>
              <el-icon><MagicStick /></el-icon> 一键生成
            </el-button>
          </div>
        </div>
      </div>

      <!-- 文章正文 -->
      <div class="article-body">
        <div class="markdown-body" v-html="renderedContent"></div>
      </div>

      <!-- 点赞 / 编辑按钮 -->
      <div class="action-bar">
        <el-button
          type="primary" round size="large"
          :class="{ liked: liked }"
          @click="handleLike" :loading="liking"
        >
          <el-icon><Star /></el-icon>
          {{ liked ? '已点赞' : '点赞' }} · {{ article.likeCount }}
        </el-button>
        <el-button round size="large" @click="$router.push('/articles')">
          <el-icon><ArrowLeft /></el-icon> 返回列表
        </el-button>
        <el-button v-if="canEdit" round size="large" @click="$router.push(`/write/${article.id}`)">
          <el-icon><Edit /></el-icon> 编辑文章
        </el-button>
      </div>

      <!-- ✨ 评论区 -->
      <div class="comments-section">
        <div class="comments-title">
          <el-icon><ChatLineRound /></el-icon>
          <h2>评论 <span class="comment-count">{{ comments.length }}</span></h2>
        </div>

        <!-- 发表评论 -->
        <div v-if="authStore.isLoggedIn" class="comment-form card">
          <div class="comment-form-header">
            <el-avatar :src="authStore.user?.avatar" :size="40" />
            <span class="comment-form-user">{{ authStore.user?.username }}</span>
          </div>
          <el-input
            v-model="newComment"
            type="textarea"
            :rows="4"
            placeholder="说说你的想法... (支持技术交流、学习讨论)"
            maxlength="500"
            show-word-limit
            resize="none"
          />
          <!-- AI 审查提示 -->
          <div class="ai-review-hint">
            <el-icon color="#909399"><MagicStick /></el-icon>
            <span>评论提交后将由 AI 自动审查内容合规性</span>
          </div>
          <div class="comment-form-footer">
            <el-button
              type="primary" round
              @click="submitComment"
              :loading="submitting"
              :disabled="!newComment.trim()"
            >发表评论</el-button>
          </div>
        </div>
        <div v-else class="login-tip card">
          <el-icon size="24" color="#909399"><ChatLineRound /></el-icon>
          <span>请 <router-link to="/login" class="login-link">登录</router-link> 后发表评论</span>
        </div>

        <!-- 评论列表 -->
        <transition-group name="comment-list" tag="div" class="comment-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item card">
            <el-avatar :src="comment.author?.avatar" :size="44" class="comment-avatar" />
            <div class="comment-body">
              <div class="comment-meta">
                <span class="comment-author">{{ comment.author?.username }}</span>
                <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
                <!-- AI 审查徽章 -->
                <el-tooltip
                  v-if="comment.aiReviewResult !== null"
                  :content="comment.aiReviewReason || ''"
                  placement="top"
                >
                  <el-tag
                    size="small"
                    :type="comment.aiReviewResult === 1 ? 'success' : 'danger'"
                    effect="light"
                    class="ai-badge"
                  >
                    <template v-if="comment.aiReviewResult !== 1">
                      <el-icon><Close /></el-icon>
                    </template>
                    AI {{ comment.aiReviewResult === 1 ? '通过' : '拦截' }}
                  </el-tag>
                </el-tooltip>
              </div>
              <p class="comment-content">{{ comment.content }}</p>
              <div class="comment-actions">
                <el-button
                  link size="small" type="danger"
                  v-if="canDeleteComment(comment)"
                  @click="deleteComment(comment.id)"
                >
                  <el-icon><Delete /></el-icon> 删除
                </el-button>
              </div>
            </div>
          </div>
        </transition-group>

        <el-empty
          v-if="comments.length === 0"
          description="暂无评论，来发表第一条吧！"
          :image-size="80"
        />
      </div>

      <!-- AI 审查拦截弹窗 -->
      <el-dialog
        v-model="rejectedDialog"
        title="评论未通过审查"
        width="420px"
        center
      >
        <div class="reject-dialog-content">
          <el-icon size="48" color="#f56c6c"><CircleCloseFilled /></el-icon>
          <p class="reject-reason">{{ rejectedReason }}</p>
          <p class="reject-tip">请修改评论内容后重新提交</p>
        </div>
        <template #footer>
          <el-button type="primary" @click="rejectedDialog = false">知道了</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { articleApi, commentApi } from '@/api'
import { useAuthStore } from '@/store/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import dayjs from 'dayjs'

const route = useRoute()
const authStore = useAuthStore()
const article = ref(null)
const loading = ref(true)
const comments = ref([])
const newComment = ref('')
const submitting = ref(false)
const liking = ref(false)
const liked = ref(false)
const aiSummary = ref('')
const aiLoading = ref(false)
const rejectedDialog = ref(false)
const rejectedReason = ref('')

const renderedContent = computed(() => {
  if (!article.value?.content) return ''
  return DOMPurify.sanitize(marked.parse(article.value.content))
})

const canEdit = computed(() =>
  authStore.isLoggedIn &&
  (authStore.isAdmin || authStore.user?.id === article.value?.author?.id)
)

const canDeleteComment = comment =>
  authStore.isLoggedIn &&
  (authStore.isAdmin || authStore.user?.id === comment.author?.id)

const formatDate = d => d ? dayjs(d).format('YYYY-MM-DD HH:mm') : ''

async function fetchData() {
  loading.value = true
  try {
    const [artRes, comRes] = await Promise.all([
      articleApi.detail(route.params.id),
      commentApi.list(route.params.id)
    ])
    article.value = artRes.data
    comments.value = comRes.data
    // 已有 AI 摘要直接显示
    if (artRes.data.aiSummary) {
      aiSummary.value = artRes.data.aiSummary
    } else {
      // 没有摘要则自动触发生成
      generateSummary()
    }
  } finally {
    loading.value = false
  }
}

async function generateSummary() {
  if (aiLoading.value) return
  aiLoading.value = true
  try {
    const res = await articleApi.aiSummary(article.value.id)
    if (res.data) {
      aiSummary.value = res.data
    }
  } catch (e) {
    // 静默失败，不影响阅读
  } finally {
    aiLoading.value = false
  }
}

async function regenerateSummary() {
  aiSummary.value = ''
  await generateSummary()
}

async function handleLike() {
  if (liked.value) return
  liking.value = true
  try {
    await articleApi.like(article.value.id)
    article.value.likeCount++
    liked.value = true
    ElMessage.success('点赞成功！')
  } finally {
    liking.value = false
  }
}

async function submitComment() {
  const content = newComment.value.trim()
  if (!content) return ElMessage.warning('评论内容不能为空')
  if (content.length < 2) return ElMessage.warning('评论至少2个字符')

  submitting.value = true
  try {
    const res = await commentApi.add(article.value.id, { content })
    comments.value.push(res.data)
    newComment.value = ''
    ElMessage.success('评论发表成功')
  } catch (e) {
    const msg = e.message || ''
    if (msg.includes('不符合规范') || msg.includes('拦截') || msg.includes('审查')) {
      // AI 审查拦截：弹出专属对话框
      rejectedReason.value = msg.replace('评论内容不符合规范：', '')
      rejectedDialog.value = true
    } else {
      // 其他业务错误：普通提示
      ElMessage.error(msg || '评论发表失败')
    }
  } finally {
    submitting.value = false
  }
}

async function deleteComment(id) {
  try {
    await ElMessageBox.confirm('确认删除此评论？', '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    await commentApi.delete(id)
    comments.value = comments.value.filter(c => c.id !== id)
    ElMessage.success('删除成功')
  } catch (e) {
    ElMessage.error(e.message || '删除失败')
  }
}

onMounted(fetchData)
</script>

<style scoped>
.container { max-width: 900px; margin: 0 auto; padding: 32px 24px 60px; }
.loading-wrap { padding: 40px 0; }

/* 文章头部 */
.breadcrumb { display: flex; align-items: center; gap: 6px; font-size: 13px; color: var(--text-hint); margin-bottom: 16px; }
.breadcrumb a { color: var(--text-hint); }
.breadcrumb a:hover { color: var(--primary); }
.category-tag { margin-bottom: 12px; }
.article-title { font-size: clamp(22px, 4vw, 36px); font-weight: 800; line-height: 1.4; margin-bottom: 20px; color: var(--text-primary); }
.meta-bar { display: flex; align-items: center; gap: 12px; margin-bottom: 14px; flex-wrap: wrap; }
.meta-info { display: flex; flex-direction: column; gap: 2px; }
.author-name { font-weight: 600; font-size: 15px; color: var(--text-primary); }
.date { font-size: 13px; color: var(--text-hint); }
.meta-stats { display: flex; gap: 16px; margin-left: auto; color: var(--text-hint); font-size: 14px; }
.meta-stats span { display: flex; align-items: center; gap: 4px; }
.tags { display: flex; gap: 6px; flex-wrap: wrap; }

/* 封面 */
.cover-wrap { border-radius: 12px; overflow: hidden; margin: 20px 0; max-height: 420px; }
.cover-wrap img { width: 100%; max-height: 420px; object-fit: cover; display: block; }

/* ✨ AI 摘要区域 */
.ai-summary-block { margin: 24px 0; }

.ai-summary-card {
  border-radius: 12px;
  background: var(--card-bg);
  border: 1px solid var(--border);
  padding: 18px 22px;
  box-shadow: var(--shadow);
}
.ai-generating-card {
  border-radius: 12px;
  background: var(--card-bg);
  border: 1px solid var(--border);
  padding: 18px 22px;
  box-shadow: var(--shadow);
}
.ai-trigger-card {
  border-radius: 12px;
  background: var(--card-bg);
  border: 1px solid var(--border);
  padding: 16px 22px;
  transition: all 0.3s;
  box-shadow: var(--shadow);
}
.ai-trigger-card:hover { border-color: var(--primary); background: var(--bg-secondary); }

.ai-card-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.ai-card-title { display: flex; align-items: center; gap: 8px; font-weight: 600; font-size: 15px; color: var(--text-primary); }
.ai-icon-wrap { display: flex; align-items: center; color: var(--primary); font-size: 18px; }
.ai-summary-text { color: var(--text-secondary); line-height: 1.8; font-size: 14px; }

.ai-trigger-content { display: flex; align-items: center; gap: 16px; }
.ai-trigger-icon { font-size: 28px; color: var(--text-hint); line-height: 1; }
.ai-trigger-title { font-weight: 600; font-size: 15px; color: var(--text-primary); }
.ai-trigger-desc { font-size: 13px; color: var(--text-hint); margin-top: 3px; }

/* 骨架屏动画 */
.ai-skeleton { display: flex; flex-direction: column; gap: 8px; margin-top: 4px; }
.skeleton-line { height: 12px; border-radius: 6px; background: linear-gradient(90deg, var(--bg-secondary) 25%, var(--bg) 50%, var(--bg-secondary) 75%); background-size: 200% 100%; animation: shimmer 1.5s infinite; }
.skeleton-line.long { width: 92%; }
.skeleton-line.medium { width: 76%; }
.skeleton-line.short { width: 55%; }
@keyframes shimmer { 0% { background-position: -200% 0; } 100% { background-position: 200% 0; } }

/* 文章正文 */
.article-body { padding: 36px 40px; margin: 0 0 24px; background: var(--card-bg); border-radius: var(--radius); border: 1px solid var(--border); }

/* 操作栏 */
.action-bar { display: flex; justify-content: center; gap: 14px; margin: 28px 0 40px; flex-wrap: wrap; }
.liked { background: var(--danger) !important; border-color: var(--danger) !important; color: white !important; }

/* 评论区 */
.comments-section { margin-top: 8px; }
.comments-title { display: flex; align-items: center; gap: 8px; margin-bottom: 20px; }
.comments-title h2 { font-size: 20px; font-weight: 700; color: var(--text-primary); }
.comment-count { background: var(--primary); color: white; border-radius: 10px; padding: 1px 8px; font-size: 13px; }

.comment-form { padding: 20px; margin-bottom: 24px; background: var(--card-bg); }
.comment-form-header { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.comment-form-user { font-weight: 600; font-size: 15px; color: var(--text-primary); }
.ai-review-hint { display: flex; align-items: center; gap: 6px; font-size: 12px; color: var(--text-hint); margin: 8px 0 12px; }
.comment-form-footer { display: flex; justify-content: flex-end; }

.login-tip { padding: 24px; display: flex; align-items: center; justify-content: center; gap: 10px; color: var(--text-hint); margin-bottom: 24px; background: var(--card-bg); }
.login-link { color: var(--primary); font-weight: 600; }

.comment-list { display: flex; flex-direction: column; gap: 16px; }
.comment-item { display: flex; gap: 14px; padding: 18px 20px; background: var(--card-bg); }
.comment-avatar { flex-shrink: 0; }
.comment-body { flex: 1; min-width: 0; }
.comment-meta { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; flex-wrap: wrap; }
.comment-author { font-weight: 600; font-size: 15px; color: var(--text-primary); }
.comment-date { font-size: 13px; color: var(--text-hint); }
.ai-badge { cursor: help; }
.comment-content { color: var(--text-secondary); line-height: 1.75; font-size: 14px; word-break: break-word; }
.comment-actions { margin-top: 8px; }

/* AI 拦截弹窗 */
.reject-dialog-content { display: flex; flex-direction: column; align-items: center; gap: 12px; padding: 10px 0; text-align: center; }
.reject-reason { font-size: 15px; color: var(--danger); font-weight: 500; }
.reject-tip { font-size: 13px; color: var(--text-hint); }

/* 过渡动画 */
.fade-slide-enter-active { transition: all 0.4s ease; }
.fade-slide-enter-from { opacity: 0; transform: translateY(-10px); }
.comment-list-enter-active { transition: all 0.35s ease; }
.comment-list-enter-from { opacity: 0; transform: translateX(-20px); }

@media (max-width: 640px) {
  .article-body { padding: 20px 16px; }
  .meta-stats { margin-left: 0; }
  .ai-trigger-content { flex-direction: column; align-items: flex-start; }
}
</style>