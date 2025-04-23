<script setup>
import {computed, nextTick, onBeforeUnmount, onMounted, ref, watch} from "vue";
import axios from "axios";
import store from "../../../store";
import {Star} from "@element-plus/icons-vue";
import QRCode from "qrcode.vue";
import {ElMessage} from "element-plus";
import {EventBus} from "../../../utils/event-bus";

const pageNum = ref(1)
//Data required for order list
const OrderList = ref([])
const currentPage = ref(1)
const loading = ref(false)
const error = ref('')
const hasMore = ref(true)
const noMore = computed(() => !hasMore.value)
const disabled = computed(() => loading.value || noMore.value)
//Order loading method
const OrderListLoad = async () => {
  if (disabled.value) return
  if (loading.value) return
  const status = await orderStatusConvert(orderStstusValue.value);
  try {
    loading.value = true
    error.value = ''

    const response = await axios.post(
        `${import.meta.env.VITE_BACKEND_IP}/api/order/get-myOrderList`,
        {type: 0, status: status},
        {
          params: {pageNum: currentPage.value, pageSize: 20},
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
//Refresh order list
const refreshOrderList = async () => {
  OrderList.value = []
  currentPage.value = 1
  hasMore.value = true
  loading.value = false
  error.value = ''
  await nextTick()
  await OrderListLoad()
}
//Order status classification
const orderStstusValue = ref('全部')
const orderStstusOptions = ['全部', '未支付', '待接单', '进行中', '已完结']
watch(orderStstusValue, () => {
  refreshOrderList()
})
const orderStatusConvert = async (ststus) => {
  switch (ststus) {
    case '全部':
      return null
    case '未支付':
      return 0
    case '待接单':
      return 1
    case '进行中':
      return 2
    case '已完结':
      return 3
    default:
      return null
  }
}

//Data required for Buy Business Area
const BuyBusinessAreaDialogVisible = ref(false);
const openBuyBusinessPAreaDialogVisible = () => {
  BuyBusinessAreaDialogVisible.value = true;
  getBusinessList();
}
const closeBuyBusinessAreaDialogVisible = () => {
  BuyBusinessAreaDialogVisible.value = false;
}
const BusinessListLoading = ref(false);
const BusinessList = ref([]);
const getBusinessList = () => {
  BusinessListLoading.value = true;
  axios.get(
      `${import.meta.env.VITE_BACKEND_IP}/api/product/get-list`,
      {
        params: {
          type: 0
        },
        headers: {
          'token': store.state.token
        }
      }
  )
      .then(response => {
        console.log(response.data);
        if (response.data.code === 200) {
          if (response.data.data !== null) {
            BusinessList.value = response.data.data
          } else {
            ElMessage.error(`加载失败，请重试`)
          }
        }
      })
      .catch(error => {
        ElMessage.error(`${error.message}`)
      }).finally(() => {
    BusinessListLoading.value = false;
  })
}
// 计算实际价格（考虑折扣）
const calculatePrice = (item) => {
  return (item.price * item.discount).toFixed(2);
};

// 根据会员ID返回按钮类型（保持你原来的样式逻辑）
const getButtonType = (id) => {
  return id === 1 ? 'info' : id === 2 ? 'success' : 'primary';
};
//Data required for Pay Info
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
  if (business.status === 1) {
    store.state.expert.input.inputIsVisible = false;
    store.state.expert.input.hiddenReasons = "订单已经结束，有疑问请联系客服"
  }
  if (business.status === 0) {
    store.state.expert.input.inputIsVisible = true;
    store.state.expert.input.hiddenReasons = ""
  }
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
//Processing method after the order is completed
onMounted(() => {
  EventBus.on('endOrder1',refreshOrderList);
  OrderListLoad();
});
onBeforeUnmount(() => {
  EventBus.off('endOrder1',refreshOrderList);
});
//Data required for Page Two
const nowOrder = ref(null);
const openCheckOrderDetail = (order) => {
  console.log(order)
  nowOrder.value = order;
  pageNum.value = 2;
}
const closeCheckOrderDetail = () => {
  pageNum.value = 1;
  nowOrder.value = null;
}
</script>

<template>
  <!--  Page 1 -->
  <div v-if="pageNum === 1">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item><strong>专家定制</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <h1>定制服务</h1>
    <el-button type="primary" @click="openBuyBusinessPAreaDialogVisible">创建定制服务</el-button>
    <!-- 我的订单无限滚动列表 -->
    <el-button :loading="loading" type="primary" @click="refreshOrderList">{{ loading ? '加载中' : '刷新' }}</el-button>
    <!--  订单状态分类-->
    <div class="flex flex-col items-start gap-4" style="margin-bottom: 4px;margin-top: 4px">
      <el-segmented v-model="orderStstusValue" :options="orderStstusOptions" size="large"/>
    </div>
    <div class="infinite-list-wrapper" style="overflow: auto">
      <ul
          v-infinite-scroll="OrderListLoad"
          class="list"
          :infinite-scroll-disabled="disabled"
          v-loading="businessLoading"
          infinite-scroll-immediate="false"
          infinite-scroll-distance="100"
      >
        <li v-for="Order in OrderList" :key="Order.orderNo" class="list-item"
            @click="openCheckOrderDetail(Order)">
          <el-tag v-if="Order.status === 0" type="info">未支付</el-tag>
          <el-tag v-if="Order.status  === 1" type="primary">待接单</el-tag>
          <el-tag v-if="Order.status  === 2" type="success">进行中</el-tag>
          <el-tag type="danger" v-if="Order.status  === 3">已完结</el-tag>
          <div>{{ Order.createAt }}</div>
        </li>
        <li v-if="loading" v-loading="loading" class="list-item"></li>
      </ul>
      <p v-if="loading">加载中...</p>
      <p v-if="noMore">没有更多数据了</p>
      <p v-if="error" style="color: red">{{ error }}</p>
    </div>
  </div>
  <!--  Page 2 -->
  <div v-if="pageNum === 2">
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
    <el-form>
      <el-form-item label="订单号:">{{ nowOrder.orderNo }}
      </el-form-item>
      <el-form-item label="订单状态">
        <el-tag v-if="nowOrder.status === 0" type="info">未支付</el-tag>
        <el-tag v-if="nowOrder.status  === 1" type="primary">待接单</el-tag>
        <el-tag v-if="nowOrder.status  === 2" type="success">进行中</el-tag>
        <el-tag v-if="nowOrder.status  === 3" type="danger">已完结</el-tag>
      </el-form-item>
      <el-form-item :label="(nowOrder.status === 0)? '待付款金额' : '付款金额'">
        <div style="color: #3e72f5;font-weight: bold;">{{nowOrder.paymentAmount}}</div>元
      </el-form-item>
      <el-form-item label="下单时间">
        {{nowOrder.createAt}}
      </el-form-item>
    </el-form>
  </div>
  <!-- Buy Business dialog-->
  <el-dialog
      v-model="BuyBusinessAreaDialogVisible"
      title="购买服务"
      width="500"
      align-center
  >
    <el-form ref="buyBusinessForm" v-loading="BuyYiHuLoading || BusinessListLoading" label-width="100px">
      <!-- 动态渲染会员列表 -->
      <div v-for="item in BusinessList" :key="item.id" class="Business-item">
        <h2>
          <el-icon>
            <Star/>
          </el-icon>
          {{ item.name }}
          <el-button
              :type="getButtonType(item.id)"
              @click="BuyBusiness(item.id)"
          >
            立即购买（¥{{ calculatePrice(item) }}）
          </el-button>
        </h2>
        <!-- 如果有折扣，显示折扣信息 -->
        <div v-if="item.discount < 1" class="discount-info">
          原价¥{{ item.price }}，{{ (item.discount * 10) }}折优惠
        </div>
      </div>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="BuyBusinessAreaDialogVisible = false">取消</el-button>
      </div>
    </template>
  </el-dialog>
  <!-- Pay info dialog -->
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
  height: calc(100vh - 221px);
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
    height: calc(100vh - 221px);
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

/* 服务列表 */
.Business-item {
  margin-bottom: 20px;
  padding: 10px;
  border-bottom: 1px solid #eee;
}

.Business-item h1 {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0;
}

.discount-info {
  color: #f56c6c;
  font-size: 12px;
  margin-left: 30px;
  margin-top: 5px;
}
</style>
