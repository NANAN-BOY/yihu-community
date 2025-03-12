<script setup>
import {useStore} from 'vuex'
import {computed} from 'vue'
import {ElMessage} from 'element-plus'

const store = useStore()
const user = computed(() => store.state.user)

// 修改后的日期格式化函数
const formatDate = (timestamp) => {
  if (timestamp) {
    try {
      const date = new Date(Number(timestamp))
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      }).replace(/\//g, '-')
    } catch {
      return '无效时间'
    }
  } else {
    return '暂无记录'
  }
}
</script>
<template>
  <div v-loading="!user.id" class="user-info-container">
    <!-- 头像和基础信息 -->
    <div class="header-section">
      <el-avatar :size="100" class="user-avatar">
        <i class="el-icon-user-solid"/>
      </el-avatar>
      <div class="base-info">
        <h2>{{ user.name || '未设置昵称' }}</h2>
        <p class="role-tag">{{ user.role }}</p>
      </div>
    </div>

    <!-- 详细信息展示 -->
    <el-form label-width="100px" class="detail-section">
      <!-- 基本信息 -->
      <el-form-item label="联系电话">
        <el-input :model-value="user.phone" readonly/>
      </el-form-item>

      <el-form-item label="个人简介">
        <el-input
            :model-value="user.description || '暂无简介'"
            type="textarea"
            :rows="3"
            readonly
        />
      </el-form-item>

      <!-- 账户信息 -->
      <el-divider content-position="left"><h3>账户信息</h3></el-divider>
      <el-form-item label="账户状态">
        <el-tag :type="user.status === 'active' ? 'success' : 'danger'">
          {{ user.status === 'active' ? '正常' : '已冻结' }}
        </el-tag>
      </el-form-item>

      <el-form-item label="账户余额">
        <el-input :value="`¥ ${user.balance}`" readonly/>
      </el-form-item>

      <el-divider content-position="left"><h3>时间信息</h3></el-divider>
      <el-form-item label="注册时间">
        <el-input :value="formatDate(user.createAt)" readonly/>
      </el-form-item>

      <el-form-item label="最后登录">
        <el-input :value="formatDate(user.lastLoginTime)" readonly/>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.user-info-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.header-section {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.user-avatar {
  margin-right: 30px;
  background: #409eff;
}

.base-info h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
}

.role-tag {
  color: #666;
  font-size: 14px;
}

.detail-section {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.el-divider h3 {
  margin: 0;
  color: #333;
  font-size: 16px;
}
</style>
