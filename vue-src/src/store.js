import { createStore } from 'vuex';
import router from './router';
import { ElNotification } from 'element-plus'; // 导入 ElNotification

const store = createStore({
  state: {
    token: localStorage.getItem('token') || '', // 从本地存储获取 token
    // 用户信息
    user: {
      user_id: null,                // 用户唯一标识符
      user_name: '',                // 用户名
      user_phoneNumber: '',         // 用户电话号码
      user_role: '',                // 用户角色
      user_accountStatus: '',       // 用户账号状态
    },
    navbar: {
      isLocked: false,               // 导航栏是否锁定
      lockReason: '',                // 锁定原因
    },
  },
  getters: {
    token: state => state.token,      // 获取 token
    isAuthenticated: state => !!state.token, // 检查用户是否已认证
    userInfo: state => state.user,   // 获取用户信息
    isNavbarLocked: state => state.navbar.isLocked, // 导航栏是否锁定
    navbarLockReason: state => state.navbar.lockReason, // 获取锁定原因
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token; // 设置 token
    },
    SET_USER(state, userData) {
      state.user = { ...state.user, ...userData }; // 更新用户信息
    },
    LOGOUT(state) {
      state.user = {
        user_id: null,
        user_name: '',
        user_phoneNumber: '',
        user_role: '',
        user_accountStatus: '',
      };
      state.token = ''; // 清除 token
      localStorage.removeItem('token');
      state.navbar.isLocked = false; // 解锁导航栏
    },
    SET_NAVBAR_LOCK(state, payload) {
      state.navbar.isLocked = payload.isLocked; // 设置导航栏锁定状态
      state.navbar.lockReason = payload.lockReason || ''; // 设置锁定原因
    },
  },
  actions: {
    setToken({ commit }, token) {
      commit('SET_TOKEN', token); // 提交设置 token 的 mutation
      localStorage.setItem('token', token); // 将 token 存储到 localStorage
    },
    setUser({ commit }, userData) {
      commit('SET_USER', userData); // 提交设置用户信息的 mutation
    },
    logout({ commit }) {
      localStorage.removeItem('token'); // 从本地存储移除 token
      commit('LOGOUT'); // 提交注销的 mutation
      // 使用 ElNotification 显示成功的登出消息
      ElNotification({
        title: '成功安全登出',
        message: '您已成功登出。',
        type: 'success',
        duration: 3000, // 自动关闭时间
      });
      router.push('/login'); // 跳转到登录页
    },
    // 锁定导航栏
    lockNavbar({ commit }, reason) {
      commit('SET_NAVBAR_LOCK', { isLocked: true, lockReason: reason });
    },
    // 解锁导航栏
    unlockNavbar({ commit }) {
      commit('SET_NAVBAR_LOCK', { isLocked: false, lockReason: '' });
    },
  },
});

export default store;
