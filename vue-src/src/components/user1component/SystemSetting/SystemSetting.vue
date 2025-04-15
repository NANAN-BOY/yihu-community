<script setup>
import {onMounted, ref} from "vue";
import axios from "axios";
import store from "../../../store";

const pageNum = ref(1)
const ProductData = ref([
])
const ProductDataLoading= ref(false)
const ProductDataError= ref('')
const getProductDataList = async () => {
  try {
    ProductDataLoading.value = true
    ProductDataError.value = ''

    const response = await axios.get(
        `${import.meta.env.VITE_BACKEND_IP}/api/product/get`,
        {
          headers: {token: store.state.token}
        }
    )
    console.log(response.data)
    if (response.data.code === 200) {
      ProductData.value = response.data.data
    } else if (response.data.code === 404) {
    } else {
      // Other error
      ProductDataError.value = response.data.msg || '请求出现异常'
    }

  } catch (err) {
    // Network error
    ProductDataError.value = '数据加载失败，请稍后再试'
  } finally {
    ProductDataLoading.value = false
  }
}
onMounted(
    getProductDataList
)
</script>

<template>
  <div v-if="pageNum === 1">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item><strong>系统设置</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <br>
    <el-text class="mx-1" size="large">系统商品设置</el-text>
    <el-form v-loading="ProductDataLoading">
      <el-form-item>
        <el-table :data="ProductData" style="width: 100%">
          <el-table-column prop="name" label="名称" width="180" />
          <el-table-column prop="price" label="售卖价格" width="180" />
          <el-table-column prop="proportion" label="提成比例" />
          <el-table-column fixed="right" label="操作" min-width="120">
            <template #default>
              <el-button link type="primary" size="small" @click="handleClick">
                编辑
              </el-button>
              <el-button link type="warning" size="small" @click="handleClick">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>

</style>