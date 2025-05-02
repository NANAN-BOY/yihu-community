<template>
  <!-- Dashboard -->
  <div>
    <!-- TopNavbar -->
    <el-header class="top-navbar">
      <div class="header-content">
        <!-- MobilePhoneMenuButton -->
        <el-button v-if="isMobile" class="mobile-menu-button" @click="drawerVisible = true">
          <el-icon>
            <Expand/>
          </el-icon>
        </el-button>
        <!-- Logo -->
        <el-col :span="isMobile ? 19 : 20" class="left-menu">
          <span :class="isMobile ? 'menu-title-mobile' : 'menu-title'"><strong>易互</strong></span>
          <div>
            <el-tag size="small" type="warning" v-if="store.state.connectionStatus==='connecting'">连接中</el-tag>
            <el-tag size="small" type="success" v-if="store.state.connectionStatus==='connected'">在线</el-tag>
            <el-tag size="small" type="danger" v-if="store.state.connectionStatus==='disconnected'">离线</el-tag>
          </div>
        </el-col>
        <el-col :span="1" class="right-menu">
          <el-button @click="openVIPAreaDialogVisible" type="warning" class="logout-button" color="warning">
            <div v-if="MembershipInfo == null && MembershipLevel===0">不是会员</div>
            <div v-if="MembershipInfo !== null && MembershipLevel===0">会员已过期</div>
            <div v-if="MembershipLevel===1">普通会员</div>
            <div v-if="MembershipError">请重试</div>
            <div v-if="MembershipStatusLoading">加载中..</div>
          </el-button>
          <!-- LogoutButton -->
          <el-button v-if="!isMobile" type="primary" @click="handleLogout" class="logout-button">安全登出</el-button>
        </el-col>
      </div>
    </el-header>
    <!-- Content -->
    <el-container style="height: auto">
      <!-- LeftSidebar -->
      <div>
        <!-- DesktopSidebar -->
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
          </el-menu>
        </el-aside>
        <!-- MobileSidebar -->
        <el-drawer v-model="drawerVisible" :size="'250px'" direction="ltr" :before-close="handleDrawerClose">
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
            <!-- OnlyMobileLogoutButton -->
            <el-menu-item v-if="isMobile" index="logout" @click="handleLogout">
              <el-button type="primary" class="mobile-logout-button">
                安全登出
              </el-button>
            </el-menu-item>
          </el-menu>
        </el-drawer>
      </div>
      <!-- MainContent -->
      <el-container>
        <el-main class="main-content">
          <component :is="currentComponent"/>
        </el-main>
      </el-container>
    </el-container>
  </div>
  <!-- MyVIPArea -->
  <el-dialog
      v-model="VIPAreaDialogVisible"
      title="会员中心"
      width="500"
      align-center
  >
    <span v-if="MembershipLevel !== 0">
        <p>购买日期: {{ MembershipInfo.buyDate }}</p>
        <p>到期时间: {{ MembershipInfo.deadline }}</p>
        <p>剩余天数: {{
            Math.ceil((new Date(MembershipInfo.deadline) - new Date()) / (1000 * 60 * 60 * 24)) > 0 ? Math.ceil((new Date(MembershipInfo.deadline) - new Date()) / (1000 * 60 * 60 * 24)) : 0
          }}</p>
        <p>会员等级: {{
            MembershipInfo.grade === 1 ? '普通会员' : MembershipInfo.grade === 2 ? '高级会员' : 'VIP会员'
          }}</p>
    </span>
    <span v-else-if="MembershipInfo !== null && MembershipLevel===0">
        <p>您的会员已经过期{{
            Math.ceil((new Date() - new Date(MembershipInfo.deadline)) / (1000 * 60 * 60 * 24))
          }}天了</p>
        <p>请及时续费</p>
    </span>
    <span v-else>
        <p>您还不是会员，点击立即购买</p>
    </span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="VIPAreaDialogVisible = false;openBuyVIPAreaDialogVisible();">{{
            MembershipLevel === 0 ? '立即购买' : '续费会员'
          }}
        </el-button>
        <el-button type="primary" @click="VIPAreaDialogVisible = false">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>
  <!-- BuyVIPArea -->
  <el-dialog
      v-model="BuyVIPAreaDialogVisible"
      title="购买会员"
      width="500"
      align-center
  >
    <el-form ref="buyVIPForm" v-loading="BuyYiHuLoading || VipListLoading" label-width="100px">
      <!-- 动态渲染会员列表 -->
      <div v-for="item in VipList" :key="item.id" class="vip-item">
        <h2>
          <el-icon>
            <Star/>
          </el-icon>
          {{ item.name }}
          <el-button
              :type="getButtonType(item.id)"
              @click="BuyYiHu_Vip(item.id)"
          >
            立即购买（¥{{ calculatePrice(item) }}）
          </el-button>
        </h2>
        <!-- 如果有折扣，显示折扣信息 -->
        <div v-if="item.discount < 1" class="discount-info">
          原价¥{{ item.price }}，{{ (item.discount * 10) }}折优惠
        </div>
      </div>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="BuyVIPAreaDialogVisible = false">取消</el-button>
      </div>
    </template>
  </el-dialog>
  <!-- PayInfo -->
  <el-dialog
      v-model="PayInfoDialogVisible"
      title="支付信息"
      width="500"
      align-center
  >
      <div>
        <p>请您使用支付宝支付：</p>
        <div style="text-align: center; margin-top: 20px;">
          <QRCode :value="PayInfo.qr_code" size="200"/>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="CheckPayStatus">我已付款</el-button>
        <el-button type="primary" @click="closePayInfoDialogVisible">关闭</el-button>
      </span>
  </el-dialog>
  <Communicate v-if="store.state.user.id!==null"/>
