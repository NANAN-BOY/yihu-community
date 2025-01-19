import { createRouter, createWebHistory } from 'vue-router';
import store from './store.js';  // Vuex
import login from './views/login.vue';
import dashboard from './views/dashboard.vue';
import register from './views/register.vue';
import expertInvitedRegister from './views/expertInvitedRegister.vue';
const routes = [
  {
    path: '/',
    component: dashboard,
    meta: { requiresAuth: true },
  },
  {
    path: '/login',
    name: 'login',
    component: login,
  },
  {
    path: '/register',
    name: 'register',
    component: register,
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: dashboard,
    meta: { requiresAuth: true },  // 需要身份验证
  },
  {
    path: '/expertInvitedRegister/:inviteId',
    name: 'expertInvitedRegister',
    component: expertInvitedRegister,
    meta: { requiresAuth: false },  // 需要身份验证
  },
];

const router = createRouter({
  history: createWebHistory(),  // 使用 createWebHistory() 以去掉 # 号
  routes,
});

// 添加路由守卫
router.beforeEach(async (to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const isAuthenticated = store.getters.isAuthenticated; // Vuex 中的登录状态

  if (requiresAuth && !isAuthenticated) {
    try {
      // 如果没有登录，尝试获取用户信息
      await store.dispatch('getUserInfo');
      if (store.getters.isAuthenticated) {
        next();  // 如果已登录，允许进入路由
      } else {
        // 如果未登录，跳转到登录页面
        next({ name: 'login' });
      }
    } catch (error) {
      console.error('获取用户信息失败:', error);
      next({ name: 'login' });  // 获取用户信息失败，也跳转到登录页
    }
  } else {
    next();  // 如果不需要身份验证，直接进入
  }
});

export default router;
