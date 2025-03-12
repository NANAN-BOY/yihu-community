<template>
  <div class="container">
    <div class="card">
      <h1 class="title">邀请信息</h1>
      <div v-if="inviteInfo" class="invite-info">
        <p v-if="!expired">
          邀请人: <strong>{{ inviteInfo.invite_user_name }}</strong>
        </p>
        <p v-if="remainingTime">
          剩余时间: <strong>{{ remainingTime }}</strong>
        </p>
        <el-button v-if="!expired" type="danger" @click="refuseInvitation">拒绝</el-button>
        <el-button v-if="!expired" type="success" @click="acceptInvitation"
          >接受邀请</el-button
        >
        <p v-if="expired" class="expired">该邀请已过期</p>
      </div>
      <p v-if="loading" class="loading">正在加载邀请信息...</p>
      <p v-if="!inviteInfo && !loading" class="no-invite">
        没有邀请信息，请与管理员联系。
      </p>

      <!-- 查询失败时的提示 -->
      <p v-if="queryFailed && !loading" class="no-invite">
        邀请信息查询失败，请稍后重试。
      </p>
    </div>
  </div>
</template>

<script setup>
import {ref, watch, onMounted, h} from 'vue';
import { useRoute } from 'vue-router';
import {ElMessage, ElMessageBox} from 'element-plus';
import router from "../router";
import store from "../store";
import axios from "axios";

const inviteId = ref(null);
const inviteInfo = ref(null);
const remainingTime = ref(null);
const expired = ref(false);
const loading = ref(false);
const queryFailed = ref(false);

const route = useRoute();
inviteId.value = route.params.inviteId;

watch(route, (to) => {
  inviteId.value = to.params.inviteId;
  fetchInviteInfo();
});

const fetchInviteInfo = async () => {
  loading.value = true;
  queryFailed.value = false;
  try {
    const response = await axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/expert/get-record`, {
      params: {
        id: inviteId.value
      }
    });
    console.log(response);
    if (response.data.code == 200 && response.data.data.id) {
      inviteInfo.value = response;// 保存邀请信息
      const diffTime = (new Date(response.data.data.deadline)) - (new Date());
      if ((diffTime > 0) && (response.data.data.isAgree == null)) {
        remainingTime.value = `${Math.floor(diffTime / (1000 * 60 * 60))}小时
                               ${Math.floor((diffTime % (1000 * 60 * 60)) / (1000 * 60))}分钟`;
        expired.value = false;
      } else {
        expired.value = true;
        remainingTime.value = null;
      }
    } else {
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
const refuseInvitation = async () => {
  ElMessageBox({
    title: '提示',
    message: h('div', [
      h('p', '您为什么拒绝？'),
      h('el-input', {
        modelValue: '',
        'onUpdate:modelValue': (val) => { /* 保留输入框逻辑 */ }
      })
    ]),
    showCancelButton: true,
    confirmButtonText: '提交',
    cancelButtonText: '取消',
    showInput: true,
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        if (!/^.{1,30}$/.test(instance.inputValue)) {
          ElMessage.error('请输入1-30个字符')
          return false
        }
        instance.confirmButtonLoading = true
        instance.confirmButtonText = '提交中...'
        axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/expert/refuse-invite`, {
          id: inviteId.value,
          reason: instance.inputValue
        }).then(response => {
          if (response.data.code === 200) {
            done()
            router.push('/')
          } else {
            // 请求成功但业务逻辑失败
            ElMessage.error(response.data.msg || '提交失败')
            instance.confirmButtonLoading = false
            instance.confirmButtonText = '提交'
          }
        }).catch(error => {
          console.error('请求失败:', error)
          ElMessage.error(`请求失败: ${error.response?.data?.msg || error.message}`)
          instance.confirmButtonLoading = false // 保持对话框开启
          instance.confirmButtonText = '提交'
        }).finally(() => {
          // 移除done调用，只在成功时关闭对话框
          setTimeout(() => {
            instance.confirmButtonLoading = false
            instance.confirmButtonText = '提交'
          }, 300)
        })
      } else {
        done()
      }
    }
  }).then(() => {
    ElMessage.success('操作已提交')
  }).catch(() => {
    ElMessage.info('操作已取消')
  })
}
const acceptInvitation = async () => {
  ElMessageBox.confirm(
      '请选择您的加入方式',
      '提示',
      {
        confirmButtonText: '注册新账号',
        cancelButtonText: '关联现有账号',
        type: 'info',
        center: true,
      }
  )
      .then(() => {
        ElMessage({
          type: 'warning',
          message: '请注册后继续操作',
        })
        store.dispatch('setExpertInviteId', inviteId);
        router.push('/register');

      })
      .catch(() => {
        if (!store.state.user.id) {
          ElMessage({
            type: 'warning',
            message: '请登录后继续操作',
          })
        }
        store.dispatch('setExpertInviteId', inviteId);
        router.push('/dashboard');
      })
}
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
