import request from '@/utils/request'

export const authApi = {
  login: data => request.post('/auth/login', data),
  register: data => request.post('/auth/register', data),
  getMe: () => request.get('/auth/me'),
  updateProfile: data => request.put('/auth/profile', data),
  changePassword: data => request.post('/auth/change-password', data)
}

export const fileApi = {
  uploadAvatar: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return request.post('/files/avatar/upload', formData);
  },
  uploadFile: (formData) => {
    return request.post('/files/wang/upload', formData);
  }
}

export const articleApi = {
  list: params => request.get('/articles', { params }),
  detail: id => request.get(`/articles/${id}`),
  create: data => request.post('/articles', data),
  update: (id, data) => request.put(`/articles/${id}`, data),
  delete: id => request.delete(`/articles/${id}`),
  aiSummary: id => request.post(`/articles/${id}/ai-summary`),
  like: id => request.post(`/articles/${id}/like`),
  categories: () => request.get('/articles/categories'),
  hot: () => request.get('/articles/hot')
}

export const commentApi = {
  list: articleId => request.get(`/comments/article/${articleId}`),
  add: (articleId, data) => request.post(`/comments/article/${articleId}`, data),
  delete: id => request.delete(`/comments/${id}`)
}

export const adminApi = {
  dashboard: () => request.get('/admin/dashboard'),
  // users
  users: params => request.get('/admin/users', { params }),
  getUser: id => request.get(`/admin/users/${id}`),
  updateUser: (id, data) => request.put(`/admin/users/${id}`, data),
  deleteUser: id => request.delete(`/admin/users/${id}`),
  toggleUserStatus: id => request.post(`/admin/users/${id}/toggle-status`),
  // articles
  articles: params => request.get('/admin/articles', { params }),
  deleteArticle: id => request.delete(`/admin/articles/${id}`),
  updateArticleStatus: (id, status) => request.put(`/admin/articles/${id}/status`, { status }),
  // comments
  comments: params => request.get('/admin/comments', { params }),
  updateCommentStatus: (id, status) => request.put(`/admin/comments/${id}/status`, { status }),
  deleteComment: id => request.delete(`/admin/comments/${id}`),
  // configs
  configs: () => request.get('/admin/configs'),
  createConfig: data => request.post('/admin/configs', data),
  updateConfig: (id, data) => request.put(`/admin/configs/${id}`, data),
  deleteConfig: id => request.delete(`/admin/configs/${id}`)
}