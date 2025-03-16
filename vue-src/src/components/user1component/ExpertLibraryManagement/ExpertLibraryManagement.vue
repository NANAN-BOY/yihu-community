<template>
  <el-breadcrumb separator="/">
    <el-breadcrumb-item><strong>专家库管理</strong></el-breadcrumb-item>
  </el-breadcrumb>
  <br />
  <el-button type="primary" @click="OpenInviteComponent">邀请专家</el-button>&nbsp;
  <!-- 创建邀请弹窗 -->
  <el-dialog title="创建邀请链接" v-model="dialogVisible" width="380px" @close="CloseCreateComponent">
    <div>
      <p>邀请有效时间</p>
      <el-select v-model="value" placeholder="3天" size="large" style="width: 220px">
        <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"/>
      </el-select>
    </div><br />
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="CloseCreateComponent">取消</el-button>
      <el-button type="success" @click="CreateInviteURL">确认邀请</el-button>
    </span>
  </el-dialog>
  <!-- 显示邀请链接和二维码的弹窗 -->
  <el-dialog title="邀请链接与二维码" v-model="inviteUrlDialogVisible" width="380px" @close="CloseInviteUrlDialog">
    <div>
      <p>邀请链接已成功生成：</p>
      <el-input v-model="inviteUrl" type="text" readonly style="width: 100%;" />
      <el-button @click="copyInviteUrl" size="mini" style="margin-top: 10px;">复制链接</el-button>
      <div style="text-align: center; margin-top: 20px;">
        <QRCode :value="inviteUrl" size="200" />
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
    <el-button type="primary" @click="CloseInviteUrlDialog">关闭</el-button>
  </span>
  </el-dialog>
  <h1>当前在职专家</h1>
  <!-- 专家卡片列表 -->
  <div class="expert-list">
    <div v-for="user in users" :key="user.user_id" class="expert-card" @click="viewExpertDetails(user.user_id)">
      <div class="card-header">
        <span class="card-title">{{ user.user_name }}</span>
      </div>
      <div class="card-body">
        <p><strong>用户 ID:</strong> {{ user.user_id }}</p>
      </div>
    </div>
  </div>
  <div class="infinite-list-wrapper" style="overflow: auto">
    <ul
        v-infinite-scroll="load"
        class="list"
        :infinite-scroll-disabled="disabled"
    >
      <li v-for="i in count" :key="i" class="list-item">{{ i }}</li>
    </ul>
    <p v-if="loading">Loading...</p>
    <p v-if="noMore">No more</p>
  </div>
  <!-- 查看专家详细信息的弹窗 -->
  <el-dialog title="专家详细信息" v-model="expertDialogVisible" width="50%">
    <div v-if="expertDetails">
      <p><strong>专家姓名:</strong> {{ expertDetails.user_name }}</p>
      <p><strong>用户 ID:</strong> {{ expertDetails.user_id }}</p>
      <p><strong>注册手机号:</strong> {{ expertDetails.user_phoneNumber }}</p>
      <p><strong>邀请人:</strong> {{ expertDetails.inviteUserInfo.invite_user_name }}</p>
      <p><strong>加入时间:</strong> {{ formatDate(expertDetails.user_createDate) }}</p>
      <!-- 这里可以继续展示更多专家的详细信息 -->
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="closeExpertDialog">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script setup>
import {ref, onMounted, computed} from 'vue';
import { ElDialog, ElButton, ElSelect, ElOption, ElInput, ElMessage } from 'element-plus';
import axios from 'axios';
import store from "../../../store";
import QRCode from 'qrcode.vue';

const dialogVisible = ref(false);
const inviteUrlDialogVisible = ref(false);
const inviteUrl = ref('');
const users = ref([]);
const expertDialogVisible = ref(false);
const expertDetails = ref(null);

const count = ref(10)
const loading = ref(false)
const noMore = computed(() => count.value >= 20)
const disabled = computed(() => loading.value || noMore.value)
const load = () => {
  loading.value = true
  setTimeout(() => {
    count.value += 2
    loading.value = false
  }, 2000)
}



// 格式化时间，去掉毫秒和时区
const formatDate = (date) => {
  const newDate = new Date(date);

  const year = newDate.getFullYear();
  const month = newDate.getMonth() + 1; // 获取月份，注意月份是从0开始的
  const day = newDate.getDate();
  const hour = newDate.getHours();
  const minute = newDate.getMinutes();

  // 格式化为 "2024年12月1日 21:32"
  return `${year}年${month}月${day}日 ${hour}:${minute < 10 ? '0' + minute : minute}`;
};


