<template>
  <div>
    <div v-if="IsLoading" class="global-loading">
      <div class="spinner-container">
        <div class="spinner"></div>
        <h1 class="loading-text">登陆中</h1>
      </div>
    </div>
    <router-view :key="$route.fullPath" v-show="!IsLoading"></router-view>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue';
import axios from 'axios';
import store from './store';
import {ElMessage} from "element-plus";
import router from "./router";
const IsLoading = ref(false);
const restoreLoginStatus = async () => {
  try {
    if (!store.state.token) {
      return;
    }
    IsLoading.value = true
    const response = await axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/user/reLogin`, {
      headers: {
        'token': `${store.state.token}`
      }
    });
    IsLoading.value= false
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
  background-color: #ffffff;
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
    color: #ffffff; /* 按钮文本颜色 */
    border: none; /* 去掉按钮边框 */
    border-radius: 5px; /* 圆角 */
    cursor: pointer; /* 鼠标悬停时变成手指 */
}

button:hover {
    background-color: #0056b3; /* 鼠标悬停时改变背景颜色 */
}
.global-loading {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.95);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.spinner-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #ffffff;
  border-top: 4px solid #007BFF;
  border-radius: 50%;
  margin-bottom: 1rem; /* 增加与文字的间距 */
  animation: spin 1s linear infinite;
}


.loading-text {
  color: #666;
  font-size: 1.2rem;
  font-weight: normal;
  text-align: center;
  margin: 0;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}
</style>
