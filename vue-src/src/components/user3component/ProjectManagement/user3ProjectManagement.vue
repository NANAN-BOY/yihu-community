<script setup>
import { ref, onMounted } from "vue";
import { ElButton, ElInput, ElMessage, ElMessageBox } from "element-plus";
import { Search } from "@element-plus/icons-vue";
import store from "../../../store";
import axios from "axios";
import CreateProject from "./CreateProject.vue";

const showCreateComponent = ref(false); // 控制创建组件的显示
const input = ref(''); // 搜索框的输入
const filteredData = ref([]); // 存储过滤后的项目数据
const allProjects = ref([]); // 存储所有项目数据
const loading = ref(false); // 控制加载状态

// 控制是否显示创建项目页面
const toggleCreateComponent = () => {
  store.dispatch('lockNavbar', '创建项目模板');
  showCreateComponent.value = true;
};

// 获取用户的所有项目
const getMyProjects = async () => {
  const userId = store.state.user.user_id;  // 从 Vuex 获取当前用户的 ID
  if (!userId) {
    ElMessage.error('用户未授权');
    return;
  }
  loading.value = true;  // 显示加载动画
  try {
    const response = await axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/project/getMyProjects`, {
      params: { userId }  // 将 userId 作为查询参数传递
    });
    if (response.data.success) {
      allProjects.value = response.data.projects;
      filteredData.value = allProjects.value; // 初始化项目列表
    } else {
      ElMessage.error('获取项目列表失败');
    }
  } catch (error) {
    ElMessage.error('获取项目列表失败');
    console.error(error);
  } finally {
    loading.value = false;  // 隐藏加载动画
  }
};

// 搜索项目
const searchProjects = () => {
  if (input.value) {
    filteredData.value = allProjects.value.filter(project =>
        project.project_name.toLowerCase().includes(input.value.toLowerCase()) ||
        project.projectDeclare_user.toLowerCase().includes(input.value.toLowerCase())
    );
  } else {
    filteredData.value = [...allProjects.value];  // 输入为空时恢复所有项目
  }
};

// 取消创建项目操作
const GiveUpCreateComponent = () => {
  ElMessageBox.confirm(
      '确定放弃当前的创建操作吗？',
      '确定返回吗？',
      {
        confirmButtonText: '放弃',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(() => {
        store.dispatch('unlockNavbar');
        showCreateComponent.value = false;
        ElMessage.warning('已放弃创建');
      })
      .catch(() => {
        ElMessage.info('操作已取消');
      });
};

// 关闭创建项目页面
const CloseCreateComponent = () => {
  store.dispatch('unlockNavbar');
  showCreateComponent.value = false;
  getMyProjects();  // 关闭后刷新项目列表
};
// 在组件挂载时获取我的项目
onMounted(() => {
  getMyProjects();
});
</script>

<template>
  <div v-if="!showCreateComponent">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item><strong>项目管理</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <br />
    <el-button type="primary" @click="toggleCreateComponent">创建新项目</el-button>&nbsp;
    <el-input v-model="input" style="width: auto" placeholder="请输入" @input="searchProjects" />
    <el-button type="primary" @click="searchProjects">
      <el-icon><Search /></el-icon>
    </el-button>
    <br /><br />

    <!-- 项目列表显示方式 -->
    <div v-loading="loading" class="project-list">
      <div v-if="filteredData.length > 0">
        <div v-for="(project, index) in filteredData" :key="index" class="project-card">
          <div class="project-header">
            <span class="project-title">{{ project.project_name }}</span>
          </div>
          <div class="project-description">
            <strong>提交时间：</strong> {{ project.projectDeclare_create_at }}
          </div>
          <div class="project-status">
            <strong>优化状态: </strong>
            <span :style="{ color: project.isOptimized ? 'green' : 'red' }">
              {{ project.isOptimized ? '已优化' : '未优化' }}
            </span>
          </div>
        </div>
      </div>
      <el-empty
          v-else
          description="暂无项目数据，点击上方按钮创建新项目"
          :image-size="200"
      />
    </div>
  </div>

  <div v-if="showCreateComponent">
    <!-- 创建项目模板页面 -->
    <el-breadcrumb separator="/">
      <el-breadcrumb-item @click="GiveUpCreateComponent"><a>项目管理</a></el-breadcrumb-item>
      <el-breadcrumb-item><strong>新建项目</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <br />
    <el-page-header @back="GiveUpCreateComponent" title="放弃">
      <template #content>
        <span class="text-large font-600 mr-3"> 创建项目 </span>
      </template>
    </el-page-header>
    <!-- 创建模板组件 -->
    <CreateProject @closeForm="CloseCreateComponent"/>
  </div>
</template>

<style scoped>
/* 项目列表样式 */
.project-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.project-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.project-header {
  font-size: 18px;
  font-weight: bold;
}

.project-description {
  font-size: 14px;
  color: #666;
  margin-top: 10px;
}

.project-status {
  font-size: 14px;
  margin-top: 10px;
}

.project-status span {
  font-weight: bold;
}

.project-list {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 10px;
}
</style>
