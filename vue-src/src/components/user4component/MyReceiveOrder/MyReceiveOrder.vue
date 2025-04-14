<script setup>
import {computed, ref} from "vue";
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
const reloadOrderList = async () => {
  // 重置所有状态到初始值
  currentPage.value = 1
  OrderList.value = []
  hasMore.value = true
  error.value = ''
  loading.value = false
  await OrderListLoad()
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
//打开订单沟通页面
const businessLoading = ref(false)
const openExpertCommunicate = (business) => {
  store.state.expert.business = business;
}
const checkBusinessCommunicate = (orderNo) => {
  businessLoading.value = true;
  axios.get(
      `${import.meta.env.VITE_BACKEND_IP}/api/order/get-myBusiness`,
      {
        params: {orderNo: orderNo},
        headers: {'token': store.state.token}
      }
  )
      .then(response => {
        businessLoading.value = false;
        if (response.data.code === 200) {
          const business = response.data.data
          openExpertCommunicate(business)
        } else if (response.data.code === 404) {
          ElMessage.error('订单不存在！')
        }
      })
      .catch(error => {
        businessLoading.value = false;
        ElMessage.error(`${error.message}`)
      })
}

//查看订单详细信息
const checkOrderDetailisVisible = ref(false)
const nowOrder = ref(null);
const openCheckOrderDetail = (order) => {
  console.log(order)
  nowOrder.value = order;
  checkOrderDetailisVisible.value = true;
}
const closeCheckOrderDetail = () => {
  checkOrderDetailisVisible.value = false;
  nowOrder.value = null;
}
//专家订单结束逻辑
const endOrder = (orderNo) => {
  ElMessageBox.confirm(
      '请确认您已经完成了用户诉求',
      '确定要结束该订单吗？',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
        // 新增按钮加载状态控制
        confirmButtonLoading: false,
        cancelButtonDisabled: false,
        // 添加对话框关闭前钩子
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true
            instance.cancelButtonDisabled = true

            axios.post(
                `${import.meta.env.VITE_BACKEND_IP}/api/order/finishOrder`,
                {},
                {
                  params: {orderNo: orderNo},
                  headers: {'token': store.state.token}
                }
            )
                .then(response => {
                  if (response.data.code === 200) {
                    ElMessage({
                      message: "订单结束成功！",
                      type: "success",
                    });
                    done();
                    closeCheckOrderDetail();
                    reloadOrderList();
                  } else {
                    ElMessage.error(response.data.msg || '订单结束失败');
                  }
                })
                .catch(error => {
                  ElMessage.error(error.message || '请求失败')
                })
                .finally(() => {
                  instance.confirmButtonLoading = false
                  instance.cancelButtonDisabled = false
                })
          } else {
            done() // 取消操作直接关闭
          }
        }
      }
  )
}

</script>

<template>
  <div v-if="!checkOrderDetailisVisible">
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
      <li v-for="Order in OrderList" :key="Order.orderNo" class="list-item"
          @click="openCheckOrderDetail(Order)" v-loading="businessLoading">
        <el-tag type="danger" v-if="Order.status  === 2">进行中</el-tag>
        <el-tag type="danger" v-if="Order.status  === 3">已完结</el-tag>
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
  </div>
  <div v-if="checkOrderDetailisVisible">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item><strong @click="closeCheckOrderDetail">我的订单</strong></el-breadcrumb-item>
      <el-breadcrumb-item><strong>订单详情</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <br>
    <el-page-header @back="closeCheckOrderDetail" title="返回">
      <template #content>
        <span class="text-large font-600 mr-3"> 订单详情 </span>
      </template>
    </el-page-header>
    <br>
    <el-button type="primary" @click="checkBusinessCommunicate(nowOrder.orderNo)">进入订单</el-button>
    <el-button type="warning" @click="endOrder(nowOrder.orderNo)">结束订单</el-button>
    <el-form>
      <el-form-item label="订单号:">{{ nowOrder.orderNo }}
      </el-form-item>
      <el-form-item label="订单状态">
        <el-tag type="danger" v-if="nowOrder.status  === 2">进行中</el-tag>
        <el-tag type="danger" v-if="nowOrder.status  === 3">已完结</el-tag>
      </el-form-item>
      <el-form-item label="订单金额">
        <el-input v-model="nowOrder" disabled></el-input>
        <el-tag type="danger" v-if="nowOrder.status  === 2">元</el-tag>
        <el-tag type="danger" v-if="nowOrder.status  === 3">元</el-tag>
      </el-form-item>
      <el-form-item label="下单时间">
        <el-input v-model="nowOrder.createAt" disabled></el-input>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.infinite-list-wrapper {
  height: 750px;
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
  background: #f5f5f5;
  color: #000000;
}
@media (max-width: 768px) {
  .infinite-list-wrapper {
    height: 730px;
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
    background: #f5f5f5;
    color: #000000;
  }

  .infinite-list-wrapper .list-item + .list-item {
    margin-top: 10px;
  }
}
</style>
