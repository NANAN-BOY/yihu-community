<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';

// 表单数据
const phone = ref('');
const captcha = ref('');
const newPassword = ref('');
const confirmPassword = ref('');
const captchaError = ref(false);
const passwordError = ref(false);

// 验证码相关
const captchaSent = ref(false);
const countdown = ref(0);
let timer = null;

// 发送验证码
const sendCaptcha = async () => {
  if (!/^1[3-9]\d{9}$/.test(phone.value)) {
    ElMessage.warning('请输入有效的手机号码');
    return;
  }

  try {
    countdown.value = 60;
    timer = setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0) {
        clearInterval(timer);
      }
    }, 1000);

    await axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/user/captcha/generate`, null, {
      params: {
        phone: phone.value
      }
    });

    ElMessage.success('验证码已发送');
    captchaSent.value = true;
  } catch (error) {
    ElMessage.error('验证码发送失败');
    console.error('验证码发送错误:', error);
    clearInterval(timer);
    countdown.value = 0;
  }
};
// 开始倒计时
const startCountdown = () => {
  countdown.value = 60;
  timer = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--;
    } else {
      clearInterval(timer);
    }
  }, 1000);
};

// 提交重置密码
const submitReset = async () => {
  // 验证密码是否一致
  if (newPassword.value !== confirmPassword.value) {
    passwordError.value = true;
    return;
  }
  console.log(phone.value);
  console.log(captcha.value);
  console.log(newPassword.value);
  console.log(confirmPassword.value);
  try {
    const response = await axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/user/reset-password`, null, {
      params: {
        phone: phone.value,
        captcha: captcha.value,
        newPassword: newPassword.value
      }
    });
  console.log(response);
    if (response.data.code === 200) {
      ElMessage.success('密码重置成功');
      // 重置表单
      phone.value = '';
      captcha.value = '';
      newPassword.value = '';
      confirmPassword.value = '';
      captchaSent.value = false;
    } else {
      ElMessage.error(response.data.msg || '密码重置失败');
    }
  } catch (error) {
    ElMessage.error('请求失败，请稍后重试');
  }
};
</script>

<template>
  <div class="container">
    <div class="card">
      <h1 class="title">找回密码</h1>

      <el-form label-width="80px">
        <!-- 手机号 -->
        <el-form-item label="手机号">
          <el-input v-model="phone" placeholder="请输入手机号" />
        </el-form-item>

        <!-- 验证码 -->
        <el-form-item label="验证码">
          <el-input v-model="captcha" placeholder="请输入验证码" />
          <el-button
            type="primary"
            @click="sendCaptcha"
            :disabled="captchaSent || countdown > 0"
          >
            {{ countdown > 0 ? `${countdown}秒后重试` : '获取验证码' }}
          </el-button>
        </el-form-item>

        <!-- 新密码 -->
        <el-form-item label="新密码">
          <el-input
            v-model="newPassword"
            type="password"
            placeholder="请输入新密码"
          />
        </el-form-item>

        <!-- 确认密码 -->
        <el-form-item label="确认密码">
          <el-input
            v-model="confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
          />
          <p v-if="passwordError" class="error-message">两次输入的密码不一致</p>
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="submitReset">提交</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f4f7fc;
}

.card {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 90%;
  max-width: 500px;
}

.title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.error-message {
  color: red;
  font-size: 12px;
  margin-top: 5px;
}
</style>
