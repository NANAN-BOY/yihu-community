<template>
  <div class="outer-layer">
    <div class="outer-layer-content">
      <div class="content_box">
        <div class="content_left"></div>
        <div class="content_right">
          <h2>欢迎注册</h2>
          <div class="cr_top">
            <el-form @submit.prevent="handleSubmit" label-width="0px">
              <!-- 组织名称 -->
              <el-form-item>
                <el-input
                  v-model="user_name"
                  placeholder="请输入组织名称"
                  clearable
                  prefix-icon="User"
                ></el-input>
              </el-form-item>

              <!-- 手机号 -->
              <el-form-item>
                <el-input
                  v-model="user_phoneNumber"
                  type="tel"
                  placeholder="请输入管理者手机号"
                  clearable
                  prefix-icon="PhoneFilled"
                ></el-input>
              </el-form-item>

              <!-- 密码 -->
              <el-form-item>
                <el-input
                  v-model="user_password"
                  type="password"
                  placeholder="请输入密码"
                  show-password
                  prefix-icon="Key"
                ></el-input>
              </el-form-item>

              <!-- 确认密码 -->
              <el-form-item>
                <el-input
                  v-model="user_password_confirm"
                  type="password"
                  placeholder="请确认密码"
                  show-password
                  prefix-icon="Key"
                ></el-input>
              </el-form-item>

              <!-- 注册按钮 -->
              <el-form-item>
                <el-button
                  class="btn_login"
                  type="success"
                  block
                  :loading="loading"
                  :disabled="passwordMismatch"
                  @click="handleSubmit"
                >
                  立即注册
                </el-button>
              </el-form-item>

              <!-- 已有账号 -->
              <div class="account-oprate">
                <span>已有账号？</span>
                <router-link to="/login">立即登录</router-link>
              </div>
            </el-form>
          </div>
        </div>
      </div>
      <div class="login_footer"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import axios from 'axios';
import store from "../store.js";
import router from "../router.js";
import { ElNotification } from "element-plus";

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
/* 共用登录页面样式 */
/* 基础布局 */
.outer-layer {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.outer-layer-content {
  width: 100%;
  max-width: 1200px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 内容容器 */
.content_box {
  display: flex;
  min-height: 600px;
}

/* 左侧图片区域 */
.content_left {
  flex: 1;
  background: url("https://source.unsplash.com/random/800x600?technology")
    center/cover;
  display: none; /* 默认隐藏，PC端显示 */
}

/* 右侧表单区域 */
.content_right {
  flex: 1;
  padding: 40px 30px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

h2 {
  text-align: center;
  color: #2c3e50;
  font-size: 28px;
  margin-bottom: 40px;
  font-weight: 600;
}

/* 表单样式 */
.cr_top {
  max-width: 400px;
  margin: 0 auto;
  width: 100%;
}

.el-form-item {
  margin-bottom: 28px;
}

.el-input {
  width: 100%;
}

.el-input :deep(.el-input__wrapper) {
  padding: 12px 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.el-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #3ec474 inset;
}

/* 登录按钮 */
.btn_login {
  width: 100%;
  height: 48px;
  font-size: 16px;
  background-color: #3ec474;
  border: none;
  transition: all 0.3s ease;
}

.btn_login:hover {
  background-color: #34a862;
  transform: translateY(-2px);
}

/* 账号操作链接 */
.account-oprate {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.account-oprate a {
  color: #666;
  font-size: 14px;
  text-decoration: none;
  transition: color 0.3s ease;
}

.account-oprate a:hover {
  color: #3ec474;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .outer-layer {
    padding: 10px;
    background: #fff;
  }

  .outer-layer-content {
    border-radius: 12px;
  }

  .content_box {
    min-height: auto;
  }

  .content_right {
    padding: 30px 20px;
  }

  h2 {
    font-size: 24px;
    margin-bottom: 30px;
  }

  .el-form-item {
    margin-bottom: 20px;
  }

  .btn_login {
    height: 44px;
  }

  .account-oprate {
    flex-direction: column;
    align-items: center;
    gap: 12px;
  }
}

/* PC端显示左侧图片 */
@media (min-width: 992px) {
  .content_left {
    display: block;
  }

  .content_right {
    padding: 60px;
  }
}

/* 底部装饰 */
.login_footer {
  height: 40px;
  background: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  font-size: 12px;
}
/* 注册页专属调整 */
.content_right {
  padding-top: 60px;
}

.el-form-item {
  margin-bottom: 22px;
}

.account-oprate {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.account-oprate span {
  margin-right: 8px;
}

.account-oprate a {
  color: #3ec474;
  text-decoration: none;
  font-weight: 500;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .content_right {
    padding-top: 40px;
  }

  .el-form-item {
    margin-bottom: 18px;
  }
}
</style>