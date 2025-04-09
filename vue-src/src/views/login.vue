<template>
  <div class="outer-layer">
    <div class="outer-layer-content">
      <div class="content_box">
        <div class="content_left"></div>
        <div class="content_right">
          <h2>欢迎登录易互</h2>
          <div class="cr_top">
            <el-form @submit.prevent="handleSubmit" label-width="0px">
              <!-- 手机号输入框 -->
              <el-form-item>
                <el-input
                  v-model="userPhoneNumber"
                  type="tel"
                  placeholder="请输入手机号"
                  required
                  clearable
                  prefix-icon="PhoneFilled"
                ></el-input>
              </el-form-item>

              <!-- 密码输入框 -->
              <el-form-item>
                <el-input
                  v-model="userPassword"
                  type="password"
                  placeholder="请输入密码"
                  required
                  clearable
                  prefix-icon="Key"
                ></el-input>
              </el-form-item>

              <!-- 登录按钮 -->
              <el-form-item>
                <el-button
                  class="btn_login"
                  type="success"
                  block
                  :loading="loading"
                  @click="handleSubmit"
                >
                  登录
                </el-button>
              </el-form-item>

              <!-- 账号操作区域 -->
              <div class="account-oprate">
                <router-link to="/register">申请账号</router-link>
                <router-link to="/resetPassword" >找回密码</router-link>
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
import { ref } from 'vue';
import { useStore } from 'vuex';  // 导入 Vuex store
import {ElMessage} from 'element-plus';  // 导入 Element Plus 消息组件
import { useRouter } from 'vue-router';
import request from "../utils/request.js";
// 定义响应式变量
const userPhoneNumber = ref('');  // 用户手机号
const userPassword = ref('');  // 用户密码
const loading = ref(false);  // 控制登录按钮加载状态
const router = useRouter();

const store = useStore();  // 获取 Vuex store 实例
// request.get('api/login').then(res =>{
//   alert("111")
//   alert(res)
//   console(res)
// })


// 处理表单提交
const handleSubmit = async () => {
  loading.value = true;
  try {
    const queryParams = new URLSearchParams({
      phone: userPhoneNumber.value,
      password: userPassword.value,
    });
    const url = `${import.meta.env.VITE_BACKEND_IP}/api/user/login?${queryParams.toString()}`;
    const response = await fetch(url, {
      method: 'POST',
    });
    const data = await response.json();
    if (data.msg === 'error') {
      ElMessage.error("账号或密码错误");
    } else if (data.msg === 'success') {
      await store.dispatch('setToken', data.token);
      await store.dispatch('setUser', data.data);
      localStorage.setItem('token', data.token);
      console.log(store.state)
      ElMessage.success('登录成功');
      await router.push('/dashboard');
    }
  } catch (error) {
    console.error('Error:', error);
    ElMessage.error('登录失败');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
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
  box-shadow: 0 0 0 1px #007BFF inset;
}

/* 登录按钮 */
.btn_login {
  width: 100%;
  height: 48px;
  font-size: 16px;
  background-color: #007BFF;
  border: none;
  transition: all 0.3s ease;
}

.btn_login:hover {
  background-color: #0058b9;
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
  color: #007BFF;
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
</style>
