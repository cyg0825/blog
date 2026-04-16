import { defineStore } from 'pinia'

export const useThemeStore = defineStore('theme', {
  state: () => ({
    theme: localStorage.getItem('theme') || 'light' // 默认为浅色主题
  }),

  getters: {
    isDark: (state) => state.theme === 'dark',
    isLight: (state) => state.theme === 'light'
  },

  actions: {
    toggleTheme() {
      this.theme = this.isDark ? 'light' : 'dark'
      this.applyTheme()
    },

    setTheme(theme) {
      this.theme = theme
      this.applyTheme()
    },

    applyTheme() {
      const html = document.documentElement
      if (this.isDark) {
        html.classList.remove('light-theme')
        html.classList.add('dark-theme')
      } else {
        html.classList.remove('dark-theme')
        html.classList.add('light-theme')
      }
      
      // 保存主题设置到本地存储
      localStorage.setItem('theme', this.theme)
    },

    initializeTheme() {
      // 初始化时应用当前主题
      this.applyTheme()
    }
  }
})