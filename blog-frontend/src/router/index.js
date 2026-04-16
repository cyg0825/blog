import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import { useAuthStore } from '@/store/auth'

const routes = [
  // 用户端
  {
    path: '/',
    component: () => import('@/views/user/Layout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/user/Home.vue') },
      { path: 'articles', name: 'Articles', component: () => import('@/views/user/ArticleList.vue') },
      { path: 'articles/:id', name: 'ArticleDetail', component: () => import('@/views/user/ArticleDetail.vue') },
      { path: 'profile', name: 'Profile', component: () => import('@/views/user/Profile.vue'), meta: { requiresAuth: true } },
      { path: 'my-articles', name: 'MyArticles', component: () => import('@/views/user/MyArticles.vue'), meta: { requiresAuth: true } },
      { path: 'write', name: 'Write', component: () => import('@/views/user/ArticleEdit.vue'), meta: { requiresAuth: true } },
      { path: 'write/:id', name: 'EditArticle', component: () => import('@/views/user/ArticleEdit.vue'), meta: { requiresAuth: true } }
    ]
  },
  { path: '/login', name: 'Login', component: () => import('@/views/user/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('@/views/user/Register.vue') },
  // 管理员端
  {
    path: '/admin',
    component: () => import('@/views/admin/Layout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'users', name: 'AdminUsers', component: () => import('@/views/admin/Users.vue') },
      { path: 'articles', name: 'AdminArticles', component: () => import('@/views/admin/Articles.vue') },
      { path: 'comments', name: 'AdminComments', component: () => import('@/views/admin/Comments.vue') },
      { path: 'configs', name: 'AdminConfigs', component: () => import('@/views/admin/Configs.vue') }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach((to, from, next) => {
  NProgress.start()
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (to.meta.requiresAdmin && !authStore.isAdmin) {
    next('/')
  } else {
    next()
  }
})

router.afterEach(() => NProgress.done())

export default router