</template>

<script setup>
import {ref, onMounted, h, watch, onUnmounted} from 'vue';
import {useRouter} from 'vue-router';
import store from '../store';
import user2ProjectManagement from '../components/user2component/ProjectManagement/user2ProjectManagement.vue';

import user2ProjectTemplateManagement
  from '../components/user2component/TemplateManagement/ProjectTemplateManagement/ProjectTemplateManagement.vue';
import user2ActivityTemplateManagement
  from '../components/user2component/TemplateManagement/ActivityTemplateManagement.vue';
import user2SocialOrganizationManagement
  from '../components/user2component/SocialOrganizationManagement/SocialOrganizationManagement.vue';
//User1Component
import user1ExpertLibraryManagement
  from '../components/user1component/ExpertLibraryManagement/ExpertLibraryManagement.vue';
import ActivityExamine from "../components/user1component/ProjectExamine/ActivityExamine.vue";
import ProjectExamine from "../components/user1component/ProjectExamine/ProjectExamine.vue"
import SystemSetting from "../components/user1component/SystemSetting/SystemSetting.vue";
import ActivityQuestionnaireSetting
  from "../components/user1component/ActivityQuestionnaire/ActivityQuestionnaireSetting.vue";
//User3Component
import ExpertCustomization from '../components/user3component/ExpertCustomization/ExpertCustomization.vue';
import ProjectManagement from '../components/user3component/ProjectManagement/ProjectManagement.vue'
//User4Component
import ReceiveOrder from '../components/user4component/ReceiveOrder/ReceiveOrder.vue';
import MyReceiveOrder from "../components/user4component/MyReceiveOrder/MyReceiveOrder.vue";

//AllUserComponent
import aboutMyInfo from '../components/user/aboutMyInfo/aboutMyInfo.vue';
import Communicate from '../components/user/Communicate.vue'


import {Avatar, Document, Expand, Star, StarFilled, Tickets, UserFilled} from "@element-plus/icons-vue";
import {ElMessage, ElMessageBox} from "element-plus";
import axios from "axios";

const router = useRouter();
const drawerVisible = ref(false);
const sidebarWidth = ref('250px');
const isMobile = ref(false);
const currentComponent = ref(null);
//初始化用户展示页面
onMounted(() =>{
  switch (store.state.user.role) {
    case 1:
      currentComponent.value = user1ExpertLibraryManagement;
      break;
    case 2:
      currentComponent.value = user2ProjectManagement;
      break;
    case 3:
      currentComponent.value = ProjectManagement;
      break;
    case 4:
      currentComponent.value = ReceiveOrder;
      break;
    default:
      return null;
  }
})

