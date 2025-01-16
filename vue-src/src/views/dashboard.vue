<template>
  <!-- 顶部导航栏 -->
  <el-header style="background-color: #409EFF; color: white; padding: 0; display: flex; align-items: center;">
    <div class="header-content" style="width: 100%; padding: 0 20px; display: flex; justify-content: space-between; align-items: center;">
      <!-- 手机端三道杠按钮 -->
      <el-button
          v-if="isMobile"
          style="position: fixed; top: 15px; left: 15px; z-index: 10; border: none;"
          @click="drawerVisible = true"
      >
        <el-icon><Expand /></el-icon>
      </el-button>

      <!-- 左侧菜单 -->
      <el-col :span="isMobile ? 24 : 20" style="display: flex; align-items: center; justify-content: flex-start;">
        <!-- 在手机端时，居中显示“智能管理系统” -->
        <span v-if="!isMobile" style="margin-left: 10px;">智能管理系统</span>
        <span v-if="isMobile" style="width: 100%; text-align: center;">智能管理系统</span>
      </el-col>

      <!-- 电脑端退出按钮 -->
      <el-col v-if="!isMobile" :span="4" style="display: flex; align-items: center; justify-content: flex-end;">
        <el-button type="primary" @click="handleLogout">安全登出</el-button>
      </el-col>
    </div>
  </el-header>

  <el-container style="height: 100vh;">
    <!-- 左侧侧边栏 -->
    <el-aside :width="sidebarWidth" style="background-color: #f4f4f4;">
      <el-menu default-active="1" class="el-menu-vertical-demo" @select="handleSelect">
        <el-menu-item v-if="currentUserRole === 2" index="1">项目管理</el-menu-item> <!-- 只有角色2有权限 -->
        <el-menu-item v-if="currentUserRole === 2" index="2">专家库管理</el-menu-item>
        <el-menu-item v-if="currentUserRole === 2" index="3">模板管理</el-menu-item>
        <el-menu-item v-if="currentUserRole === 2" index="4">社会组织管理</el-menu-item> <!-- 角色3可以访问此项 -->

        <!-- 手机端下，登出按钮也显示在侧边栏 -->
        <el-menu-item v-if="isMobile" index="5" @click="handleLogout">
          <el-button type="primary" style="width: 100%;">安全登出</el-button>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧内容区 -->
    <el-container>
      <!-- 页面主内容 -->
      <el-main style="padding: 20px; background-color: #ffffff;">
        <component :is="currentComponent" />
      </el-main>
    </el-container>
  </el-container>

  <!-- 手机端侧边栏 -->
  <el-drawer v-model="drawerVisible" :size="'250px'" direction="ltr" :before-close="handleDrawerClose">
    <el-menu default-active="1" class="el-menu-vertical-demo" @select="handleSelect">
      <!-- 判断当前角色是否为2 -->
      <el-menu-item v-if="currentUserRole === 2" index="1">项目管理</el-menu-item>
      <el-menu-item v-if="currentUserRole === 2" index="2">专家库管理</el-menu-item>
      <el-menu-item v-if="currentUserRole === 2" index="3">模板管理</el-menu-item>
      <el-menu-item v-if="currentUserRole === 2" index="4">社会组织管理</el-menu-item>

      <!-- 手机端下，登出按钮 -->
      <el-menu-item index="5" @click="handleLogout">
        <el-button type="primary" style="width: 100%;">安全登出</el-button>
      </el-menu-item>
    </el-menu>
  </el-drawer>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import store from '../store'; // 假设你已经配置了 Vuex 存储
import ProjectManagement from '../components/user2component/ProjectManagement.vue'; // 导入项目管理组件
import ExpertLibraryManagement from '../components/user2component/ExpertLibraryManagement.vue'; // 导入专家库管理组件
import TemplateManagement from '../components/user2component/TemplateManagement.vue'; // 导入模板管理组件
import SocialOrganizationManagement from '../components/user2component/SocialOrganizationManagement.vue';
import {Expand} from "@element-plus/icons-vue"; // 导入社会组织管理组件

const router = useRouter();
const drawerVisible = ref(false); // 控制手机端侧边栏的显示
const sidebarWidth = ref('250px'); // 默认宽度
const isMobile = ref(false); // 是否是移动设备
const currentComponent = ref(null); // 当前显示的组件

const currentUserRole = ref(2); // 当前用户角色（例如：角色 2）

// 处理菜单项选择
const handleSelect = (index) => {
  console.log('选择的菜单项:', index);
  switch (index) {
    case '1':
      currentComponent.value = ProjectManagement;
      break;
    case '2':
      currentComponent.value = ExpertLibraryManagement;
      break;
    case '3':
      currentComponent.value = TemplateManagement;
      break;
    case '4':
      currentComponent.value = SocialOrganizationManagement;
      break;
    default:
      currentComponent.value = null;
  }
  // 手机端选择后关闭侧边栏
  if (isMobile.value) {
    drawerVisible.value = false;
  }
};


const handleLogout = () => {
  console.log('正在退出登录...');
  store.dispatch('logout'); // 调用 Vuex 中的 logout 动作，清除用户信息和 token
  router.push('/login');
};

const handleDrawerClose = () => {
  drawerVisible.value = false;
};

onMounted(() => {
  const handleResize = () => {
    if (window.innerWidth <= 768) {
      isMobile.value = true;
      sidebarWidth.value = '0px'; // 小屏幕下隐藏侧边栏
    } else {
      isMobile.value = false;
      sidebarWidth.value = '250px'; // 大屏幕下显示侧边栏
    }
  };

  window.addEventListener('resize', handleResize);
  handleResize(); // 初始化时判断屏幕大小
});
</script>

<style scoped>
/* 设置侧边栏、顶部导航栏和主内容区的样式 */

.el-menu-vertical-demo {
  border-right: none;
  padding-top: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-col {
  display: flex;
  justify-content: flex-start; /* 保证左侧内容在左边 */
  align-items: center; /* 使左侧内容竖直居中 */
}

@media (max-width: 768px) {
  .header-content {
    display: flex;
    justify-content: center; /* 居中显示顶部文字 */
  }
  .el-col {
    justify-content: center;
  }
}
</style>
