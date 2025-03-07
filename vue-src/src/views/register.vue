<template>
  <div class="outer-layer">
    <div class="outer-layer-content">
      <div class="content_box">
        <div class="content_left"></div>
        <div class="content_right">
          <h2>注册易互平台</h2>
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
                <div class="phone-input-wrapper">
                  <el-input
                      v-model="user_phoneNumber"
                      type="tel"
                      placeholder="请输入管理者手机号"
                      clearable
                      prefix-icon="PhoneFilled"
                  ></el-input>
                  <el-button
                      class="send-code-btn"
                      :disabled="countdown > 0"
                      @click="sendSMSCode">
                    {{ countdown > 0 ? `${countdown}秒后重发` : '获取验证码' }}
                  </el-button>
                </div>
              </el-form-item>

              <!-- 验证码 -->
              <el-form-item>
                <el-input
                    v-model="sms_code"
                    placeholder="请输入短信验证码"
                    clearable
                    prefix-icon="Message"
                ></el-input>
              </el-form-item>
              <el-form-item>
                <el-cascader
                    v-model="selectedArea"
                    :options="areaOptions"
                    :props="areaProps"
                    :show-all-levels="true"
                    placeholder="请选择所在地区"
                    clearable
                    style="width: 100%"
                />
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
import { ref, computed, onMounted } from 'vue';
import { ElNotification, ElMessage } from 'element-plus';
import axios from "axios";
import store from "../store";
import router from "../router";
import { regionData } from 'element-china-area-data';

const user_name = ref('');
const user_phoneNumber = ref('');
const user_password = ref('');
const user_password_confirm = ref('');
const sms_code = ref('');
const countdown = ref(0);

let timer = null;
// 新增地区相关响应式变量
const selectedArea = ref([]);
const areaOptions = ref(regionData);
const areaProps = {
  value: 'value',
  label: 'label',
  children: 'children',
  emitPath: false,
};
const passwordMismatch = computed(() =>
  user_password.value !== user_password_confirm.value
);

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

  // 新增验证码校验
  if (!sms_code.value?.trim() || sms_code.value.length !== 6) {
    ElMessage.warning('请输入6位验证码');
    return;
  }

  if (user_password.value.length < 6) {
    ElMessage.warning('密码长度不能少于6位');
    return;
  }
  if (selectedArea.value.length !== 6) {
    ElMessage.warning('请选择完整的省市区划');
    return;
  }
  try {
    const response = await axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/register`, null, {
      params: {
        userName: user_name.value,
        password: user_phoneNumber.value,
        phoneNumber: user_password.value,
        captcha: sms_code.value,
        location: selectedArea.value
      }
    });
    console.log(response.data);
    console.log( user_name.value,
        user_phoneNumber.value,
         user_password.value,
         sms_code.value,
         selectedArea.value);
      const queryParams = new URLSearchParams({phone: user_phoneNumber.value, password: user_password.value,});
      const url = `${import.meta.env.VITE_BACKEND_IP}/api/login?${queryParams.toString()}`;
      const response1 = await fetch(url, {method: 'POST',});
      const data = await response1.json();
      console.log(data);
      if (data.msg === 'error') {
        ElNotification({
          title: '登录失败',
          message: data.message,
          type: 'error',
          duration: 3000, // 自动关闭时间
        });
      } else if (data.msg === 'success') {
        await store.dispatch('setToken', data.token);
        await store.dispatch('setUser', data.data);
        localStorage.setItem('token', data.token);
        console.log(store.state)
        ElNotification({
          title: '登录成功',
          message: '欢迎！',
          type: 'success',
          duration: 3000,
        });
        await router.push('/dashboard');
      }
  } catch (error) {
    const errorMsg = error.response?.data?.message ||
        '注册失败，请检查网络后重试';
    ElMessage({
      message: errorMsg,
      type: 'error',
      duration: 3000,
      showClose: true
    });
  }
};
const sendSMSCode = async () => {
  if (!/^1[3-9]\d{9}$/.test(user_phoneNumber.value)) {
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
    await axios.post(`${import.meta.env.VITE_BACKEND_IP}/captcha/generate`, null, {
      params: {
        phone: user_phoneNumber.value
      }
    });
    console.log(user_phoneNumber.value);
    ElMessage.success('验证码已发送');
  } catch (error) {
    ElMessage.error('验证码发送失败');
    console.error('验证码发送错误:', error);
    clearInterval(timer);
    countdown.value = 0;
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

.content_left {
  flex: 1;
  background: url("htgl-bg.png") center/cover;
  background-size: 85%;  /* 缩小背景图片尺寸 */
  background-repeat: no-repeat;  /* 禁止重复 */
  display: none;
  background-color: #ffffff;  /* 添加备用背景色 */
}

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

/* 表单通用样式 */
.cr_top {
  max-width: 400px;
  margin: 0 auto;
  width: 100%;
}

.el-form-item {
  margin-bottom: 28px;
}


/* 新增级联选择器内部输入框精准调整 */
.el-cascader :deep(.el-input) {
  --el-input-height: 40px;
  --el-input-inner-height: calc(var(--el-input-height) - 2px) !important;
}
.el-cascader :deep(.el-input__inner) {
  height: 100% !important;
  line-height: 38px !important; /* 精确对齐其他输入框文本 */
}
.el-cascader :deep(.el-input__suffix) {
  top: 50% !important;
  transform: translateY(-50%);
}

/* 按钮样式 */
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

/* 辅助链接 */
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

/* 手机号输入容器 */
.phone-input-wrapper {
  display: flex;
  width: 100%;
  align-items: center;
  gap: 10px;
}

.phone-input-wrapper :deep(.el-input) {
  flex: 1;
  min-width: 0;
}

.phone-input-wrapper :deep(.el-input__inner) {
  width: 100% !important;
}

.phone-input-wrapper :deep(.el-input__suffix) {
  right: 8px;
  position: absolute;
}

/* 验证码按钮 */
.send-code-btn {
  width: 120px;
  height: 40px;
  padding: 0 15px;
  border-radius: 6px;
  transition: all 0.3s;
  white-space: nowrap;
  background-color: #f5f7fa;
  border-color: #dcdfe6;
}

.send-code-btn:hover {
  background-color: #3ec474;
  color: white;
  border-color: #3ec474;
}

.send-code-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 级联选择器专用样式 */
.el-cascader :deep(.el-input__inner) {
  line-height: 40px;
}

.el-cascader :deep(.el-icon) {
  font-size: 14px;
  margin-top: -2px;
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

  /* 移动端输入框高度调整 */
  .el-input :deep(.el-input__wrapper),
  .el-cascader :deep(.el-input__wrapper) {
    height: 36px !important;
  }

  .el-cascader :deep(.el-input__inner) {
    line-height: 36px;
  }

  /* 级联选择器弹窗优化 */
  .el-cascader__dropdown {
    position: fixed !important;
    top: 50% !important;
    left: 50% !important;
    transform: translate(-50%, -50%);
    max-width: 90vw;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }

  .el-cascader-panel {
    max-height: 60vh !important;
    overflow-y: auto;
    flex-direction: column;
  }

  .el-cascader-menu {
    min-width: 100px !important;
    height: auto !important;
    border-right: 1px solid #eee;
  }

  .el-cascader-node__label {
    font-size: 14px;
    padding: 8px 12px;
    white-space: normal;
  }
}

/* PC端图片显示 */
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
</style>

