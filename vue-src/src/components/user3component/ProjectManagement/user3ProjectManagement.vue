<script setup>
import {ref} from "vue";
import {ElButton, ElInput, ElMessage, ElMessageBox} from "element-plus";
import store from "../../../store";
import {Search} from "@element-plus/icons-vue";
import CreateProject
  from "../../user3component/ProjectManagement/CreateProject.vue";

const showCreateComponent = ref(false);
// 控制是否显示 Create 组件
const toggleCreateComponent = () => {
  store.dispatch('lockNavbar', '创建项目模板');
  showCreateComponent.value = true;
};
const input = ref('');
const filteredData = ref([]);  // 存储过滤后的数据
const searchTemplates = () => {
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

</script>

<template>
  <div v-if="!showCreateComponent">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item><strong>项目管理</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <br />
    <el-button type="primary" @click="toggleCreateComponent">创建新项目</el-button>&nbsp;
  <el-input v-model="input" style="width: auto" placeholder="请输入" @input="searchTemplates" />
  <el-button type="primary" @click="searchTemplates">
    <el-icon><Search /></el-icon>
  </el-button>
  <br /><br />

  </div>
  <div v-if="showCreateComponent">
    <!-- 创建项目模板页面 -->
    <el-breadcrumb separator="/">
      <el-breadcrumb-item @click="closeCreateComponent"><a>项目管理</a></el-breadcrumb-item>
      <el-breadcrumb-item><strong>新建项目</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <br /><el-page-header @back="closeCreateComponent" title="放弃">
    <template #content>
      <span class="text-large font-600 mr-3"> 创建项目 </span>
    </template>
  </el-page-header>
    <!-- 创建模板组件 -->
    <CreateProject />
  </div>
</template>

<style scoped>

</style>
