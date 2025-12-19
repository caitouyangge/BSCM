import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import Vant from 'vant'
import 'vant/lib/index.css'
import App from './App.vue'
import router from './router'
import './utils/rem' // 引入rem适配
import { isMobile, disableZoom } from './utils/mobile'

const app = createApp(App)
const pinia = createPinia()

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 移动端禁用缩放
if (isMobile()) {
  disableZoom()
}

app.use(pinia)
app.use(router)
app.use(ElementPlus)
app.use(Vant)

app.mount('#app')

