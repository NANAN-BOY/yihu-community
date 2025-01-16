<template>
  <!-- 横条导航 -->
  <div class="flex flex-col items-start gap-4">
    <el-segmented v-model="activeTab" :options="tabOptions" size="large" />
  </div>

  <!-- 显示选中的模板管理组件 -->
  <div v-if="activeTab === '项目申报模板'">
    <ProjectTemplateManagement />
  </div>
  <div v-if="activeTab === '活动细节模板'">
    <ActivityTemplateManagement />
  </div>
</template>

<script setup>
import {ref} from 'vue';

// 导入两个组件
import ProjectTemplateManagement from './ProjectTemplateManagement/ProjectTemplateManagement.vue';
import ActivityTemplateManagement from './ActivityTemplateManagement.vue';

const activeTab = ref('项目申报模板'); // 默认激活"项目申报模板" tab

// 定义tab选项（主选项）
const tabOptions = [
  {label: '项目申报模板', value: '项目申报模板'},
  {label: '活动细节模板', value: '活动细节模板'},
];
</script>

<style scoped>
/* 样式调整 */
.el-segmented {
  width: 100%; /* 让选项占满宽度 */
}

.el-segmented__button {
  font-size: 16px;
  padding: 10px;
  border-radius: 4px;
}
</style>
