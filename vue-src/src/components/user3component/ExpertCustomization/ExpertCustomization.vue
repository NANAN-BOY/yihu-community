<script setup>
import {computed, onBeforeUnmount, onMounted, ref} from "vue";
import axios from "axios";
import store from "../../../store";
import {Star} from "@element-plus/icons-vue";
import QRCode from "qrcode.vue";
import {ElMessage} from "element-plus";
import {EventBus} from "../../../utils/event-bus";

const OrderList = ref([])
const currentPage = ref(1)
const loading = ref(false)
const error = ref('')
const hasMore = ref(true)
const noMore = computed(() => !hasMore.value)
const disabled = computed(() => loading.value || noMore.value)

const OrderListLoad = async () => {
  if (disabled.value) return
  if (loading.value) return
  try {
    loading.value = true
    error.value = ''

    const response = await axios.post(
        `${import.meta.env.VITE_BACKEND_IP}/api/order/get-myOrderList`,
        {type: 0, status: null},
        {
          params: {pageNum: currentPage.value, pageSize: 10},
          headers: {token: store.state.token}
        }
    )
    if (response.data.code === 200) {
      // success
      OrderList.value = [...OrderList.value, ...response.data.data.list]
      hasMore.value = response.data.data.hasNextPage
      currentPage.value++
    } else if (response.data.code === 404) {
      // Order not found , list is empty
      hasMore.value = false

      if (currentPage.value === 1) OrderList.value = []
    } else {
      // Other error
      error.value = response.data.msg || '请求出现异常'
    }

  } catch (err) {
    // Network error
    error.value = '数据加载失败，请稍后再试'
  } finally {
    loading.value = false
  }
}

const refreshOrderList = async () => {
  currentPage.value = 1
  OrderList.value = []
  hasMore.value = true
  error.value = ''
  loading.value = false
  await OrderListLoad()
  await OrderListLoad()
}


//BuyBusinessArea
const BuyBusinessAreaDialogVisible = ref(false);
const openBuyBusinessPAreaDialogVisible = () => {
  BuyBusinessAreaDialogVisible.value = true;
}
const closeBuyBusinessAreaDialogVisible = () => {
  BuyBusinessAreaDialogVisible.value = false;
}
//PayInfo
const PayInfoDialogVisible = ref(false);
const openPayInfoDialogVisible = () => {
  PayInfoDialogVisible.value = true;
}
const closePayInfoDialogVisible = () => {
  PayInfoDialogVisible.value = false;
}
const PayInfo = ref(null);
const BuyYiHuLoading = ref(false);
const BuyBusiness = (type) => {
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
        if (response.data.alipay_trade_precreate_response) {
          if (response.data.alipay_trade_precreate_response.code === "10000") {
            PayInfo.value = response.data.alipay_trade_precreate_response;
            openPayInfoDialogVisible();
            BuyYiHuLoading.value = false;
          } else {
            throw new Error(response.data.msg || '购买失败');
            BuyYiHuLoading.value = false;
          }
        } else {
          throw new Error(response.data.msg || '购买失败');
          BuyYiHuLoading.value = false;
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
        if (response.data.code === 200) {
          if (response.data.msg === "success") {
            ElMessage.success('支付成功，您已经成功购买。');
            closePayInfoDialogVisible();
            closeBuyBusinessAreaDialogVisible();
            refreshOrderList();
            return;
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
// BusinessCommunicate
const businessLoading = ref(false)
const openExpertCommunicate = (business) => {
  store.state.expert.business = business;
}
const checkBusinessCommunicate = (orderNo) => {
  businessLoading.value = true;
  axios.get(
      `${import.meta.env.VITE_BACKEND_IP}/api/order/get-business`,
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
          ElMessage.error('订单等待中！')
        }
      })
      .catch(error => {
        businessLoading.value = false;
        ElMessage.error(`${error.message}`)
      })

}
onMounted(() => {
  EventBus.on('endOrder1',refreshOrderList);
});
onBeforeUnmount(() => {
  EventBus.off('endOrder1',refreshOrderList);
});
</script>

<template>
  <el-breadcrumb separator="/">
    <el-breadcrumb-item><strong>专家定制</strong></el-breadcrumb-item>
  </el-breadcrumb>
  <h1>定制服务</h1>
  <el-button type="primary" @click="openBuyBusinessPAreaDialogVisible">创建定制服务</el-button>
  <!-- 我的订单无限滚动列表 -->
  <el-button type="primary" @click="refreshOrderList" v-loading="loading">刷新</el-button>
  <div class="infinite-list-wrapper" style="overflow: auto">
    <ul
        v-infinite-scroll="OrderListLoad"
        class="list"
        :infinite-scroll-disabled="disabled"
        v-loading="businessLoading"
    >
      <li v-for="Order in OrderList" :key="Order.orderNo" class="list-item"
          @click="checkBusinessCommunicate(Order.orderNo)">
        <el-tag type="warning" v-if="Order.status === 0">未支付</el-tag>
        <el-tag type="success" v-if="Order.status  === 1">待接单</el-tag>
        <el-tag type="danger" v-if="Order.status  === 2">进行中</el-tag>
        <el-tag type="danger" v-if="Order.status  === 3">已完结</el-tag>
        <div>{{ Order.createAt }}</div>
      </li>
      <li v-if="loading" v-loading="loading" class="list-item"></li>
    </ul>
    <p v-if="loading">加载中...</p>
    <p v-if="noMore">没有更多数据了</p>
    <p v-if="error" style="color: red">{{ error }}</p>
  </div>
  <!-- BuyVIPArea -->
  <el-dialog
      v-model="BuyBusinessAreaDialogVisible"
      title="购买服务"
      width="500"
      align-center
  >
    <el-form v-loading="BuyYiHuLoading" ref="buyVIPForm" label-width="100px">
      <h1>
        <el-icon>
          <Star/>
        </el-icon>
        一对一专家定制服务
        <el-button type="success" @click="BuyBusiness(0)">立即购买</el-button>
      </h1>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="BuyBusinessAreaDialogVisible = false">取消</el-button>
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
