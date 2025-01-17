<template>
  <div>
    <!-- 路由内容显示区域-->
    <router-view></router-view>
  </div>
</template>

<script setup>
import { onMounted, ref, computed, getCurrentInstance } from 'vue';
import axios from 'axios';
import store from './store';
import {ElNotification} from "element-plus";
import router from "./router";
const restoreLoginStatus = async () => {
  try {

    // 尝试获取用户信息
    const response = await fetch(`${import.meta.env.VITE_BACKEND_IP}/api/restore-login`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${store.state.token}`,  // 将 token 作为 Bearer token 发送
      },
    });

    // 如果响应失败，抛出错误
    if (!response.ok) {
      throw new Error('身份已过期');
    }

    // 获取响应数据
    const data = await response.json();

    // 如果后端返回错误信息，抛出错误
    if (data.status === 'error') {
      throw new Error(data.message);
    }

    // 返回状态正常时，处理用户信息
    if (data.status === 'success' && data.user) {
      // 将用户信息存入 Vuex
      await store.dispatch('setUser', {
        user_id: data.user.user_id,
        user_name: data.user.user_name,
        user_phoneNumber: data.user.user_phoneNumber,
        user_role: data.user.user_role,
        user_accountStatus: data.user.user_accountStatus,
      });
    } else {
      throw new Error('恢复用户信息失败');
    }
  } catch (error) {
    // 使用 ElNotification 显示错误信息
    ElNotification({
      title: '身份已过期',
      message: '请重新登录。',
      type: 'error',
      duration: 3000, // 自动关闭时间
    });

    // 清除 token 和用户信息
    localStorage.removeItem('token');  // 清除 token
    await store.dispatch('setToken', null);  // 清空 Vuex 中的 token
    await store.dispatch('setUser', null);  // 清空 Vuex 中的用户信息

    // 跳转到登录页面
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
