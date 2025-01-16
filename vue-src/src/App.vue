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
      console.log('fetchUserInfo');
      const response = await fetch('http://localhost:3001/api/user-info', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${store.state.token}`,
        },
      });

      if (!response.ok) {
        throw new Error('身份已过期'); // 如果响应失败，抛出错误
      }

      const data = await response.json();

      if (!data.user || typeof data.user !== 'object') {
        throw new Error('身份已过期'); // 如果用户信息无效，抛出错误
      }

      commit('SET_USER', data.user); // 设置用户信息

    } catch (error) {
      // 使用 ElNotification 显示错误信息
      ElNotification({
        title: '身份已过期',
        message: '请重新登录。',
        type: 'error',
        duration: 3000, // 自动关闭时间
      });
      // 清除 token 和用户信息，跳转到登录页
      //登出
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