const copyInviteUrl = () => {
  const input = document.createElement('input');
  input.value = inviteUrl.value;
  document.body.appendChild(input);
  input.select();
  document.execCommand('copy');
  document.body.removeChild(input);

  // 显示提示信息
  ElMessage.success('邀请链接已复制成功！');
};


const fetchUsers = async () => {
  try {
    const response = await axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/users/list?user_role=4&user_accountStatus=1`);
    if (response.data.users) {
      users.value = response.data.users;
    } else {
      ElMessage.error('未找到用户信息');
    }
  } catch (error) {
    console.error('获取用户列表失败:', error);
    ElMessage.error('获取用户列表失败，请稍后重试');
  }
};

onMounted(() => {
  fetchUsers();
});

const OpenInviteComponent = () => {
  dialogVisible.value = true;
};

const CloseCreateComponent = () => {
  dialogVisible.value = false;
};

const CloseInviteUrlDialog = () => {
  inviteUrlDialogVisible.value = false;
};

const value = ref('3');
const options = [
  { value: '1', label: '1天' },
  { value: '3', label: '3天' },
  { value: '7', label: '7天' },
  { value: '30', label: '30天' }
];

const calculateDeadline = () => {
  const currentDate = new Date();
  const deadlineDate = new Date(currentDate);
  deadlineDate.setDate(currentDate.getDate() + parseInt(value.value));
  return deadlineDate.toISOString().slice(0, 19);
};

const CreateInviteURL = async () => {
  const inviteDeadline = calculateDeadline();
  const inviteUserId = store.state.user.id;
  const inviteData = {
    inviteUserId: inviteUserId,
    createAt: new Date(Date.now()).toISOString().replace('T', ' ').slice(0, 19),
    deadLine: inviteDeadline.replace('T', ' ').slice(0, 19)
  };
  try {
    const response = await axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/expert/create-record`, null, {
      params: inviteData,
      headers: {'token': `${store.state.token}`}
    });
    if (response.status !== 200) {throw new Error(`HTTP错误 ${response.status}`);}
    if (response.data?.code !== 200) {throw new Error(`业务错误: ${response.data?.message || '未知错误'}`);}
    const inviteId = response.data.data;
    inviteUrl.value = `${window.location.origin}/expertInvitedRegister/${inviteId}`;
    CloseCreateComponent();
    inviteUrlDialogVisible.value = true;
  } catch (error) {
    console.error('邀请失败:', error);
    ElMessage.error('邀请失败，请稍后重试');
  }
};

const viewExpertDetails = async (userId) => {
  try {
    // 同时发起两个请求，一个获取专家信息，一个获取邀请人信息
    const [userResponse, inviteUserResponse] = await Promise.all([
      axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/users/getUserInfo/${userId}`),
      axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/ExpertLibrary/inviteUserInfo/${userId}`)
    ]);

    // 判断专家信息是否存在
    if (userResponse.data) {
      // 获取到专家的详细信息
      expertDetails.value = userResponse.data.user;
    } else {
      ElMessage.error('未找到专家详细信息');
      return;
    }

    // 判断邀请人信息是否存在
    if (inviteUserResponse.data) {
      // 获取到邀请人的信息并保存到专家详细信息中
      expertDetails.value.inviteUserInfo = inviteUserResponse.data;
    } else {
      ElMessage.error('未找到邀请人信息');
    }

    // 显示专家对话框
    expertDialogVisible.value = true;
  } catch (error) {
    console.error('获取专家详细信息失败:', error);
    ElMessage.error('获取专家详细信息失败，请稍后重试');
  }
};


// 关闭专家详细信息弹窗
const closeExpertDialog = () => {
  expertDialogVisible.value = false;
  expertDetails.value = null;
};
</script>

<style scoped>
/* 专家卡片列表样式 */
.expert-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 20px;
}

.expert-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  width: 240px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.expert-card:hover {
  background-color: #f5f5f5;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
}

.card-body {
  font-size: 14px;
  color: #666;
}

.infinite-list-wrapper {
  height: 300px;
  text-align: center;
}

.infinite-list-wrapper .list {
  padding: 0;
  margin: 0;
  list-style: none;
}

.infinite-list-wrapper .list-item {
  display: flex;
  align-items: center;
//justify-content: center; height: 50px;
  background: var(--el-color-danger-light-9);
  color: var(--el-color-danger);
}

.infinite-list-wrapper .list-item + .list-item {
  margin-top: 10px;
}
</style>
