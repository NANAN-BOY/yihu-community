<template>
  <!-- 顶部导航栏 -->
  <el-header style="background-color: #3ec474; color: white; padding: 0; display: flex; align-items: center;">
    <div class="header-content" style="width: 100%; padding: 0 20px; display: flex; justify-content: space-between; align-items: center;">

      <!-- 手机端三道杠按钮 -->
      <el-button v-if="isMobile" style="position: fixed; top: 15px; left: 15px; z-index: 10; border: none;" @click="drawerVisible = true"><el-icon><Expand /></el-icon></el-button>

      <!-- 左侧菜单 -->
      <el-col :span="isMobile ? 24 : 20" style="display: flex; align-items: center; justify-content: flex-start;">
        <span v-if="!isMobile" style="margin-left: 10px;">智能管理系统</span>
        <span v-if="isMobile" style="width: 100%; text-align: center;">智能管理系统</span>
      </el-col>

      <!-- 电脑端退出按钮 -->
      <el-col v-if="!isMobile" :span="4" style="display: flex; align-items: center; justify-content: flex-end;">
        <el-button type="primary" @click="handleLogout" color="#d7fbe8" >安全登出</el-button>
      </el-col>
    </div>
  </el-header>
  <el-container style="height: auto;">
    <!-- 左侧侧边栏 -->
    <el-aside :width="sidebarWidth" style="background-color: #ffffff;">
      <el-menu :default-active="store.state.user.user_role + '-1'" class="el-menu-vertical-demo" @select="handleSelect">
        <!-- 角色2相关菜单项 -->
        <el-menu-item v-if="store.state.user.user_role === 2" index="2-1">
          <el-icon><Document /></el-icon>
          <span>项目管理</span>
        </el-menu-item>
        <el-menu-item v-if="store.state.user.user_role=== 2" index="2-2">
          <el-icon><Avatar /></el-icon>
          <span>专家库管理</span>
        </el-menu-item>
        <el-sub-menu v-if="store.state.user.user_role === 2" index="2-3">
          <template #title><el-icon><Tickets /></el-icon>模板管理</template>
          <el-menu-item index="2-3-1">项目申报模板</el-menu-item>
          <el-menu-item index="2-3-2">活动细节模板</el-menu-item>
        </el-sub-menu>
        <el-menu-item v-if="store.state.user.user_role === 2" index="2-4">
          <el-icon><UserFilled /></el-icon>
          <span>社会组织管理</span>
        </el-menu-item>
        <!-- 角色3相关菜单项 -->
        <el-menu-item v-if="store.state.user.user_role === 3" index="3-1">
          <el-icon><Document /></el-icon>
          <span>项目管理</span>
        </el-menu-item>
        <el-menu-item v-if="store.state.user.user_role === 3" index="3-2">
          <el-icon><Document /></el-icon>
          <span>活动管理</span>
        </el-menu-item>
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
    <!-- 角色2相关菜单项 -->
    <el-menu :default-active="store.state.user.user_role + '-1'" class="el-menu-vertical-demo" @select="handleSelect">
      <el-menu-item v-if="store.state.user.user_role === 2" index="2-1">
        <el-icon><Document /></el-icon><span>项目管理</span></el-menu-item>
      <el-menu-item v-if="store.state.user.user_role === 2" index="2-2"><el-icon><Avatar /></el-icon><span>专家库管理</span></el-menu-item>
      <el-sub-menu v-if="store.state.user.user_role === 2" index="2-3"><template #title><el-icon><Tickets /></el-icon>模板管理</template>
        <el-menu-item index="2-3-1">项目申报模板</el-menu-item>
        <el-menu-item index="2-3-2">活动细节模板</el-menu-item>
      </el-sub-menu>
      <el-menu-item v-if="store.state.user.user_role === 2" index="2-4"><el-icon><UserFilled /></el-icon><span>社会组织管理</span></el-menu-item>
      <!-- 角色3相关菜单项 -->
      <el-menu-item v-if="store.state.user.user_role === 3" index="3-1"><el-icon><Document /></el-icon><span>项目管理</span></el-menu-item>
      <el-menu-item v-if="store.state.user.user_role === 3" index="3-2"><el-icon><Document /></el-icon><span>活动管理</span></el-menu-item>
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
import ProjectTemplateManagement from '../components/user2component/TemplateManagement/ProjectTemplateManagement/ProjectTemplateManagement.vue'; // 导入项目申报模板模板管理组件
import ActivityTemplateManagement from '../components/user2component/TemplateManagement/ActivityTemplateManagement.vue'; // 导入活动细节模板模板管理组件
import SocialOrganizationManagement from '../components/user2component/SocialOrganizationManagement.vue';
import user3PrijectManagement from '../components/user3component/ProjectManagement/user3ProjectManagement.vue'

import {Avatar, Document, Expand, List, Tickets, UserFilled} from "@element-plus/icons-vue"; // 导入社会组织管理组件
import {Location, Menu as IconMenu, Setting} from '@element-plus/icons-vue';
import {ElNotification} from "element-plus";

const router = useRouter();
const drawerVisible = ref(false); // 控制手机端侧边栏的显示
const sidebarWidth = ref('250px'); // 默认宽度
const isMobile = ref(false); // 是否是移动设备
const currentComponent = ref(null); // 当前显示的组件

// 处理菜单项选择
const handleSelect = (index) => {
  const navbarState = store.getters.isNavbarLocked;
  const lockReason = store.getters.navbarLockReason;
  // 检查导航栏是否锁定
  if (navbarState) {
    ElNotification({
      title: '警告',
      message: `您正在进行“${lockReason || '重要'}”操作，切换页面可能会导致操作丢失，请先保存或放弃。`,
      type: 'warning',
      duration: 3000,
    });
    return; // 阻止导航切换
  }
  // 根据索引设置当前组件
  switch (index) {
    //角色2
    case '2-1':
      currentComponent.value = ProjectManagement;
      break;
    case '2-2':
      currentComponent.value = ExpertLibraryManagement;
      break;
    case '2-3-1':
      currentComponent.value = ProjectTemplateManagement;
      break;
    case '2-3-2':
      currentComponent.value = ActivityTemplateManagement;
      break;
    case '2-4':
      currentComponent.value = SocialOrganizationManagement;
      break;
    //角色3
    case '3-1':
      currentComponent.value = user3PrijectManagement;
      break;
    case '3-2':
      currentComponent.value = ProjectManagement;
      break;
    case '3-3':
      currentComponent.value = ActivityTemplateManagement;
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
