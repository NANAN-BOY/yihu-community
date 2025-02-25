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
// 修改引入部分
import { ref, computed } from 'vue';
import { ElNotification, ElMessage } from 'element-plus';
import axios from "axios";
import store from "../store";
import router from "../router"; // 添加ElMessage

// 表单数据（移除passwordMismatch的ref声明）
const user_name = ref('');
const user_phoneNumber = ref('');
const user_password = ref('');
const user_password_confirm = ref('');

// 改为计算属性
const passwordMismatch = computed(() =>
  user_password.value !== user_password_confirm.value
);

// 优化后的提交方法
const handleSubmit = async () => {
  // 密码一致性检查
  if (passwordMismatch.value) {
    ElMessage.error('两次输入的密码不一致，请重新输入');
    return;
  }

  // 必填字段校验
  if (!user_name.value?.trim()) {
    ElMessage.warning('请输入组织名称');
    return;
  }

  if (!/^1[3-9]\d{9}$/.test(user_phoneNumber.value)) {
    ElMessage.warning('请输入有效的手机号码');
    return;
  }

  if (user_password.value.length < 6) {
    ElMessage.warning('密码长度不能少于6位');
    return;
  }

  try {
    // 保留原有注册逻辑...
    const response = await axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/register`, {
      user_name: user_name.value.trim(),
      user_phoneNumber: user_phoneNumber.value.trim(),
      user_password: user_password.value
    });

    // 成功后的本地存储操作
    localStorage.setItem('token', response.data.token);
    await store.dispatch('setToken', response.data.token);
    await store.dispatch('setUser', response.data.user);

    ElMessage.success('注册成功！欢迎使用。')

    await router.push('/dashboard');

  } catch (error) {
    // 优化错误处理
    const errorMsg = error.response?.data?.message ||
                     '注册失败，请检查网络后重试';
    ElMessage({
      message: errorMsg,
      type: 'error',
      duration: 3000,
      showClose: true
    });
    console.error('注册错误:', error);
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
