<template>
  <el-breadcrumb separator="/">
    <el-breadcrumb-item><strong>专家库管理</strong></el-breadcrumb-item>
  </el-breadcrumb>
  <br/>
  <!-- 创建邀请 -->
  <el-button type="primary" @click="OpenCreateInviteLinkComponent">邀请专家</el-button>&nbsp;
  <el-dialog title="创建邀请链接" v-model="CreateInviteLinkDialogVisible" width="380px"
             @close="CloseCreateInviteLinkComponent">
    <div>
      <p>邀请有效时间</p>
      <el-select v-model="value" placeholder="3天" size="large" style="width: 220px">
        <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"/>
      </el-select>
    </div>
    <br/>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="CloseCreateInviteLinkComponent">取消</el-button>
      <el-button type="success" @click="CreateInviteURL">确认邀请</el-button>
    </span>
  </el-dialog>
  <!-- 显示邀请链接和二维码的弹窗 -->
  <el-dialog title="邀请链接与二维码" v-model="inviteUrlDialogVisible" width="380px" @close="CloseInviteUrlDialog">
    <div>
      <p>邀请链接已成功生成：</p>
      <el-input v-model="inviteUrl" type="text" readonly style="width: 100%;"/>
      <el-button @click="copyInviteUrl" size="mini" style="margin-top: 10px;">复制链接</el-button>
      <div style="text-align: center; margin-top: 20px;">
        <QRCode :value="inviteUrl" size="200"/>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
    <el-button type="primary" @click="CloseInviteUrlDialog">关闭</el-button>
  </span>
  </el-dialog>
  <!-- 邀请记录 -->
  <el-button type="primary" @click="OpenInviteRecordComponent">查看邀请记录</el-button>&nbsp;
  <el-dialog
      v-model="InviteRecordDialogVisible"
      title="邀请记录"
      width="1000"
      :before-close="handleClose"
      align-center
  >
    <span>This is a message</span>
    <template #footer>
      <div class="dialog-footer">
        <div class="infinite-list-wrapper" style="overflow: auto">
          <ul
              v-infinite-scroll="InviteHistoryRecordListload"
              class="list"
              :infinite-scroll-disabled="InviteHistoryRecordDisabled"
          >
            <li v-for="InviteHistoryRecord in InviteHistoryRecordList" :key="InviteHistoryRecord.id" class="list-item">
              <div>{{ InviteHistoryRecord.name }}</div>
            </li>
            <li v-if="InviteHistoryRecordLoading" v-loading="InviteHistoryRecordLoading" class="list-item"></li>
          </ul>
          <p v-if="InviteHistoryRecordLoading">加载中...</p>
          <p v-if="InviteHistoryRecordNoMore">没有更多数据了</p>
          <p v-if="InviteHistoryRecordError" style="color: red">{{ InviteHistoryRecordError }}</p>
        </div>
      </div>
    </template>
  </el-dialog>
  <!-- 当前在职专家无限滚动列表 -->
  <h1>当前在职专家</h1>
  <div class="infinite-list-wrapper" style="overflow: auto">
    <ul
        v-infinite-scroll="userListLoad"
        class="list"
        :infinite-scroll-disabled="disabled"
    >
      <li v-for="user in userList" :key="user.id" class="list-item">
        <div>{{ user.name }}</div>
      </li>
      <li v-if="loading" v-loading="loading" class="list-item"></li>
    </ul>
    <p v-if="loading">加载中...</p>
    <p v-if="noMore">没有更多数据了</p>
    <p v-if="error" style="color: red">{{ error }}</p>
  </div>
  <!-- 查看专家详细信息的弹窗 -->
  <el-dialog title="专家详细信息" v-model="expertDialogVisible" width="50%">
    <div v-if="expertDetails">
      <p><strong>专家姓名:</strong> {{ expertDetails.user_name }}</p>
      <p><strong>用户 ID:</strong> {{ expertDetails.user_id }}</p>
      <p><strong>注册手机号:</strong> {{ expertDetails.user_phoneNumber }}</p>
      <p><strong>邀请人:</strong> {{ expertDetails.inviteUserInfo.invite_user_name }}</p>
      <p><strong>加入时间:</strong> {{ formatDate(expertDetails.user_createDate) }}</p>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="closeExpertDialog">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script setup>
