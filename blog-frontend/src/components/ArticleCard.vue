<template>
  <div class="article-card card tech-btn" @click="$emit('click')">
    <div class="cover-container" v-if="article.coverImage">
      <div class="cover-overlay"></div>
      <img :src="article.coverImage" :alt="article.title" loading="lazy" class="cover-image" />
    </div>
    <div class="content">
      <div class="meta">
        <el-tag size="small" v-if="article.category" class="category-tag">{{ article.category }}</el-tag>
        <span class="date">{{ formatDate(article.createdAt) }}</span>
      </div>
      <h3 class="title">{{ article.title }}</h3>
      <p class="summary">{{ article.summary }}</p>
      <div class="tags" v-if="article.tags">
        <el-tag
          v-for="tag in article.tags.split(',')"
          :key="tag"
          size="small"
          type="info"
          effect="plain"
          class="article-tag"
        >{{ tag.trim() }}</el-tag>
      </div>
      <div class="footer-info">
        <div class="author">
          <el-avatar :src="article.author?.avatar" :size="28" />
          <span>{{ article.author?.username }}</span>
        </div>
        <div class="stats">
          <div class="stat-item">
            <el-icon><View /></el-icon>
            <span>{{ article.viewCount }}</span>
          </div>
          <div class="stat-item">
            <el-icon><ChatLineRound /></el-icon>
            <span>{{ article.commentCount || 0 }}</span>
          </div>
          <div class="stat-item">
            <el-icon><Star /></el-icon>
            <span>{{ article.likeCount }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import dayjs from 'dayjs'
defineProps({ article: Object })
defineEmits(['click'])
const formatDate = d => d ? dayjs(d).format('YYYY-MM-DD') : ''
</script>

<style scoped>
.article-card {
  display: flex; 
  gap: 0; 
  padding: 0;
  margin-bottom: 24px; 
  cursor: pointer; 
  transition: all 0.3s ease;
  border: 1px solid var(--border);
  overflow: hidden;
  position: relative;
  box-shadow: var(--shadow);
}
.article-card:hover { 
  transform: translateY(-4px);
  box-shadow: var(--shadow-hover);
}
.cover-container {
  position: relative;
  width: 220px;
  height: 140px;
  flex-shrink: 0;
  overflow: hidden;
  z-index: 2;
}
.cover-overlay {
  display: none;
}
.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
  position: relative;
  z-index: 0;
}
.article-card:hover .cover-image {
  transform: scale(1.05);
}
.content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 24px;
  position: relative;
  z-index: 3;
}
.meta {
  display: flex;
  align-items: center;
  gap: 12px;
}
.date {
  font-size: 13px;
  color: var(--text-hint);
}
.title {
  font-size: 1.4rem;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.4;
  margin: 0;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  transition: color 0.3s;
}
.article-card:hover .title {
  color: var(--primary);
}
.summary {
  font-size: 0.95rem;
  color: var(--text-secondary);
  line-height: 1.6;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  flex: 1;
}
.footer-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  padding-top: 10px;
}
.author {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.9rem;
  color: var(--text-secondary);
  font-weight: 500;
}
.stats {
  display: flex;
  gap: 20px;
  font-size: 0.85rem;
  color: var(--text-hint);
}
.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
}
.tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: -8px;
}
.category-tag {
  background: rgba(14, 165, 233, 0.15);
  border: 1px solid rgba(14, 165, 233, 0.3);
  color: var(--primary);
  border-radius: 20px;
  padding: 4px 12px;
  font-weight: 500;
}
.article-tag {
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.2);
  color: var(--secondary);
  border-radius: 20px;
  padding: 4px 10px;
  font-size: 0.8rem;
}

@media (max-width: 768px) {
  .article-card {
    flex-direction: column;
  }
  .cover-container {
    width: 100%;
    height: 180px;
  }
  .content {
    padding: 20px;
  }
}
</style>