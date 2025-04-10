import {createRouter, createWebHistory} from 'vue-router';
import store from './store.js';

// 使用动态导入实现路由级懒加载（Webpack自动代码分割）
const routes = [
  {
    path: '/',
    component: () => import(/* webpackChunkName: "dashboard" */ './views/dashboard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'login',
    component: () => import(/* webpackChunkName: "auth" */ './views/login.vue')
  },
  {
    path: '/register',
    name: 'register',
    component: () => import(/* webpackChunkName: "auth" */ './views/register.vue')
  },
  {
    path: '/resetPassword',
    name: 'resetPassword',
    component: () => import(/* webpackChunkName: "auth" */ './views/resetPassword.vue')
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: () => import(/* webpackChunkName: "dashboard" */ './views/dashboard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/expertInvitedRegister/:inviteId',
    name: 'expertInvitedRegister',
    component: () => import(/* webpackChunkName: "expert" */ './views/expertInvitedRegister.vue'),
    meta: { requiresAuth: false }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 优化后的路由守卫（增加加载状态管理）
router.beforeEach(async (to, from, next) => {
  // 显示全局加载动画
  store.commit('SET_LOADING', true);

  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);

  try {
    // // 每次路由跳转前检查用户状态（可优化为应用启动时加载）
    // if (!store.getters.isAuthenticated) {
    //   await store.dispatch('getUserInfo');
    // }

    if (requiresAuth && !store.getters.isAuthenticated) {
      next({ name: 'login' });
    } else {
      next();
    }
  } catch (error) {
    console.error('路由守卫错误:', error);
    next({ name: 'login' });
  } finally {
    // 确保隐藏加载动画
    store.commit('SET_LOADING', false);
  }
});

export default router;
