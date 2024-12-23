import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import locate from 'element-plus/es/locale/lang/zh-cn';
import router from './router';
import App from './App.vue'
import 'animate.css';
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate);

app.use(router);
app.use(ElementPlus, { locale: locate })
app.use(pinia)

app.mount('#app')