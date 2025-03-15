<script setup>
import {useStore} from 'vuex'
import {computed} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {ElIcon} from 'element-plus';
import {Edit} from "@element-plus/icons-vue";
import axios from "axios";

const store = useStore()
const user = computed(() => store.state.user)
const role = computed(() => {
  if (store.state.user.role === 1) {
    return '系统管理员'
  }
  if (store.state.user.role === 2) {
    return '政府'
  }
  if (store.state.user.role === 3) {
    return '社会组织'
  }
  if (store.state.user.role === 4) {
    return '专家'
  }
})
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

const editMyInfo = () => {
  ElMessageBox.prompt('请输入要修改的新昵称', '编辑名称', {
    confirmButtonText: '提交',
    cancelButtonText: '取消',
    inputPattern: /^[\w\u4e00-\u9fa5]{1,30}$/,
    inputErrorMessage: '请输入1 - 30个字符',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        const value = instance.inputValue;
        instance.confirmButtonLoading = true;
        instance.confirmButtonText = '加载中...';
        axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/user/update-info`, {
              id: store.state.user.id,
              name: value,
              description: store.state.user.description,
              location: store.state.user.location,
            }, {
              headers: {
                'token': `${store.state.token}`,
              },
            },
        )
            .then((response) => {
              console.log(response.data)
              if (response.data.code === 200) {
                store.state.user.name = value;
                ElMessage.success(response.data.data);
                // 成功后关闭窗口
                done();
                setTimeout(() => {
                  instance.confirmButtonLoading = false;
                  instance.confirmButtonText = '提交';
                }, 300);
              } else {
                ElMessage.error(response.data.data);
                // 失败则取消加载状态，不关闭窗口
                instance.confirmButtonLoading = false;
                instance.confirmButtonText = '提交';
              }
            })
            .catch((error) => {
              ElMessage.error('请求出错：' + error.message);
              // 出错则取消加载状态，不关闭窗口
              instance.confirmButtonLoading = false;
              instance.confirmButtonText = '提交';
            });
      } else {
        done();
      }
    },
  })
      .then((action) => {
        if (action === 'confirm') {
          // 这里可以根据需要添加额外逻辑
        }
      })
      .catch(() => {
        ElMessage.info('已取消修改');
      });
};

</script>
<template>
  <div v-loading="!user.id" class="user-info-container">
    <div class="header-section">
      <el-avatar :size="100" class="user-avatar">
        <i class="el-icon-user-solid"/>
      </el-avatar>
      <div class="base-info">
        <h2>
          {{ user.name || '未设置名称' }}
          <el-icon
              :size="21"
              color="blue"
              @click="editMyInfo"
              class="interactive-icon">
            <Edit/>
          </el-icon>
        </h2>

        <p class="role-tag">{{ role }}</p>
      </div>
    </div>
    <el-form label-width="100px" class="detail-section">
      <el-form-item label="关联手机号">
        <el-input :model-value="user.phone ? user.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : '未绑定手机'"
                  readonly/>
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
        <el-tag :type="user.status === 'active' ? 'danger' : 'success'">
          {{ user.status === 'active' ? '已封禁' : '正常' }}
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

.interactive-icon {
  background-color: transparent;
  transition: background-color 0.3s ease;
  border-radius: 50%;
}

.interactive-icon:hover, .interactive-icon:active {
  background-color: rgba(0, 0, 0, 0.3);
}
</style>
