import axios from 'axios'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

request.interceptors.request.use(config => {
  NProgress.start()
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
}, error => {
  NProgress.done()
  return Promise.reject(error)
})

request.interceptors.response.use(response => {
  NProgress.done()
  const data = response.data
  // 业务层失败（success: false）时展示错误并抛出，但 HTTP 状态码仍是 2xx
  if (data && data.success === false) {
    ElMessage.error(data.message || '操作失败')
    return Promise.reject(new Error(data.message || '操作失败'))
  }
  return data
}, error => {
  NProgress.done()
  const status = error.response?.status
  const msg = error.response?.data?.message || error.message || '网络请求失败'

  if (status === 401) {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    // 仅非登录页才跳转，避免死循环
    if (!window.location.pathname.includes('/login')) {
      window.location.href = '/login'
    }
  } else if (status === 403) {
    // 静默处理，由路由守卫负责权限跳转
  } else if (status === 400) {
    // 400 由调用方自己处理（如评论AI拦截、登录失败等），拦截器只透传错误消息
  } else {
    ElMessage.error(msg)
  }
  return Promise.reject(new Error(msg))
})

export default request
