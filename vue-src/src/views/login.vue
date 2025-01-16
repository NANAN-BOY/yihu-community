<template>
  <div class="outer-layer">
    <div class="outer-layer-content">
      <div class="content_box">
        <div class="content_left"></div>
        <div class="content_right">
          <h2>智能管理系统Beta</h2>
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
                <a href="javascript:;">找回密码</a>
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
import { ElNotification } from 'element-plus';  // 导入 Element Plus 消息组件
import { useRouter } from 'vue-router';

// 定义响应式变量
const userPhoneNumber = ref('');  // 用户手机号
const userPassword = ref('');  // 用户密码
const loading = ref(false);  // 控制登录按钮加载状态
const router = useRouter();

const store = useStore();  // 获取 Vuex store 实例

// 处理表单提交
const handleSubmit = async () => {
  loading.value = true;  // 开始加载状态

  try {
    // 创建请求体（JSON 格式）
    const requestBody = {
      user_phoneNumber: userPhoneNumber.value,
      user_password: userPassword.value,
    };

    // 使用 Fetch API 发送 POST 请求
    const response = await fetch('http://localhost:3001/api/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json', // 设置请求头为 JSON 格式
      },
      body: JSON.stringify(requestBody), // 发送 JSON 格式的数据
    });

    const data = await response.json();

    if (data.status === 'error') {
      // 使用 ElNotification 显示错误信息
      ElNotification({
        title: '登录失败',
        message: data.message,
        type: 'error',
        duration: 3000, // 自动关闭时间
      });
    } else if (data.status === 'success') {
      // 使用 ElNotification 显示成功信息
      ElNotification({
        title: '登录成功',
        message: '欢迎回来！',
        type: 'success',
        duration: 3000, // 自动关闭时间
      });

      // 将 token 和用户信息存入 Vuex
      await store.dispatch('setToken', data.token);
      await store.dispatch('setUser', {
        user_id: data.user.user_id,
        user_name: data.user.user_name,
        user_phoneNumber: data.user.user_phoneNumber,
        user_role: data.user.user_role,
        user_accountStatus: data.user.user_accountStatus,
      });

      // 将 token 存入 localStorage
      localStorage.setItem('token', data.token);
      await router.push('/dashboard');
    }
  } catch (error) {
    console.error('Error:', error);
    // 显示错误通知
    ElNotification({
      title: '提交失败',
      message: '请稍后重试。',
      type: 'error',
      duration: 3000,
    });
  } finally {
    loading.value = false;  // 请求完成后停止加载
  }
};
</script>

<style scoped>
/* Global styles */
body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  margin: 0;
  padding: 0;
  background-color: #f2f4f3;
}

.outer-layer {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f2f4f3;
}

.outer-layer-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 480px; /* Set max-width to prevent it from getting too wide on large screens */
  padding: 40px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.content_left {
  display: none; /* Hide the left side */
}

.content_right {
  width: 100%;
  padding: 20px;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  font-size: 26px; /* Slightly larger for better readability */
  color: #333;
}

.ct_input {
  margin-bottom: 20px;
}

.input_text {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 6px;
  box-sizing: border-box;
  transition: border-color 0.3s ease;
}

.input_text:focus {
  border-color: #1CB278;
  outline: none;
}

.btn_login {
  width: 100%;
  padding: 14px;
  background-color: #1CB278;
  border: none;
  color: white;
  font-size: 18px;
  cursor: pointer;
  border-radius: 6px;
  transition: background-color 0.3s ease;
}

.btn_login:hover {
  background-color: #3cc18a;
}

.btn_login:active {
  background-color: #13996a;
}

.account-oprate {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
}

.account-oprate a {
  font-size: 14px;
  color: #1CB278;
  text-decoration: none;
  transition: color 0.3s ease;
}

.account-oprate a:hover {
  color: #3cc18a;
}

.error-message {
  color: red;
  font-size: 14px;
  margin-top: 10px;
}

.success-message {
  color: green;
  font-size: 14px;
  margin-top: 10px;
}

/* Responsive styles */
@media (max-width: 768px) {
  .outer-layer-content {
    width: 90%;
    padding: 30px;
  }

  h2 {
    font-size: 24px;
  }

  .input_text, .btn_login {
    font-size: 16px;
  }

  .account-oprate a {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .outer-layer-content {
    width: 95%;
    padding: 25px;
  }

  h2 {
    font-size: 22px;
  }

  .input_text, .btn_login {
    font-size: 14px;
  }

  .account-oprate a {
    font-size: 12px;
  }
}
</style>
