<template>
  <div>
    <!-- 路由内容显示区域-->
    <router-view></router-view>
  </div>
</template>

<script setup>
import { onMounted, ref, computed, getCurrentInstance } from 'vue';
import axios from 'axios';
import { useStore } from 'vuex';

const store = useStore(); // 使用 Vuex store

const restoreLoginStatus = async () => {
  const token = localStorage.getItem('token'); // 从 localStorage 获取 token
  if (token) {
    try {
      const response = await axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/restore-login`, {
        headers: {
          Authorization: `Bearer ${token}`, // 使用 token 进行身份验证
        },
      });
      const userInfo = response.data;
      // 使用 Vuex 存储用户信息
      await store.dispatch('login', token);
      await store.dispatch('getUserInfo', {
        user_id: userInfo.user.user_id,
      });
    } catch (error) {
      if (error.response && error.response.status === 401) {
        // 清除本地 token
        localStorage.removeItem('token');
      }
    }
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
}

html, body {
    height: 100%;
    margin: 0;
    padding: 0;
    font-family: 'Arial', sans-serif;
}

.router-view {
    flex: 1; /* 确保内容区域扩展填满屏幕 */
    width: 100%;
    padding-top: 100px; /* 避免内容遮挡导航栏 */
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
