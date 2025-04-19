<script setup>
import {onMounted, ref} from "vue";
import axios from "axios";
import store from "../../../store";
import PercentageInput from "./PercentageInput.vue";
import {ElMessage} from "element-plus";

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
const editProductDataDialogVisible = ref(false)
const nowEditProductData = ref(null)
const openEditProductDataDialog = (product) => {
  console.log(product)
  nowEditProductData.value = JSON.parse(JSON.stringify(product));
  editProductDataDialogVisible.value = true
}
const closeEditProductDataDialog = () => {
  editProductDataDialogVisible.value = false
  nowEditProductData.value = null;
}
const editProductDataDialogLoading = ref(false)
const editProductData = async () => {
  console.log(nowEditProductData)
  editProductDataDialogLoading.value = true
  try {
    const response = await axios.put(
        `${import.meta.env.VITE_BACKEND_IP}/api/product/update`,
          nowEditProductData.value,
        {
          headers: {token: store.state.token}
        }
    )
    console.log(response.data)
    if (response.data.code === 200) {
      if (response.data.data === true) {
        ElMessage.success(`修改成功`)
        closeEditProductDataDialog()
        ProductData.value = []
        await getProductDataList()
      } else {
        ElMessage.error(`修改失败，请重试`)
      }
    } else if (response.data.code === 404) {
    } else {
    }
    editProductDataDialogLoading.value = false
  }catch (err){
    editProductDataDialogLoading.value = false
  }
}
</script>

<template>
  <div v-if="pageNum === 1">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item><strong>系统设置</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <br>
    <el-text class="mx-1" size="large">系统会员设置</el-text>
    <el-form v-loading="ProductDataLoading">
      <el-form-item>
        <el-table :data="ProductData.filter(item => item.type === 1)" style="width: 100%">
          <el-table-column prop="name" label="名称" width="130" />
          <el-table-column prop="price" label="售卖价格" width="100">
            <template #default="{row}">
              {{ row.price.toFixed(2) }}元
            </template>
          </el-table-column>
          <el-table-column prop="discount" label="折扣" width="80">
            <template #default="{row}">
              {{ (row.discount * 100).toFixed(0) }}%
            </template>
          </el-table-column>
          <el-table-column prop="vipTime" label="授予月份数" width="100">
            <template #default="{row}">
              {{ row.vipTime }}个月
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" min-width="120">
            <template #default="{row}">
              <el-button link type="primary" size="small" @click="openEditProductDataDialog(row)">
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
    <el-text class="mx-1" size="large">系统商品设置</el-text>
    <el-form v-loading="ProductDataLoading">
      <el-form-item>
        <el-table :data="ProductData.filter(item => item.type === 0)" style="width: 100%">
          <el-table-column prop="name" label="名称" width="130" />
          <el-table-column prop="price" label="售卖价格" width="100">
            <template #default="{row}">
              {{ row.price.toFixed(2) }}元
            </template>
          </el-table-column>
          <el-table-column prop="discount" label="折扣" width="80">
            <template #default="{row}">
              {{ (row.discount * 100).toFixed(0) }}%
            </template>
          </el-table-column>
          <el-table-column prop="proportion" label="提成比例" width="80">
          <template #default="{row}">
            {{ (row.proportion * 100).toFixed(0) }}%
          </template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" min-width="120">
            <template #default="{row}">
              <el-button link type="primary" size="small" @click="openEditProductDataDialog(row)">
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
    <el-dialog title="编辑产品" v-model="editProductDataDialogVisible" width="500" align-center>
      <div>
        <el-form label-position="top" v-if="editProductDataDialogVisible">
          <el-form-item label="产品名称" required>
            <el-input v-model="nowEditProductData.name" />
          </el-form-item>
          <el-form-item
              label="价格(元)"
              v-if="nowEditProductData.type === 1 || nowEditProductData.type === 0"
          >
            <el-input-number
                v-model="nowEditProductData.price"
                :min="0"
                :precision="2"
            />
          </el-form-item>

          <el-form-item
              label="折扣"
              v-if="nowEditProductData.type === 1 || nowEditProductData.type === 0"
          >
            <PercentageInput
                v-model="nowEditProductData.discount"
                :min="0"
                :max="1"
                :step="0.01"
            />
          </el-form-item>

          <el-form-item
              label="提成比例"
              v-if="nowEditProductData.type === 0"
          >
            <PercentageInput
                v-model="nowEditProductData.proportion"
                :min="0"
                :max="1"
                :step="0.01"
            />
          </el-form-item>

          <el-form-item
              label="VIP时长(月）"
              v-if="nowEditProductData.type === 1"
          >
            <el-input-number
                v-model="nowEditProductData.vipTime"
                :min="0"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeEditProductDataDialog">取 消</el-button>
          <el-button type="primary" @click="editProductData" :loading="editProductDataDialogLoading" >确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>

</style>