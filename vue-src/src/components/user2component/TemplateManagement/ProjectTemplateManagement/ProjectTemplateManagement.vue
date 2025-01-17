<script setup>
import { ref, onMounted } from 'vue';
import {
  ElMessage,
  ElButton,
  ElTable,
  ElTableColumn,
  ElDropdown,
  ElDropdownMenu,
  ElDropdownItem,
  ElInput,
  ElMessageBox
} from 'element-plus';
import {ArrowDown, Search} from '@element-plus/icons-vue';
import store from '../../../../store';
import CreateProjectTemplate from "./CreateProjectTemplate.vue";

// 控制是否显示 Create 组件
const showCreateComponent = ref(false);
const input = ref('');
// 模板数据
const tableData = ref([]);
const filteredData = ref([]);  // 存储过滤后的数据

// 格式化时间
const formatDate = (dateString) => {
  const match = dateString.match(/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})/);
  if (match) {
    const [ , year, month, day, hours, minutes, seconds ] = match;
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  }
  return '无效时间';
};

// 请求模板列表
const getTemplateList = async () => {
  try {
    const response = await fetch(`${import.meta.env.VITE_BACKEND_IP}/api/template/getProjectTemplateList`);
    if (!response.ok) throw new Error('Network response was not ok');
    const data = await response.json();
    if (data.templates) {
      tableData.value = data.templates.map(template => ({
        ...template,
        formattedCreateAt: formatDate(template.template_create_at),
      }));
      filteredData.value = [...tableData.value];  // 初始化数据
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
    filteredData.value = tableData.value.filter(template =>
        template.template_name.toLowerCase().includes(input.value.toLowerCase()) ||
        template.template_description.toLowerCase().includes(input.value.toLowerCase())
    );
  } else {
    filteredData.value = [...tableData.value];  // 输入为空时恢复所有模板
  }
};

// 控制是否显示 Create 组件
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

// 模板启用/归档操作
const enableTemplate = (templateId) => {
  console.log(`启用模板：${templateId}`);
};

const archiveTemplate = (templateId) => {
  console.log(`归档模板：${templateId}`);
};

</script>
<template>
  <div v-if="!showCreateComponent">
    <!-- 面包屑导航 -->
    <el-breadcrumb separator="/">
      <el-breadcrumb-item><strong>项目申报模板管理</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <br />
    <el-button type="primary" @click="toggleCreateComponent">创建新模板</el-button>&nbsp;
    <el-input v-model="input" style="width: auto" placeholder="请输入" @input="searchTemplates" />
    <el-button type="primary" @click="searchTemplates">
      <el-icon><Search /></el-icon>
    </el-button>
    <br /><br />

    <!-- 模板列表，添加了滚动样式 -->
    <div class="template-list">
      <div v-for="(template, index) in filteredData" :key="index" class="template-card">
        <div class="template-header">
          <span class="template-title">{{ template.template_name }}</span>
          <div class="template-actions">
            <el-dropdown>
              <span class="el-dropdown-link">
                <el-icon class="el-icon--right">
                  <arrow-down />
                </el-icon>
                ...
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="enableTemplate(template.template_id)">启用模板</el-dropdown-item>
                  <el-dropdown-item @click="archiveTemplate(template.template_id)">归档模板</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
        <div class="template-description">{{ template.template_description.length > 10 ? template.template_description.slice(0, 10) + '...' : template.template_description }}</div>
        <div class="template-created-at">{{ template.formattedCreateAt }}</div>
      </div>
    </div>
  </div>

  <div v-if="showCreateComponent">
    <!-- 创建项目模板页面 -->
    <el-breadcrumb separator="/">
      <el-breadcrumb-item @click="closeCreateComponent"><a>项目申报模板管理</a></el-breadcrumb-item>
      <el-breadcrumb-item><strong>创建项目模板</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <br /><el-page-header @back="closeCreateComponent" title="放弃">
    <template #content>
      <span class="text-large font-600 mr-3"> 创建项目模板向导 </span>
    </template>
  </el-page-header>
    <!-- 创建模板组件 -->
    <CreateProjectTemplate />
  </div>
</template>

<style scoped>
/* 面板样式 */
.template-card {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 16px;
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
}

.template-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.template-title {
  font-weight: bold;
}

.template-actions {
  margin-left: 10px;
}

.template-description {
  font-size: 14px;
  color: #666;
  margin-top: 10px;
}

.template-created-at {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

/* 设置列表容器的滚动 */
.template-list {
  max-height: 80vh;  /* 可以根据需要调整高度 */
  overflow-y: auto;   /* 启用垂直滚动 */
  padding-right: 10px; /* 防止右侧滚动条遮挡内容 */
}

/* 针对小屏幕优化 */
@media (max-width: 768px) {
  .template-card {
    padding: 12px;
  }

  .template-list {
    max-height: 80vh;  /* 小屏幕时限制滚动区域的高度 */
  }
}
</style>
