<script setup>
import {computed, h, ref} from "vue";
import axios from "axios";
import store from "../../../store";
import {ElMessage, ElMessageBox} from "element-plus";

const OrderList = ref([])
const currentPage = ref(1)
const loading = ref(false)
const error = ref('')
const hasMore = ref(true)
const noMore = computed(() => !hasMore.value)
const disabled = computed(() => loading.value || noMore.value)

const OrderListLoad = async () => {
  // Skip if already loading or no more data available
  if (disabled.value) return
  try {
    loading.value = true
    error.value = ''
    const response = await axios.get(
        `${import.meta.env.VITE_BACKEND_IP}/api/order/get-preemptOrderList`,
        {
          params: {
            status: 2,
            pageNum: currentPage.value,
            pageSize: 10
          },
          headers: {
            token: store.state.token
          }
        }
    )
    // Handle business status codes
    if (response.data.code === 200) {
      // Handle empty data response
      if (!response.data.data || !response.data.data.list) {
        hasMore.value = false
        return
      }
      // Process new orders and fetch buyer info for each
      const newOrders = response.data.data.list
      const ordersWithBuyerInfo = await Promise.all(
          newOrders.map(async (order) => {
            try {
              const buyerInfo = await getUserInfo(order.buyerId)
              return {...order, buyerInfo}
            } catch {
              return {...order, buyerInfo: "ERROR"}
            }
          })
      )

      // Merge new orders with existing list
      OrderList.value = [...OrderList.value, ...ordersWithBuyerInfo]

      // Update pagination state
      hasMore.value = response.data.data.hasNextPage
      currentPage.value++ // Only increment page on successful data fetch
    } else {
      // Handle business logic errors (e.g., 404 no more data)
      hasMore.value = false
    }
  } catch (err) {
    error.value = 'Failed to load data, please try again later'
    // Preserve hasMore state for retry on network errors
  } finally {
    loading.value = false
  }
}
const getUserInfo = async (userId) => {
  try {
    // Make a request to get the user info
    const response = await axios.get(
        `${import.meta.env.VITE_BACKEND_IP}/api/user/get-info`,
        {
          params: {
            userId: userId
          }
        }
    )
    return response.data.data
  } catch (error) {
    return "ERROR"
  }
}
</script>

<template>
  <el-breadcrumb separator="/">
    <el-breadcrumb-item><strong>我的订单</strong></el-breadcrumb-item>
  </el-breadcrumb>
  <h1>定制服务</h1>
  <!-- 我的订单无限滚动列表 -->
  <div class="infinite-list-wrapper" style="overflow: auto">
    <ul
        v-infinite-scroll="OrderListLoad"
        class="list"
        :infinite-scroll-disabled="disabled"
    >
      <li v-for="Order in OrderList" :key="Order.orderNo" class="list-item" @click="">
        <div>客户{{ Order.buyerInfo.name }}的订单</div>
        &nbsp
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
