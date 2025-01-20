<template>
  <!-- 顶部导航栏 -->
  <el-header style="background-color: #3ec474; color: white; padding: 0; display: flex; align-items: center;">
    <div class="header-content" style="width: 100%; padding: 0 20px; display: flex; justify-content: space-between; align-items: center;">
      <!-- 手机端三道杠按钮 -->
      <el-button v-if="isMobile" style="position: fixed; top: 15px; left: 15px; z-index: 10; border: none;" @click="drawerVisible = true"><el-icon><Expand /></el-icon></el-button>
      <!-- 左侧菜单 -->
      <el-col :span="isMobile ? 24 : 20" style="display: flex; align-items: center; justify-content: flex-start;"><span v-if="!isMobile" style="margin-left: 10px;">智能管理系统</span><span v-if="isMobile" style="width: 100%; text-align: center;">智能管理系统</span></el-col>
      <!-- 电脑端退出按钮 -->
      <el-col v-if="!isMobile" :span="4" style="display: flex; align-items: center; justify-content: flex-end;">
        <el-button type="primary" @click="handleLogout" color="#d7fbe8" >安全登出</el-button>
      </el-col>
    </div>
  </el-header>
  <el-container style="height: auto;">
    <!-- ComputerSidebar -->
    <el-aside :width="sidebarWidth" style="background-color: #ffffff;">
      <el-menu class="el-menu-vertical-demo" @select="handleSelect">
        <!-- R2 -->
        <el-menu-item v-if="store.state.user.user_role === 2" index="2-1"><el-icon><Document /></el-icon><span>项目管理</span></el-menu-item>
        <el-menu-item v-if="store.state.user.user_role=== 2" index="2-2"><el-icon><Avatar /></el-icon><span>专家库管理</span></el-menu-item>
        <el-sub-menu v-if="store.state.user.user_role === 2" index="2-3"><template #title><el-icon><Tickets /></el-icon>模板管理</template><el-menu-item index="2-3-1">项目申报模板</el-menu-item><el-menu-item index="2-3-2">活动细节模板</el-menu-item></el-sub-menu>
        <el-menu-item v-if="store.state.user.user_role === 2" index="2-4"><el-icon><UserFilled /></el-icon><span>社会组织管理</span></el-menu-item>
        <!-- R3 -->
        <el-menu-item v-if="store.state.user.user_role === 3" index="3-1"><el-icon><Document /></el-icon><span>项目管理</span></el-menu-item>
        <el-menu-item v-if="store.state.user.user_role === 3" index="3-2"><el-icon><Document /></el-icon><span>活动管理</span></el-menu-item>
        <!-- R4 -->
        <el-menu-item v-if="store.state.user.user_role === 4" index="4-1"><el-icon><Document /></el-icon><span>项目库</span></el-menu-item>
        <el-menu-item v-if="store.state.user.user_role === 4" index="4-2"><el-icon><Document /></el-icon><span>待优化项目</span></el-menu-item>
        <!-- MobilePhoneLogoutButton -->
        <el-menu-item v-if="isMobile" index="5" @click="handleLogout"><el-button type="primary" style="width: 100%;">安全登出</el-button></el-menu-item>
      </el-menu>
    </el-aside>
    <!-- MainContainer -->
    <el-container><el-main style="padding: 20px; background-color: #ffffff;"><component :is="currentComponent" /></el-main></el-container>
  </el-container>
  <!-- MobilePhoneSidebar -->
  <el-drawer v-model="drawerVisible" :size="'250px'" direction="ltr" :before-close="handleDrawerClose">
    <el-menu class="el-menu-vertical-demo" @select="handleSelect">
      <!-- R2 -->
      <el-menu-item v-if="store.state.user.user_role === 2" index="2-1"><el-icon><Document /></el-icon><span>项目管理</span></el-menu-item>
      <el-menu-item v-if="store.state.user.user_role === 2" index="2-2"><el-icon><Avatar /></el-icon><span>专家库管理</span></el-menu-item>
      <el-sub-menu v-if="store.state.user.user_role === 2" index="2-3"><template #title><el-icon><Tickets /></el-icon>模板管理</template><el-menu-item index="2-3-1">项目申报模板</el-menu-item><el-menu-item index="2-3-2">活动细节模板</el-menu-item></el-sub-menu>
      <el-menu-item v-if="store.state.user.user_role === 2" index="2-4"><el-icon><UserFilled /></el-icon><span>社会组织管理</span></el-menu-item>
      <!-- R3 -->
      <el-menu-item v-if="store.state.user.user_role === 3" index="3-1"><el-icon><Document /></el-icon><span>项目管理</span></el-menu-item>
      <el-menu-item v-if="store.state.user.user_role === 3" index="3-2"><el-icon><Document /></el-icon><span>活动管理</span></el-menu-item>
      <!-- R4 -->
      <el-menu-item v-if="store.state.user.user_role === 4" index="4-1"><el-icon><Document /></el-icon><span>项目库</span></el-menu-item>
      <el-menu-item v-if="store.state.user.user_role === 4" index="4-2"><el-icon><Document /></el-icon><span>待优化项目</span></el-menu-item>
      <!-- MobilePhoneLogoutButton -->
      <el-menu-item index="5" @click="handleLogout"><el-button type="primary" style="width: 100%;">安全登出</el-button></el-menu-item>
    </el-menu>
  </el-drawer>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import store from '../store'; 
