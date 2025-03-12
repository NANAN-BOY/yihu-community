<template>
  <div>
    <!-- TopNavbar -->
    <el-header class="top-navbar">
      <div class="header-content">
        <!-- MobilePhoneMenuButton -->
        <el-button
            v-if="isMobile"
            class="mobile-menu-button"
            @click="drawerVisible = true"
        >
          <el-icon>
            <Expand/>
          </el-icon>
        </el-button>
        <!-- 左侧标题 -->
        <el-col :span="isMobile ? 24 : 20" class="left-menu">
          <span :class="isMobile ? 'menu-title-mobile' : 'menu-title'"
          ><strong>易互</strong></span
          >
        </el-col>
        <!-- 电脑端登出按钮 -->
        <el-col v-if="!isMobile" :span="4" class="right-menu">
          <el-button type="primary" @click="handleLogout" class="logout-button"
          >安全登出
          </el-button
          >
        </el-col>
      </div>
    </el-header>
    <el-container style="height: auto">
      <!-- 侧边栏 -->
      <el-aside :width="sidebarWidth" class="sidebar">
        <el-menu class="el-menu-vertical-demo" @select="handleSelect">
          <template v-for="menu in menus">
            <el-sub-menu
                v-if="menu.children && store.state.user.role === menu.role"
                :index="menu.index"
                :key="`sub-${menu.index}`"
            >
              <template #title>
                <el-icon>
                  <component :is="menu.icon"/>
                </el-icon>
                <span>{{ menu.title }}</span>
              </template>
              <el-menu-item
                  v-for="child in menu.children"
                  :key="child.index"
                  :index="child.index"
              >
                {{ child.title }}
              </el-menu-item>
            </el-sub-menu>

            <el-menu-item
                v-else-if="store.state.user.role === menu.role"
                :index="menu.index"
                :key="`item-${menu.index}`"
            >
              <el-icon>
                <component :is="menu.icon"/>
              </el-icon>
              <span>{{ menu.title }}</span>
            </el-menu-item>
          </template>
          <!-- 桌面端手机登出按钮 -->
          <el-menu-item v-if="isMobile" index="logout" @click="handleLogout">
            <el-button type="primary" class="mobile-logout-button"
            >安全登出
            </el-button
            >
          </el-menu-item>
        </el-menu>
      </el-aside>
      <!-- 主内容区 -->
      <el-container>
        <el-main class="main-content">
          <component :is="currentComponent"/>
        </el-main>
      </el-container>
    </el-container>
    <!-- 手机端侧边栏 -->
    <el-drawer
        v-model="drawerVisible"
        :size="'250px'"
        direction="ltr"
        :before-close="handleDrawerClose"
    >
      <el-menu class="el-menu-vertical-demo" @select="handleSelect">
        <template v-for="menu in menus">
          <el-sub-menu
              v-if="menu.children && store.state.user.role === menu.role"
              :index="menu.index"
              :key="`m-sub-${menu.index}`"
          >
            <template #title>
              <el-icon>
                <component :is="menu.icon"/>
              </el-icon>
              <span>{{ menu.title }}</span>
            </template>
            <el-menu-item
                v-for="child in menu.children"
                :key="child.index"
                :index="child.index"
            >
              {{ child.title }}
            </el-menu-item>
          </el-sub-menu>

          <el-menu-item
              v-else-if="store.state.user.role === menu.role"
              :index="menu.index"
              :key="`m-item-${menu.index}`"
          >
            <el-icon>
              <component :is="menu.icon"/>
            </el-icon>
            <span>{{ menu.title }}</span>
          </el-menu-item>
        </template>
        <!-- 移动端登出按钮 -->
        <el-menu-item v-if="isMobile" index="logout" @click="handleLogout">
          <el-button type="primary" class="mobile-logout-button">
            安全登出
          </el-button>
        </el-menu-item>
      </el-menu>
    </el-drawer>
  </div>
</template>

<script setup>
import {ref, onMounted, h} from 'vue';
import {useRouter} from 'vue-router';
import store from '../store';
import user2ProjectManagement from '../components/user2component/ProjectManagement/user2ProjectManagement.vue';
import user1ExpertLibraryManagement
  from '../components/user2component/ExpertLibraryManagement/ExpertLibraryManagement.vue';
import user2ProjectTemplateManagement
  from '../components/user2component/TemplateManagement/ProjectTemplateManagement/ProjectTemplateManagement.vue';
import user2ActivityTemplateManagement
  from '../components/user2component/TemplateManagement/ActivityTemplateManagement.vue';
import user2SocialOrganizationManagement
  from '../components/user2component/SocialOrganizationManagement/SocialOrganizationManagement.vue';
import user3ProjectManagement from '../components/user3component/ProjectManagement/user3ProjectManagement.vue';
import WaitingOptimizationProject from '../components/user4component/WaitingOptimizationProject.vue';

import {Avatar, Document, Expand, Tickets, UserFilled} from "@element-plus/icons-vue";
import {ElMessage, ElMessageBox} from "element-plus";
import axios from "axios";

const router = useRouter();
const drawerVisible = ref(false);
const sidebarWidth = ref('250px');
const isMobile = ref(false);
const currentComponent = ref(null);

