import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import router from './router'
import App from './App.vue'
import './assets/main.css'
import { useThemeStore } from './store/theme'

NProgress.configure({ showSpinner: false })

const app = createApp(App)
const pinia = createPinia()

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(router)
app.use(ElementPlus, { locale: zhCn })

// 初始化主题
app.mount('#app').$nextTick(() => {
  const themeStore = useThemeStore()
  themeStore.initializeTheme()
})