// MENUConfig
const menus = [
  {role: 1, index: '1-1', icon: Document, title: '审核', children: [
      {index: '1-1-1', title: '项目申报审核'},
      {index: '1-1-2', title: '活动审核'}
    ]
  },
  {role: 1, index: '1-2', icon: Document, title: '专家库管理'},
  {role: 1, index: '1-3', icon: Avatar, title: '系统管理'},
  {role: 1, index: '1-4', icon: Tickets, title: '满意度调查模板管理'},
  {role: 1, index: '1-5', icon: Document, title: '关于我的信息'},

  {role: 2, index: '2-1', icon: Document, title: '项目管理'},
  {role: 2, index: '2-2', icon: Avatar, title: '专家库管理'},
  {
    role: 2, index: '2-3', icon: Tickets, title: '模板管理', children: [
      {index: '2-3-1', title: '项目申报模板'},
      {index: '2-3-2', title: '活动细节模板'}
    ]
  },
  {role: 2, index: '2-4', icon: UserFilled, title: '社会组织管理'},
  {role: 2, index: '2-5', icon: Document, title: '关于我的信息'},

  {role: 3, index: '3-1', icon: Document, title: '项目管理'},
  {role: 3, index: '3-2', icon: StarFilled, title: '专家定制'},
  {role: 3, index: '3-3', icon: Document, title: '关于我的信息'},

  {role: 4, index: '4-1', icon: Document, title: '接单广场'},
  {role: 4, index: '4-2', icon: Document, title: '我的订单'},
  {role: 4, index: '4-3', icon: Document, title: '关于我的信息'}
];

// HandleMENUSelect
const handleSelect = (index) => {
  const navbarState = store.getters.isNavbarLocked;
  if (navbarState) {
    ElMessage.error('请先保存或放弃当前操作。');
    return;
  }

  switch (index) {
    case '1-1-1':
      currentComponent.value = ProjectExamine;
      break;
    case '1-1-2':
      currentComponent.value = ActivityExamine;
      break;
    case '1-2':
      currentComponent.value = user1ExpertLibraryManagement;
      break;
    case '1-3':
      currentComponent.value = SystemSetting;
      break;
    case '1-4':
      currentComponent.value = ActivityQuestionnaireSetting;
      break;
    case '1-5':
      currentComponent.value = aboutMyInfo;
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
    case '2-5':
      currentComponent.value = aboutMyInfo;
      break;
    case '3-1':
      currentComponent.value = ProjectManagement;
      break;
    case '3-2':
      currentComponent.value = ExpertCustomization;
      break;
    case '3-3':
      currentComponent.value = aboutMyInfo;
      break;
    case '4-1':
      currentComponent.value = ReceiveOrder;
      break;
    case '4-2':
      currentComponent.value = MyReceiveOrder;
      break;
    case '4-3':
      currentComponent.value = aboutMyInfo;
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
        store.dispatch('setExpertInviteId', null);
        done()
      }
    }

  })
}
//MyVIPArea
const MembershipStatusLoading = ref(false);
const MembershipLevel = ref(null);
const MembershipError = ref(false);
const MembershipInfo = ref(null);
const checkMembershipStatus = () => {
  MembershipStatusLoading.value = true;
  MembershipError.value = false;
  if (store.state.user.id === null) {
    MembershipStatusLoading.value = false;
    MembershipError.value = true;
    return;
  }
  axios.get(
      `${import.meta.env.VITE_BACKEND_IP}/api/user/vip`,
      {
        params: {
          userId: store.state.user.id
        },
        headers: {
          'token': store.state.token
        }
      }
  )
      .then(response => {
        if (response.data.code === 200) {
          if (response.data.data === null) {
            MembershipLevel.value = 0;
          } else {
            MembershipLevel.value = response.data.data.grade;
            if (new Date(response.data.data.deadline) < new Date()) {
              MembershipLevel.value = 0;
            }
            MembershipInfo.value = response.data.data;
          }
          MembershipStatusLoading.value = false;
        }
      })
      .catch(error => {
        MembershipStatusLoading.value = false;
        MembershipError.value = true;
        ElMessage.error(`${error.message}`)
      })
}
const VIPAreaDialogVisible = ref(false);
const openVIPAreaDialogVisible = () => {
  if (MembershipError.value === true) {
    checkMembershipStatus();
    return;
  }
  VIPAreaDialogVisible.value = true;
}
//BuyVipArea
const BuyVIPAreaDialogVisible = ref(false);
const VipListLoading = ref(false);
const VipList = ref([]);
const openBuyVIPAreaDialogVisible = () => {
  BuyVIPAreaDialogVisible.value = true;
  getVipList();
}
const closeBuyVIPAreaDialogVisible = () => {
  BuyVIPAreaDialogVisible.value = false;
}
const getVipList = () => {
  VipListLoading.value = true;
  axios.get(
      `${import.meta.env.VITE_BACKEND_IP}/api/product/get-list`,
      {
        params: {
          type: 1
        },
        headers: {
          'token': store.state.token
        }
      }
  )
      .then(response => {
        console.log(response.data);
        if (response.data.code === 200) {
          if (response.data.data !== null) {
            VipList.value = response.data.data
          } else {
            ElMessage.error(`加载失败，请重试`)
          }
        }
      })
      .catch(error => {
        ElMessage.error(`${error.message}`)
      }).finally(() => {
    VipListLoading.value = false;
  })
}
// 计算实际价格（考虑折扣）
const calculatePrice = (item) => {
  return (item.price * item.discount).toFixed(2);
};

