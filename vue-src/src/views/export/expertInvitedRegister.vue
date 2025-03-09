<template>
  <div class="container">
    <div class="card">
      <h1 class="title">邀请信息</h1>

      <!-- 显示邀请信息 -->
      <div v-if="inviteInfo" class="invite-info">
        <p>
          邀请人: <strong>{{ inviteInfo.invite_user_name }}</strong>
        </p>
        <p v-if="remainingTime">
          剩余时间: <strong>{{ remainingTime }}</strong>
        </p>
        <!-- 绑定拒绝邀请的函数 -->
        <el-button type="danger" @click="refuseInvitation">拒绝</el-button>
        <el-button type="success" @click="dialogVisible = true"
          >接受邀请</el-button
        >
        <p v-if="expired" class="expired">该邀请已过期</p>
      </div>

      <!-- 显示加载状态 -->
      <p v-if="loading" class="loading">正在加载邀请信息...</p>

      <!-- 没有邀请信息时的提示 -->
      <p v-if="!inviteInfo && !loading" class="no-invite">
        没有邀请信息，请与管理员联系。
      </p>

      <!-- 查询失败时的提示 -->
      <p v-if="queryFailed && !loading" class="no-invite">
        邀请信息查询失败，请稍后重试。
      </p>

      <!-- 注册信息弹框 -->
      <el-dialog
        v-model="dialogVisible"
        title="填写注册信息"
        width="500"
        :before-close="handleClose"
      >
        <!-- 用户名 -->
        <el-input
          v-model="userName"
          style="width: 240px"
          placeholder="用户名"
        />
        <!-- 手机号 -->
        <el-input
          v-model="userPhoneNumber"
          style="width: 240px"
          placeholder="手机号"
        />
        <!-- 密码 -->
        <el-input
          v-model="password"
          type="password"
          style="width: 240px"
          placeholder="密码"
        />
        <!-- 确认密码 -->
        <el-input
          v-model="confirmPassword"
          type="password"
          style="width: 240px"
          placeholder="确认密码"
        />

        <!-- 错误消息 -->
        <p v-if="passwordError" class="error-message">密码和确认密码不匹配</p>

        <template #footer>
          <div class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit">提交</el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import router from "../../router";
import store from "../../store";
import axios from "axios";

const inviteId = ref(null);
const inviteInfo = ref(null);
const remainingTime = ref(null);
const expired = ref(false);
const loading = ref(false);
const queryFailed = ref(false);
const dialogVisible = ref(false);

// 用户输入数据
const userName = ref('');
const userPhoneNumber = ref('');
const password = ref('');
const confirmPassword = ref('');
const passwordError = ref(false);

// 获取路由参数
const route = useRoute();
inviteId.value = route.params.inviteId;

// 监控路由变化
watch(route, (to) => {
  inviteId.value = to.params.inviteId;
  fetchInviteInfo();
});

// 获取邀请信息
const fetchInviteInfo = async () => {
  loading.value = true;
  queryFailed.value = false;
  try {
    const response = await axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/expert/get-record`, {
      params: {
        id: inviteId.value
      }
    });
    const data = response;
    console.log(data);
    if (data.data.code == 200 && data.data.data.id) {
      inviteInfo.value = data; // 保存邀请信息

      // 计算剩余时间
      const now = new Date();
      const inviteDeadline = new Date(data.data.data.deadline);
      const diffTime = inviteDeadline - now; // 毫秒差

      if (diffTime > 0) {
        // 如果邀请未过期，计算剩余时间
        const hours = Math.floor(diffTime / (1000 * 60 * 60));
        const minutes = Math.floor((diffTime % (1000 * 60 * 60)) / (1000 * 60));
        remainingTime.value = `${hours}小时 ${minutes}分钟`;
        expired.value = false;
      } else {
        // 如果邀请已过期
        expired.value = true;
        remainingTime.value = null;
      }
    } else {
      // 如果没有邀请信息或请求失败
      queryFailed.value = true;
    }
  } catch (error) {
    console.error('API 请求失败:', error);
    queryFailed.value = true;
  } finally {
    loading.value = false; // 结束加载状态
  }
};

// 组件挂载时获取邀请信息
onMounted(() => {
  fetchInviteInfo();
});

const handleSubmit = async () => {
  // 验证密码和确认密码是否一致
  if (password.value !== confirmPassword.value) {
    passwordError.value = true;
    return;
  }

  // 如果密码一致，提交到后端 API
  try {
    const response = await fetch(`${import.meta.env.VITE_BACKEND_IP}/api/ExpertLibrary/expertRegister`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        inviteId: inviteId.value,
        userName: userName.value,
        userPhoneNumber: userPhoneNumber.value,
        userPassword: password.value,
      }),
    });

    const data = await response.json();

    if (response.ok) {
      ElMessage.success('注册成功！');
      dialogVisible.value = false; // 关闭对话框
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
      // 跳转到首页或用户的 Dashboard
      await router.push('/dashboard');
    } else {
      ElMessage.error(data.message || '注册失败，请重试');
    }
  } catch (error) {
    console.error('注册请求失败:', error);
    ElMessage.error('注册请求失败，请稍后重试');
  }
};

// 拒绝邀请的函数
const refuseInvitation = async () => {
  try {
    const response = await fetch(`${import.meta.env.VITE_BACKEND_IP}/api/ExpertLibrary/expertRefuseInvitation`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        "inviteSpecialisRrecord_id": inviteId.value,
        "expertRefuseInvitationReason": "用户拒绝邀请"
      })
    });

    const data = await response.json();

    if (response.ok) {
      ElMessage.success(data.message);
      // 可以在这里做一些后续处理，比如清除邀请信息
      inviteInfo.value = null;
      remainingTime.value = null;
      expired.value = false;
    } else {
      ElMessage.error(data.message || '拒绝邀请失败，请重试');
    }
  } catch (error) {
    console.error('拒绝邀请请求失败:', error);
    ElMessage.error('拒绝邀请请求失败，请稍后重试');
  }
};

// 关闭对话框
const handleClose = () => {
  userName.value = '';
  userPhoneNumber.value = '';
  password.value = '';
  confirmPassword.value = '';
  passwordError.value = false;
};
</script>

<style scoped>
/* 样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 页面容器 */
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f4f7fc;
}

/* 卡片样式 */
.card {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 90%;
  max-width: 500px;
  text-align: center;
}

/* 标题样式 */
.title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
}

/* 邀请信息样式 */
.invite-info {
  font-size: 18px;
  color: #333;
  margin-bottom: 20px;
}

/* 剩余时间和过期样式 */
.remaining-time {
  font-size: 18px;
  color: #28a745; /* Green color */
}

.expired {
  font-size: 18px;
  color: #dc3545; /* Red color */
}

/* 加载中的提示样式 */
.loading {
  font-size: 16px;
  color: #007bff;
}

/* 没有邀请信息时的提示 */
.no-invite {
  font-size: 16px;
  color: #dc3545;
  font-weight: bold;
}

/* 错误提示样式 */
.error-message {
  color: red;
  font-size: 14px;
  margin-top: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .card {
    padding: 15px;
    width: 100%;
  }
}

@media (max-width: 480px) {
  .title {
    font-size: 20px;
  }
  .invite-info,
  .loading,
  .no-invite {
    font-size: 14px;
  }
}
</style>