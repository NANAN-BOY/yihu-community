import { createApp } from 'vue';
import App from './App.vue';
import router from './router.js';
import store from './store.js';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import zhCn from 'element-plus/es/locale/lang/zh-cn'


const app = createApp(App);

app.use(ElementPlus);
// 使用状态管理
app.use(store);
app.use(ElementPlus, {
    locale: zhCn
})
// 使用路由
app.use(router);


// 挂载到 DOM
app.mount('#app');