// 根据会员ID返回按钮类型（保持你原来的样式逻辑）
const getButtonType = (id) => {
  return id === 1 ? 'info' : id === 2 ? 'success' : 'primary';
};

//PayInfo
const PayInfoDialogVisible = ref(false);
const openPayInfoDialogVisible = () => {
  PayInfoDialogVisible.value = true;
}
const closePayInfoDialogVisible = () => {
  PayInfoDialogVisible.value = false;
}
import QRCode from 'qrcode.vue';

const PayInfo =ref(null);
const BuyYiHuLoading = ref(false);
const BuyYiHu_Vip = (type) => {
  BuyYiHuLoading.value = true;
  axios.get(
      `${import.meta.env.VITE_BACKEND_IP}/api/order/pay/create`,
      {
        params: {
          type: type,
        },
        headers: {
          'token': store.state.token
        }
      }
  )
      .then(response => {
        console.log(response.data);
        if (response.data.alipay_trade_precreate_response) {
          if (response.data.alipay_trade_precreate_response.code === "10000") {
            PayInfo.value = response.data.alipay_trade_precreate_response;
            openPayInfoDialogVisible();
            BuyYiHuLoading.value = false;
          } else {
            BuyYiHuLoading.value = false;
            throw new Error(response.data.msg || '购买失败');
          }
        }
        else{
          BuyYiHuLoading.value = false;
          throw new Error(response.data.msg || '购买失败');
        }
      })
      .catch(error => {
        ElMessage.error(`${error.message}`);
        BuyYiHuLoading.value = false;
      });
};
const CheckPayStatus = () => {
  axios.get(
      `${import.meta.env.VITE_BACKEND_IP}/api/order/query`,
      {
        params: {
          orderNo: PayInfo.value.out_trade_no,
        },
        headers: {
          'token': store.state.token
        }
      }
  )
      .then(response => {
        console.log(response.data);
        if (response.data.code === 200) {
          console.log(response.data);
          if (response.data.msg === "success") {
            ElMessage.success('支付成功，感谢您的购买。');
            closePayInfoDialogVisible();
            closeBuyVIPAreaDialogVisible();
            checkMembershipStatus();
            openVIPAreaDialogVisible();
          } else {
            ElMessage.error('没有查询到您的支付信息！');
          }
        } else {
          ElMessage.error('没有查询到您的支付信息！');
        }
      })
      .catch(error => {
        ElMessage.error(`${error.message}`);
      });
};

const stopWatch = watch(
    () => store.state.user.id, // 监听 user.id
    (newId, oldId) => {
      // 当 id 从 null 变为有效值时触发检查
      if (newId !== null && newId !== oldId) {
        checkMembershipStatus();
      }
    }
)
onUnmounted(() => {
  stopWatch()
})
onMounted(() => {
  const handleResize = () => {
    isMobile.value = window.innerWidth <= 768;
    sidebarWidth.value = isMobile.value ? '0px' : '250px';
  };
  window.addEventListener('resize', handleResize);
  handleResize();
  checkExpertInvite();
  checkMembershipStatus();
});
</script>

<style scoped>
.top-navbar {
  background-color: #3e72f5;
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
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
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
  min-height: calc(100vh - 60px);
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

/* 会员列表 */
.vip-item {
  margin-bottom: 20px;
  padding: 10px;
  border-bottom: 1px solid #eee;
}

.vip-item h1 {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0;
}

.discount-info {
  color: #f56c6c;
  font-size: 12px;
  margin-left: 30px;
  margin-top: 5px;
}
</style>
