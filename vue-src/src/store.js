import {createStore} from 'vuex';
import router from './router';
import {ElMessage} from 'element-plus';

const store = createStore({
  state: {
    token: localStorage.getItem('token') || '', // 从本地存储获取 token
    // 用户信息
    user: {
      id: null,                // 用户唯一标识符
      name: '',                // 用户名
      phone: '',         // 用户电话号码
      description: '',
      role: '',
      status: '',
      balance: '',
      createAt: '',
      lastLoginTime: '',
      updaterId: '',
      updateAt: '',
    },
    navbar: {
      isLocked: false,               // 导航栏是否锁定
      lockReason: '',                // 锁定原因
    },
    expert: {
      inviteId: null,
      business: {
        acceptExpertId: null,
          applyUserId: null,
        status: 0,
      },
      input: {
        inputIsVisible: true,
        hiddenReasons: "",
      }
    }
  },
  getters: {
    token: state => state.token,      // 获取 token
    isAuthenticated: state => !!state.token, // 检查用户是否已认证
    userInfo: state => state.user,   // 获取用户信息
    isNavbarLocked: state => state.navbar.isLocked, // 导航栏是否锁定
    navbarLockReason: state => state.navbar.lockReason, // 获取锁定原因
    expertInviteId: state => state.expert.inviteId, // 获取专家邀请码
    business: state => state.expert.business, // 获取专家邀请码
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
        id: null,         // 更新字段名称
        name: '',         // 使用新的字段名
        phone: '',        // 使用新的字段名
        description: '',
        role: '',
        status: '',
        balance: '',
        createAt: '',
        lastLoginTime: '',
        updaterId: '',
        updateAt: '',
      };
      state.token = '';
      localStorage.removeItem('token');
      state.navbar.isLocked = false;
    },
    SET_NAVBAR_LOCK(state, payload) {
      state.navbar.isLocked = payload.isLocked; // 设置导航栏锁定状态
      state.navbar.lockReason = payload.lockReason || ''; // 设置锁定原因
    },
    SET_EXPERT_INVITE_ID(state, inviteId) {
      state.expert.inviteId = inviteId; // 设置专家邀请码
    },
    SET_BUSINESS(state, business) {
      state.expert.business = business;
    }
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
      localStorage.removeItem('token');
      commit('LOGOUT');
      ElMessage.success('您已安全登出');
      router.push('/login'); // 跳转到登录页
    },
    lockNavbar({ commit }, reason) {
      commit('SET_NAVBAR_LOCK', { isLocked: true, lockReason: reason });
    },
    unlockNavbar({ commit }) {
      commit('SET_NAVBAR_LOCK', { isLocked: false, lockReason: '' });
    },
    setExpertInviteId({ commit }, inviteId) {
      commit('SET_EXPERT_INVITE_ID', inviteId); // 提交设置专家邀请码的 mutation
    },
    clearExpertInviteId({ commit }) {
      commit('SET_EXPERT_INVITE_ID', null); // 提交清空专家邀请码的 mutation
    }
  },
});

export default store;