import {ref, onMounted, computed, reactive} from 'vue';
import {ElDialog, ElButton, ElSelect, ElOption, ElInput, ElMessage} from 'element-plus';
import axios from 'axios';
import store from "../../../store.js";
import QRCode from 'qrcode.vue';


const inviteUrl = ref('');
const users = ref([]);
const expertDialogVisible = ref(false);
const expertDetails = ref(null);

// 当前在职专家无限滚动列表所需数据
const userList = ref([])
const currentPage = ref(1)
const loading = ref(false)
const error = ref('')
const hasMore = ref(true)
const noMore = computed(() => !hasMore.value)
const disabled = computed(() => loading.value || noMore.value)
const userListLoad = async () => {
  if (disabled.value) return

  try {
    loading.value = true
    error.value = ''

    const response = await axios.get('http://localhost:8080/api/user/get-role', {
      params: {
        role: 4,
        pageNum: currentPage.value,
        pageSize: 10
      },
      headers: {
        token: store.state.token
      }
    })

    if (response.data.code === 200) {
      userList.value = [...userList.value, ...response.data.data.list]
      hasMore.value = response.data.data.hasNextPage
      currentPage.value++
    }
  } catch (err) {
    error.value = '数据加载失败，请稍后再试'
  } finally {
    loading.value = false
  }
}


// 已经接受的历史记录无限滚动列表所需数据
const InviteHistoryRecordList = ref([])
const InviteHistoryRecordCurrentPage = ref(1)
const InviteHistoryRecordLoading = ref(false)
const InviteHistoryRecordError = ref('')
const InviteHistoryRecordHasMore = ref(true)
const InviteHistoryRecordNoMore = computed(() => !InviteHistoryRecordHasMore.value)
const InviteHistoryRecordDisabled = computed(() => InviteHistoryRecordLoading.value || InviteHistoryRecordNoMore.value)
const InviteHistoryRecordListload = async () => {
  if (InviteHistoryRecordDisabled.value) return

  try {
    InviteHistoryRecordLoading.value = true
    InviteHistoryRecordError.value = ''

    const response = await axios.get('http://localhost:8080/api/user/get-role', {
      params: {
        role: 4,
        pageNum: InviteHistoryRecordCurrentPage.value,
        pageSize: 10
      },
      headers: {
        token: store.state.token
      }
    })

    if (response.data.code === 200) {
      InviteHistoryRecordList.value = [...InviteHistoryRecordList.value, ...response.data.data.list]
      InviteHistoryRecordHasMore.value = response.data.data.hasNextPage
      InviteHistoryRecordCurrentPage.value++
    }
  } catch (err) {
    InviteHistoryRecordError.value = '数据加载失败，请稍后再试'
  } finally {
    InviteHistoryRecordLoading.value = false
  }
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


const CreateInviteLinkDialogVisible = ref(false);
const inviteUrlDialogVisible = ref(false);
const OpenCreateInviteLinkComponent = () => {
  CreateInviteLinkDialogVisible.value = true;
};
const CloseCreateInviteLinkComponent = () => {
  CreateInviteLinkDialogVisible.value = false;
};
const CloseInviteUrlDialog = () => {
  inviteUrlDialogVisible.value = false;
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
const value = ref('3');
const options = [
  {value: '1', label: '1天'},
  {value: '3', label: '3天'},
  {value: '7', label: '7天'},
  {value: '30', label: '30天'}
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
    if (response.status !== 200) {
      throw new Error(`HTTP错误 ${response.status}`);
    }
    if (response.data?.code !== 200) {
      throw new Error(`业务错误: ${response.data?.message || '未知错误'}`);
    }
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

const InviteRecordDialogVisible = ref(false);
const OpenInviteRecordComponent = () => {
  InviteRecordDialogVisible.value = true;
};
const CloseInviteRecordComponent = () => {
  InviteRecordDialogVisible.value = false;
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
  height: 80vh;
  text-align: center;
}

.infinite-list-wrapper .list {
  padding: 0;
  margin: 0;
  list-style: none;
}

.infinite-list-wrapper .list-item {
  border-radius: 10px;
  padding: 10px;
  display: flex;
  align-items: center;
  //justify-content: center; height: 50px;
  background: #f5f5f5;
  color: #000000;
}

.infinite-list-wrapper .list-item + .list-item {
  margin-top: 10px;
}
</style>
