<script setup>
import { ref, onMounted } from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import store from '../../../../store';
import CreateProjectTemplate from "./CreateProjectTemplate.vue"; // 引入 Vuex Store

// 控制是否显示 Create 组件
const showCreateComponent = ref(false); // 默认不显示 Create 组件

// 控制输入框的数据
const input = ref('');
// 模板数据
const tableData = ref([]);
const filteredData = ref([]);  // 用来存储过滤后的数据

// 手动格式化时间
const formatDate = (dateString) => {
  const match = dateString.match(/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})/);
  if (match) {
    const [ , year, month, day, hours, minutes, seconds ] = match;
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  }
  return '无效的时间';
};

// 请求模板列表
const getTemplateList = async () => {
  try {
    const response = await fetch('http://localhost:3001/api/template/getProjectTemplateList'); // 后端接口请求
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    const data = await response.json();
    if (data.templates) {
      tableData.value = data.templates.map(template => ({
        ...template,
        formattedCreateAt: formatDate(template.template_create_at)
      })); // 将返回的数据存入 tableData，并格式化日期
      filteredData.value = [...tableData.value]; // 初始时显示全部模板
    } else {
      ElMessage.error('没有找到模板数据');
    }
  } catch (error) {
    ElMessage.error('获取模板列表失败');
    console.error('Error fetching templates:', error);
  }
};

// 搜索模板
const searchTemplates = () => {
  if (input.value) {
    filteredData.value = tableData.value.filter(template => {
      return template.template_name.toLowerCase().includes(input.value.toLowerCase()) ||
          template.template_description.toLowerCase().includes(input.value.toLowerCase());
    });
  } else {
    filteredData.value = [...tableData.value]; // 输入为空时恢复显示所有模板
  }
};

// 切换显示 Create 组件的函数
const toggleCreateComponent = () => {
  store.dispatch('lockNavbar', '创建项目模板');
  showCreateComponent.value = true;
};

// 询问是否放弃创建的函数
const closeCreateComponent = () => {
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

// 在组件挂载时获取模板数据
onMounted(() => {
  getTemplateList();
});
</script>

<template>
  <!-- 项目模板管理-->
  <div v-if="!showCreateComponent">
    <!-- 面包屑导航 -->
    <el-breadcrumb separator="/">
      <el-breadcrumb-item><strong>项目申报模板管理</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <br />
    <el-button type="primary" @click="toggleCreateComponent">创建新模板</el-button>&nbsp;
    <!-- 输入框和搜索按钮 -->
    <el-input v-model="input" style="width: auto" placeholder="请输入" @input="searchTemplates" />
    <el-button type="primary" @click="searchTemplates">
      <el-icon><Search /></el-icon>
    </el-button>
  </div>

  <!-- 创建项目模板页面 -->
  <div v-if="showCreateComponent">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item @click="closeCreateComponent"><a>项目申报模板管理</a></el-breadcrumb-item>
      <el-breadcrumb-item><strong>创建项目模板</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <br /><el-page-header @back="closeCreateComponent" title="放弃">
    <template #content>
      <span class="text-large font-600 mr-3"> 创建项目模板向导 </span>
    </template><br />
  </el-page-header>
    <!-- 创建模板组件 -->
    <CreateProjectTemplate />
  </div>

  <!-- 模板列表 -->
  <div v-else>
    <!-- 当前页面内容：表格 -->
    <el-table :data="filteredData" style="width: 100%; max-height: calc(100vh - 200px); overflow-y: auto;">
      <el-table-column prop="template_name" label="模板名称"></el-table-column>
      <el-table-column prop="template_description" label="描述"></el-table-column>
      <el-table-column prop="formattedCreateAt" label="创建时间"></el-table-column>
    </el-table>
  </div>
</template>


<style scoped>
/* 控制表格滚动 */
.el-table {
  max-height: calc(100vh - 200px); /* 使表格高度自适应屏幕，减去其他布局占用的空间 */
  overflow-y: auto; /* 启用垂直滚动 */
}
</style>
