import { createStore } from 'vuex';
import router from './router';
import {ElMessage} from "element-plus";  // 确保导入 router 对象

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
  },
  getters: {
    token: state => state.token,  // 获取 token
    isAuthenticated: state => !!state.token,  // 检查用户是否已认证
    userInfo: state => state.user,  // 获取用户信息
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token;  // 设置 token
    },
    SET_USER(state, userData) {
      state.user = { ...state.user, ...userData };  // 更新用户信息
    },
    LOGOUT(state) {
      state.user = {
        user_id: null,
        user_name: '',
        user_phoneNumber: '',
        user_role: '',
        user_accountStatus: '',
      };
      state.token = '';  // 清除 token
    },
  },
  actions: {
    setToken({ commit }, token) {
      commit('SET_TOKEN', token);  // 提交设置 token 的 mutation
      localStorage.setItem('token', token); // 将 token 存储到 localStorage
    },
    setUser({ commit }, userData) {
      commit('SET_USER', userData);  // 提交设置用户信息的 mutation
    },
    logout({ commit }) {
      localStorage.removeItem('token');  // 从本地存储移除 token
      commit('LOGOUT');  // 提交注销的 mutation
      router.push('/login'); // 跳转到登录页
      ElMessage.success('成功安全登出！');
    },
    // 获取用户信息
    async fetchUserInfo({ commit, state }) {
      try {
        const response = await fetch('http://localhost:3001/api/user-info', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${state.token}`,
          },
        });

        if (!response.ok) {
          throw new Error('身份已过期');  // 如果响应失败，抛出错误
        }

        const data = await response.json();
        if (data.user) {
          commit('SET_USER', data.user); // 设置用户信息
        }
      } catch (error) {
        console.error('获取用户信息失败', error);
        // 提示身份过期
        ElMessage.error('身份已过期，请重新登录');
        // 清除 token 和用户信息，跳转到登录页
        commit('LOGOUT');
        await router.push('/login');
      }
    },
  },
});

export default store;
