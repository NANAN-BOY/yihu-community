// vite.config.js
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [vue()],
  define: {
    __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: true, // 启用特性标志
  },
  server: {
    host: '0.0.0.0', // 监听所有地址
    port: 3000, // 你可以指定一个端口，默认为3000
    strictPort: true, // 如果指定的端口被占用，则抛出错误
    open: true, // 启动时自动打开浏览器
  },
});
