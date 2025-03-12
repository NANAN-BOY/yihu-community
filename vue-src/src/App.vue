<template>
  <div>
    <router-view :key="$route.fullPath"></router-view>
  </div>
</template>

<script setup>
import { onMounted, ref, computed, getCurrentInstance } from 'vue';
import axios from 'axios';
import store from './store';
import {ElMessage, ElNotification} from "element-plus";
import router from "./router";
const restoreLoginStatus = async () => {
  try {
    if (!store.state.token) {
      return;
    }
    const response = await axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/user/reLogin`, {
      headers: {
        'token': `${store.state.token}`
      }
    });
    if (response.data.code != 200) {
      throw new Error('身份已过期，请重新登录。');
    }
    if (response.status === 'error') {
      throw new Error(response.message);
    }
    if (response.data.code == '200' && response.data.data) {
      await store.dispatch('setUser', response.data.data);
    } else {
      throw new Error('身份已过期，请重新登录。');
    }
  } catch (error) {
    ElMessage.error('身份已过期，请重新登录。');
    localStorage.removeItem('token');
    await store.dispatch('setToken', null);
    await store.dispatch('setUser', null);
    await router.push('/login');
  }
};


// 在组件加载时调用
onMounted(() => {
  restoreLoginStatus(); // 恢复登录状态
});
</script>


<style scoped>
/* App.vue 的基础样式 */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  background-color: #f4f4f4;
}

html, body {
    height: 100%;
    margin: 0;
    padding: 0;
    font-family: 'Arial', sans-serif;
}


button {
    margin: 20px; /* 按钮与其他元素间的间距 */
    padding: 10px 20px; /* 按钮的内边距 */
    background-color: #007BFF; /* 按钮背景颜色 */
    color: white; /* 按钮文本颜色 */
    border: none; /* 去掉按钮边框 */
    border-radius: 5px; /* 圆角 */
    cursor: pointer; /* 鼠标悬停时变成手指 */
}

button:hover {
    background-color: #0056b3; /* 鼠标悬停时改变背景颜色 */
}
</style>
