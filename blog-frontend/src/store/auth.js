import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  async function login(credentials) {
    const res = await authApi.login(credentials)
    token.value = res.data.token
    user.value = res.data.user
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data.user))
    return res.data.user
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  async function fetchMe() {
    try {
      const res = await authApi.getMe()
      user.value = res.data
      localStorage.setItem('user', JSON.stringify(res.data))
    } catch (e) {
      logout()
    }
  }

  async function updateProfile(data) {
    const res = await authApi.updateProfile(data)
    user.value = res.data
    localStorage.setItem('user', JSON.stringify(res.data))
  }

  return { token, user, isLoggedIn, isAdmin, login, logout, fetchMe, updateProfile }
})