// 菜单配置
const menus = [
  {role: 1, index: '1-1', icon: Document, title: '专家库管理'},
  {role: 1, index: '1-2', icon: Avatar, title: '系统管理'},
  {role: 2, index: '2-1', icon: Document, title: '项目管理'},
  {role: 2, index: '2-2', icon: Avatar, title: '专家库管理'},
  {
    role: 2, index: '2-3', icon: Tickets, title: '模板管理', children: [
      {index: '2-3-1', title: '项目申报模板'},
      {index: '2-3-2', title: '活动细节模板'}
    ]
  },
  {role: 2, index: '2-4', icon: UserFilled, title: '社会组织管理'},
  {role: 3, index: '3-1', icon: Document, title: '项目管理'},
  {role: 3, index: '3-2', icon: Document, title: '活动管理'},
  {role: 4, index: '4-1', icon: Document, title: '项目库'},
  {role: 4, index: '4-2', icon: Document, title: '待优化项目'}
];

// 处理菜单项选择
const handleSelect = (index) => {
  const navbarState = store.getters.isNavbarLocked;
  if (navbarState) {
    ElMessage.error('请先保存或放弃当前操作。');
    return;
  }

  switch (index) {
    case '1-1':
      currentComponent.value = user1ExpertLibraryManagement;
      break;
    case '1-2':
      router.push('/user1component/ExpertLibraryManagement/ExpertLibraryManagement');
      break;
    case '2-1':
      currentComponent.value = user2ProjectManagement;
      break;
    case '2-2':
      currentComponent.value = user1ExpertLibraryManagement;
      break;
    case '2-3-1':
      currentComponent.value = user2ProjectTemplateManagement;
      break;
    case '2-3-2':
      currentComponent.value = user2ActivityTemplateManagement;
      break;
    case '2-4':
      currentComponent.value = user2SocialOrganizationManagement;
      break;
    case '3-1':
      currentComponent.value = user3ProjectManagement;
      break;
    case '3-2':
      currentComponent.value = user2ProjectManagement;
      break;
    case '4-1':
      currentComponent.value = ProjectLibrary;
      break;
    case '4-2':
      currentComponent.value = WaitingOptimizationProject;
      break;
    default:
      currentComponent.value = null;
  }

  if (isMobile.value) {
    drawerVisible.value = false;
  }
};

const handleLogout = () => {
  store.dispatch('logout');
  router.push('/login');
};
const handleDrawerClose = () => {
  drawerVisible.value = false;
};

const checkExpertInvite = () => {
  const inviteCode = store.state.expert.inviteId;
  if (inviteCode == null) {
    return;
  }
  ElMessageBox({
    title: '邀请信息',
    message: h('p', null, [
      h('span', null, '你收到了一条专家邀请'),
      h('br'),
      h('span', null, '请点击确认以接受邀请。'),
    ]),
    showCancelButton: true,
    confirmButtonText: '接受',
    cancelButtonText: '暂不处理',
    closeOnClickModal: false,
    closeOnPressEscape: false,
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true
        instance.confirmButtonText = '请稍后...'
        axios.post(
            `${import.meta.env.VITE_BACKEND_IP}/api/expert/accept`,
            null,
            {
              params: {
                id: inviteCode,
                isAgree: 1,
                expertId: store.state.user.id
              },
              headers: {
                'token': store.state.token
              }
            }
        )
            .then(response => {
              console.log(response)
              if (response.data.code === 200) {
                done()
                ElMessage.success('您已成为专家，请重新登陆！')
                store.dispatch('setExpertInviteId', null);
                store.dispatch('logout')
              } else {
                throw new Error(response.data.msg || '提交失败')
              }
            })
            .catch(error => {
              ElMessage.error(`${error.message}`)
              instance.confirmButtonLoading = false
              instance.confirmButtonText = '接受'
            })
        return false
      } else {
        done()
      }
    }

  }).then((action) => {
  })

}
onMounted(() => {
  const handleResize = () => {
    isMobile.value = window.innerWidth <= 768;
    sidebarWidth.value = isMobile.value ? '0px' : '250px';
  };
  window.addEventListener('resize', handleResize);
  handleResize();
  checkExpertInvite();
});
</script>

<style scoped>
.top-navbar {
  background-color: #3ec474;
  color: white;
  padding: 0;
  display: flex;
  align-items: center;
}

.header-content {
  width: 100%;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mobile-menu-button {
  position: fixed;
  top: 15px;
  left: 15px;
  z-index: 10;
  border: none;
}

.left-menu {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.menu-title {
  margin-left: 10px;
}

.menu-title-mobile {
  width: 100%;
  text-align: center;
}

.right-menu {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.logout-button {
  color: #d7fbe8;
}

.sidebar {
  background-color: #ffffff;
}

.el-menu-vertical-demo {
  border-right: none;
  padding-top: 20px;
}

.main-content {
  padding: 20px;
  background-color: #ffffff;
}

.mobile-logout-button {
  width: 100%;
}

@media (max-width: 768px) {
  .header-content {
    justify-content: center;
  }

  .el-col {
    justify-content: center;
  }
}
</style>
