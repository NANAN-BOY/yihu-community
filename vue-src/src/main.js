import { createApp } from 'vue';
import App from './App.vue';
import router from './router.js';
import store from './store.js';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue'


const app = createApp(App);

app.use(ElementPlus);
// 使用状态管理
app.use(store);

// 使用路由
app.use(router);

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

// 挂载到 DOM
app.mount('#app');
