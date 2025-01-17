<template>
  <div class="register-page">
    <div class="register-container">
      <h2>欢迎注册</h2>
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="user_name">组织名称</label>
          <input type="text" id="user_name" v-model="user_name" required />
        </div>
        <div class="form-group">
          <label for="user_phoneNumber">管理者手机号</label>
          <input type="tel" id="user_phoneNumber" v-model="user_phoneNumber" required />
        </div>
        <div class="form-group">
          <label for="user_password">密码</label>
          <input type="password" id="user_password" v-model="user_password" required />
        </div>
        <div class="form-group">
          <label for="user_password_confirm">确认密码</label>
          <input type="password" id="user_password_confirm" v-model="user_password_confirm" required />
        </div>
        <div v-if="passwordMismatch" class="error-message">
          密码不匹配，请重新输入。
        </div>
        <button type="submit" :disabled="passwordMismatch">注册</button>
      </form>
      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
      <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import axios from 'axios';
import store from "../store.js";
import router from "../router.js";
import {ElNotification} from "element-plus";

// 表单数据
const user_name = ref('');
const user_phoneNumber = ref('');
const user_password = ref('');
const user_password_confirm = ref('');
const passwordMismatch = ref(false);
const errorMessage = ref('');
const successMessage = ref('');

// 监听密码和确认密码匹配情况
watch([user_password, user_password_confirm], () => {
  passwordMismatch.value = user_password.value !== user_password_confirm.value;
});

// 表单提交方法
const handleSubmit = async () => {
  if (!passwordMismatch.value) {
    try {
      const response = await axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/register`, {
        user_name: user_name.value,
        user_phoneNumber: user_phoneNumber.value,
        user_password: user_password.value
      });
      // 成功后处理返回的用户信息和 token
      successMessage.value = response.data.message;
      errorMessage.value = '';
      // 保存 token 到 localStorage
      // 将 token 和用户信息存入 Vuex
      await store.dispatch('setToken', response.data.token);
      await store.dispatch('setUser', {
        user_id: response.data.user.user_id,
        user_name: response.data.user.user_name,
        user_phoneNumber: response.data.user.user_phoneNumber,
        user_role: response.data.user.user_role,
        user_accountStatus: response.data.user.user_accountStatus,
      });

      // 将 token 存入 localStorage
      localStorage.setItem('token', response.data.token);
      ElNotification({
        title: '注册成功',
        message: '欢迎您的加入！',
        type: 'success',
        duration: 3000,
      });
      await router.push('/dashboard');
    } catch (error) {
      console.log(error);
      // 错误处理
      if (error.response) {
        errorMessage.value = error.response.data || '注册失败，请稍后重试';
      } else {
        errorMessage.value = '网络错误，请检查网络连接';
      }
      successMessage.value = '';
    }
  }
};

</script>

<style scoped>
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f4f7fa;
  padding: 20px; /* 增加内边距，防止内容过于紧贴屏幕 */
}

.register-container {
  width: 100%;
  max-width: 400px;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

button:hover:not(:disabled) {
  background-color: #0056b3;
}

.error-message {
  color: red;
  font-size: 12px;
  margin-top: 5px;
}

.success-message {
  color: green;
  font-size: 12px;
  margin-top: 5px;
}

/* 手机端样式 */
@media (max-width: 768px) {
  .register-page {
    justify-content: flex-start; /* 让内容靠上 */
    align-items: flex-start;     /* 内容向上对齐 */
    padding-top: 40px; /* 给顶部留一些空间 */
  }

  .register-container {
    width: 100%;
    max-width: 100%;
    padding: 15px;
    box-shadow: none; /* 去掉阴影 */
    border-radius: 0; /* 让背景更加简洁 */
  }
}
</style>
