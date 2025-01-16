<script setup>
import { ref } from 'vue';
import CreateProjectTemplate from './CreateProjectTemplate.vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import store from '../../../../store'; // 引入 Vuex Store

// 控制是否显示 Create 组件
const showCreateComponent = ref(false); // 默认不显示 Create 组件

// 切换显示 Create 组件的函数
const toggleCreateComponent = () => {
  // 锁定导航栏，设置锁定原因
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
        // 用户确认放弃后解锁导航栏并关闭组件
        store.dispatch('unlockNavbar');
        showCreateComponent.value = false;
        ElMessage.warning('已放弃创建');
      })
      .catch(() => {
        // 用户取消操作，不做任何改变
        ElMessage.info('操作已取消');
      });
};

// 控制输入框的数据
const input = ref('');
// 假设表格的数据
const tableData = ref([
  { date: '2025-01-01' },
  { date: '2025-01-02' },
  { date: '2025-01-03' },
]);
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
        <el-input v-model="input" style="width: auto" placeholder="请输入" />
        <el-button type="primary">
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

  <div v-else>
    <!-- 当前页面内容：表格 -->
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="date" label="日期" width="180"></el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
/* 可以根据需要添加样式 */
</style>