import user2ProjectManagement from '../components/user2component/ProjectManagement/user2ProjectManagement.vue'; 
import user2ExpertLibraryManagement from '../components/user2component/ExpertLibraryManagement/ExpertLibraryManagement.vue'; // 导入专家库管理组件
import user2ProjectTemplateManagement from '../components/user2component/TemplateManagement/ProjectTemplateManagement/ProjectTemplateManagement.vue'; // 导入项目申报模板模板管理组件
import user2ActivityTemplateManagement from '../components/user2component/TemplateManagement/ActivityTemplateManagement.vue'; // 导入活动细节模板模板管理组件
import user2SocialOrganizationManagement from '../components/user2component/SocialOrganizationManagement/SocialOrganizationManagement.vue';
import user3ProjectManagement from '../components/user3component/ProjectManagement/user3ProjectManagement.vue'
import WaitingOptimizationProject from '../components/user4component/WaitingOptimizationProject.vue';

import {Avatar, Document, Expand, List, Tickets, UserFilled} from "@element-plus/icons-vue"; // 导入社会组织管理组件
import {Location, Menu as IconMenu, Setting} from '@element-plus/icons-vue';
import {ElNotification,ElMessage} from "element-plus";

const router = useRouter();
const drawerVisible = ref(false); // 控制手机端侧边栏的显示
const sidebarWidth = ref('250px'); // 默认宽度
const isMobile = ref(false); // 是否是移动设备
const currentComponent = ref(null); // 当前显示的组件

// 处理菜单项选择
const handleSelect = (index) => {
  const navbarState = store.getters.isNavbarLocked;
  const lockReason = store.getters.navbarLockReason;
  if (navbarState) {ElMessage.error('请先保存或放弃当前操作。');return;}
  switch (index) {
    //R2
    case '2-1':currentComponent.value = user2ProjectManagement;break;
    case '2-2':currentComponent.value = user2ExpertLibraryManagement;break;
    case '2-3-1':currentComponent.value = user2ProjectTemplateManagement;break;
    case '2-3-2':currentComponent.value = user2ActivityTemplateManagement;break;
    case '2-4':currentComponent.value = user2SocialOrganizationManagement;break;
    //R3
    case '3-1':currentComponent.value = user3ProjectManagement;break;
    case '3-2':currentComponent.value = user2ProjectManagement;break;
    case '3-3':currentComponent.value = ActivityTemplateManagement;break;
    //R4
    case '4-1':currentComponent.value = ProjectLibrary;break;
    case '4-2':currentComponent.value = WaitingOptimizationProject;break;
    default:currentComponent.value = null;
  }
  // PhoneCloseNavAfterClick
  if (isMobile.value) {drawerVisible.value = false;}
};

const handleLogout = () => {store.dispatch('logout');router.push('/login');};

const handleDrawerClose = () => {drawerVisible.value = false;};

onMounted(() => {
  const handleResize = () => {
    if (window.innerWidth <= 768) {isMobile.value = true;sidebarWidth.value = '0px';} else {isMobile.value = false;sidebarWidth.value = '250px';}
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
