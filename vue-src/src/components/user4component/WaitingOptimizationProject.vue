<script setup>

import {ElMessage} from "element-plus";
import {onMounted, ref} from "vue";
const projectData = ref([]);
const filteredProjects = ref([]);  // 存储过滤后的项目数据
const loading = ref(false);  // 控制加载状态

// 格式化时间
const formatDate = (dateString) => {
  const match = dateString.match(/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})/);
  if (match) {
    const [, year, month, day, hours, minutes, seconds] = match;
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  }
  return '无效时间';
};

const getProjectList = async () => {
  loading.value = true;  // 显示加载动画
  try {
    const response = await fetch(`${import.meta.env.VITE_BACKEND_IP}/api/project/getAllProjects`);
    if (!response.ok) throw new Error('Network response was not ok');
    const data = await response.json();
    console.log(data);

    if (data.success) {
      projectData.value = data.projects.map(project => ({
        ...project,
        project_name: project.project_name,  // 使用返回的项目名称
        projectDeclare_user: project.projectDeclare_user_name,  // 显示提交组织
        project_description: project.projectDeclare_create_at,  // 映射实际的字段描述
        formattedCreateAt: formatDate(project.projectDeclare_create_at),  // 格式化日期字段
        isOptimized: project.isOptimized  // 映射是否优化字段
      }));
      filteredProjects.value = [...projectData.value];  // 初始化数据
    } else {
      ElMessage.error('没有找到项目信息');
    }
  } catch (error) {
    ElMessage.error('获取项目列表失败');
    console.error('Error fetching projects:', error);
  } finally {
    loading.value = false;  // 数据加载完成后隐藏加载动画
  }
};
onMounted(() => {
  getProjectList();
});
</script>

<template>
<el-breadcrumb separator="/">
  <el-breadcrumb-item><strong>待优化项目</strong></el-breadcrumb-item>
</el-breadcrumb>
  <div v-loading="loading" class="project-list">
    <div v-for="(project, index) in filteredProjects" :key="index" class="project-card">
      <div class="project-header">
        <span class="project-title">{{ project.project_name }}</span>
      </div>
      <div class="project-description">
        <strong>提交组织：</strong> {{ project.projectDeclare_user }}  <!-- 显示提交组织 -->
      </div>
      <div class="project-description">
        <strong>提交时间：</strong> {{ project.formattedCreateAt }}  <!-- 显示提交时间 -->
      </div>
      <div class="project-status">
        <strong>优化状态: </strong>
        <span :style="{ color: project.isOptimized ? 'green' : 'red' }">
          {{ project.isOptimized ? '已优化' : '未优化' }}
        </span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.project-card {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 16px;
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
}
.project-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
.project-title {
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
.project-list {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 10px;
}
</style>