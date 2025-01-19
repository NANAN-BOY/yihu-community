<template>
  <div class="container">
    <div class="card">
      <h1 class="title">专家邀请注册</h1>

      <!-- 显示邀请信息 -->
      <div v-if="inviteInfo" class="invite-info">
        <p>邀请人: <strong>{{ inviteInfo.invite_user_name }}</strong></p>
        <p v-if="remainingTime">剩余时间: <strong>{{ remainingTime }}</strong></p>
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
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      inviteId: this.$route.params.inviteId, // 获取邀请ID
      inviteInfo: null, // 存储邀请信息
      remainingTime: null, // 存储剩余时间
      expired: false, // 存储是否已过期的状态
      loading: false, // 用于显示加载状态
      queryFailed: false, // 用于显示查询失败的状态
    };
  },
  watch: {
    '$route'(to, from) {
      // 路由变化时重新获取邀请信息
      this.inviteId = to.params.inviteId;
      this.fetchInviteInfo();
    },
  },
  mounted() {
    // 组件挂载时获取邀请信息
    this.fetchInviteInfo();
  },
  methods: {
    async fetchInviteInfo() {
      this.loading = true;  // 开始加载状态
      this.queryFailed = false;  // 重置查询失败状态
      try {
        // 调用后端接口获取邀请信息
        const response = await fetch(`${import.meta.env.VITE_BACKEND_IP}/api/ExpertLibrary/invite/${this.inviteId}`);
        const data = await response.json();

        if (response.ok && data.invite_user_name) {
          this.inviteInfo = data; // 保存邀请信息

          // 计算剩余时间
          const now = new Date();
          const inviteDeadline = new Date(data.invite_deadline);
          const diffTime = inviteDeadline - now; // 毫秒差

          if (diffTime > 0) {
            // 如果邀请未过期，计算剩余时间
            const hours = Math.floor(diffTime / (1000 * 60 * 60));
            const minutes = Math.floor((diffTime % (1000 * 60 * 60)) / (1000 * 60));
            this.remainingTime = `${hours}小时 ${minutes}分钟`;
            this.expired = false;
          } else {
            // 如果邀请已过期
            this.expired = true;
            this.remainingTime = null;
          }
        } else {
          // 如果没有邀请信息或请求失败
          this.queryFailed = true;
        }
      } catch (error) {
        console.error('API 请求失败:', error);
        this.queryFailed = true;
      } finally {
        this.loading = false;  // 结束加载状态
      }
    }
  },
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

/* 邀请ID样式 */
.invite-id {
  font-size: 16px;
  color: #666;
  margin-bottom: 10px;
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
  .invite-id,
  .invite-info,
  .loading,
  .no-invite {
    font-size: 14px;
  }
}
</style>
