<script setup>
import {computed, ref} from "vue";
import axios from "axios";
import store from "../../../store";

const OrderList = ref([])
const currentPage = ref(1)
const loading = ref(false)
const error = ref('')
const hasMore = ref(true)
const noMore = computed(() => !hasMore.value)
const disabled = computed(() => loading.value || noMore.value)
const OrderListLoad = async () => {
  if (disabled.value) return
  try {
    loading.value = true
    error.value = ''
    const response = await axios.post(
        `${import.meta.env.VITE_BACKEND_IP}/api/expert/orderList`,
        {},
        {
          params: {
            pageNum: currentPage.value,
            pageSize: 10
          },
          headers: {
            token: store.state.token
          }
        }
    )
    console.log(response.data)
    if (response.data.code === 200) {
      OrderList.value = [...OrderList.value, ...response.data.data.list]
      hasMore.value = response.data.data.hasNextPage
      currentPage.value++
    }
  } catch (err) {
    error.value = '数据加载失败，请稍后再试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <el-breadcrumb separator="/">
    <el-breadcrumb-item><strong>接单广场</strong></el-breadcrumb-item>
  </el-breadcrumb>
  <h1>定制服务</h1>
  <el-button type="primary" @click="openBuyBusinessPAreaDialogVisible">创建定制服务</el-button>
  <!-- 我的订单无限滚动列表 -->
  <div class="infinite-list-wrapper" style="overflow: auto">
    <ul
        v-infinite-scroll="OrderListLoad"
        class="list"
        :infinite-scroll-disabled="disabled"
    >
      <li v-for="Order in OrderList" :key="Order.id" class="list-item" @click="">
        <div>{{ Order.createAt }}</div>
      </li>
      <li v-if="loading" v-loading="loading" class="list-item"></li>
    </ul>
    <p v-if="loading">加载中...</p>
    <p v-if="noMore">没有更多数据了</p>
    <p v-if="error" style="color: red">{{ error }}</p>
  </div>
</template>

<style scoped>
.infinite-list-wrapper {
  height: 80vh;
  text-align: center;
}

.infinite-list-wrapper .list {
  padding: 0;
  margin: 0;
  list-style: none;
}

.infinite-list-wrapper .list-item:hover {
  background: #b6b6b6;
  transition: background 0.3s ease;
  cursor: pointer;
}

.infinite-list-wrapper .list-item {
  border-radius: 10px;
  padding: 10px;
  display: flex;
  align-items: center;
  //justify-content: center; height: 50px;
  background: #f5f5f5;
  color: #000000;
}
</style>
