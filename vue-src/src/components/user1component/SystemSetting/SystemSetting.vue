<script setup>
import {onMounted, ref} from "vue";
import axios from "axios";
import store from "../../../store";
import PercentageInput from "./PercentageInput.vue";
import {ElMessage, ElMessageBox} from "element-plus";

const pageNum = ref(1)
const ProductData = ref([])
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
const ProductDataDialogVisible = ref(false)
const isEditProductData = ref(false)
const nowEditProductData = ref(null)
const openEditProductDataDialog = (product) => {
  isEditProductData.value = true
  nowEditProductData.value = JSON.parse(JSON.stringify(product));
  ProductDataDialogVisible.value = true
}
const closeEditProductDataDialog = () => {
  ProductDataDialogVisible.value = false
  nowEditProductData.value = null;
}
const editProductDataDialogLoading = ref(false)
const editProductData = async () => {
  if (nowEditProductData.value.name === '') {
    ElMessage.error(`请输入产品名称`)
    return
  }
  if (nowEditProductData.value.price === 0) {
    ElMessage.error(`请输入产品价格`)
    return
  }
  if (nowEditProductData.value.type === 1) {
    try {
      // 确认弹窗
      await ElMessageBox.confirm('请确认VIP时长是否与描述一致？', '编辑确认', {
        confirmButtonText: '确认',
        cancelButtonText: '检查',
        type: 'warning',
      });
    } catch {
      return;
    }
  }
  editProductDataDialogLoading.value = true
  try {
    const response = await axios.put(
        `${import.meta.env.VITE_BACKEND_IP}/api/product/update`,
          nowEditProductData.value,
        {
          headers: {token: store.state.token}
        }
    )
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
      ElMessage.error(`修改失败，请重试(404)`)
    } else {
      ElMessage.error(`修改失败，请重试(未知错误)`)
    }
    editProductDataDialogLoading.value = false
  }catch (err){
    editProductDataDialogLoading.value = false
  }
}
const openAddNewProductData = async (productType) => {
  isEditProductData.value = false
  nowEditProductData.value = {
    name: '',
    type: productType,
    price: 0,
    discount: 1,
    proportion: 0,
    vipTime: 0,
  }
  ProductDataDialogVisible.value = true
}
const closeAddNewProductData = async () => {
  ProductDataDialogVisible.value = false
}
const addNewProductData = async () => {
  if (nowEditProductData.value.name === '') {
    ElMessage.error(`请输入产品名称`)
    return
  }
  if (nowEditProductData.value.price === 0) {
    ElMessage.error(`请输入产品价格`)
    return
  }
  if (nowEditProductData.value.type === 1) {
    try {
      // 确认弹窗
      await ElMessageBox.confirm('请确认VIP时长是否与描述一致？', '添加确认', {
        confirmButtonText: '确认添加',
        cancelButtonText: '检查',
        type: 'warning',
      });
    } catch {
      return;
    }
  }
  editProductDataDialogLoading.value = true
  try {
    const response = await axios.post(
        `${import.meta.env.VITE_BACKEND_IP}/api/product/add`,
        nowEditProductData.value,
        {
          headers: {token: store.state.token}
        }
    )
    if (response.data.code === 200) {
      if (response.data.data === true) {
        ElMessage.success(`添加成功`)
        await closeAddNewProductData()
        ProductData.value = []
        await getProductDataList()
      } else {
        ElMessage.error(`添加失败，请重试`)
      }
    } else if (response.data.code === 404) {
      ElMessage.error(`修改失败，请重试(404)`)
    } else {
      ElMessage.error(`修改失败，请重试(未知错误)`)
    }
    editProductDataDialogLoading.value = false
  }catch (err){
    editProductDataDialogLoading.value = false
  }
}
const deleteProductData = (product) => {
  ElMessageBox.confirm('确定删除该产品吗？', '删除确认', {
    confirmButtonText: '确认删除',
    cancelButtonText: '取消',
    type: 'warning',
    beforeClose: async (action, instance, done) => {
      if (action === 'confirm') {
        try {
          // 显示加载状态
          instance.confirmButtonLoading = true
          instance.confirmButtonText = '删除中...'

          // 发起删除请求
          const { data } = await axios.delete(
              `${import.meta.env.VITE_BACKEND_IP}/api/product/delete/${product.id}`,
              { headers: { token: store.state.token } }
          )

          if (data.code === 200 && data.data) {
            ElMessage.success('删除成功')
            ProductData.value = []
            await getProductDataList()
          } else {
            ElMessage.error(data.msg || '删除失败')
          }
        } catch (err) {
          ElMessage.error(err.response?.data?.msg || '网络请求失败')
        } finally {
          done()
          // 延迟恢复按钮状态避免闪烁
          setTimeout(() => {
            instance.confirmButtonLoading = false
            instance.confirmButtonText = '确认删除'
          }, 200)
        }
      } else {
        done()
        ElMessage.info('已取消删除')
      }
    }
  })
}
</script>

<template>
  <div v-if="pageNum === 1">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item><strong>系统设置</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <br>
    <el-text class="mx-1" size="large">系统会员设置</el-text>
    <el-button type="primary" @click="openAddNewProductData(1)">添加</el-button>
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
              <el-button link type="warning" size="small" @click="deleteProductData(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
    </el-form>
    <el-text class="mx-1" size="large">系统商品设置</el-text>
    <el-button type="primary" @click="openAddNewProductData(0)">添加</el-button>
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
              <el-button link type="warning" size="small" @click="deleteProductData(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
    </el-form>
    <el-dialog :title="isEditProductData? '编辑产品' : '新增产品'" v-model="ProductDataDialogVisible" width="500" align-center>
      <div>
        <el-form label-position="top" v-if="ProductDataDialogVisible">
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
                :min="1"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeEditProductDataDialog">取 消</el-button>
          <el-button type="primary" @click="editProductData" v-if="isEditProductData" :loading="editProductDataDialogLoading" >保 存</el-button>
          <el-button type="primary" @click="addNewProductData" v-if="!isEditProductData" :loading="editProductDataDialogLoading" >添 加</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>

</style>
