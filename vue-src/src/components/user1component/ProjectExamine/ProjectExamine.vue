<script setup>


//Data required for activity list
import {computed, nextTick, onMounted, ref} from "vue";
import axios from "axios";
import store from "../../../store";

const activityList = ref([])
const currentPage = ref(1)
const loading = ref(false)
const error = ref('')
const hasMore = ref(true)
const noMore = computed(() => !hasMore.value)
const disabled = computed(() => loading.value || noMore.value)
//activity loading method
const activityListLoad = async () => {
  if (disabled.value) return
  if (loading.value) return
  try {
    loading.value = true
    error.value = ''

    const response = await axios.get(
        `${import.meta.env.VITE_BACKEND_IP}/api/project/queryByCreateId`,
        {
          params: {pageNum: currentPage.value, pageSize: 20},
          headers: {token: store.state.token}
        }
    )
    console.log(response.data)
    if (response.data.code === 200) {
      // success
      activityList.value = [...activityList.value, ...response.data.data.list]
      hasMore.value = response.data.data.hasNextPage
      currentPage.value++
    } else if (response.data.code === 404) {
      // activity not found , list is empty
      hasMore.value = false

      if (currentPage.value === 1) activityList.value = []
    } else {
      // activity error
      error.value = response.data.msg || '请求出现异常'
    }

  } catch (err) {
    // Network error
    error.value = '数据加载失败，请稍后再试'
  } finally {
    loading.value = false
  }
}
//Refresh activity list
const refreshActivityList = async () => {
  activityList.value = []
  currentPage.value = 1
  hasMore.value = true
  loading.value = false
  error.value = ''
  await nextTick()
  await activityListLoad()
}
onMounted(() => {
  activityListLoad()
})
</script>

<template>
  <div class="infinite-list-wrapper" style="overflow: auto">
    <ul
        v-infinite-scroll="activityListLoad"
        class="list"
        :infinite-scroll-disabled="disabled"
        infinite-scroll-immediate="false"
        infinite-scroll-distance="100"
    >
      <li v-for="activity in activityList" :key="activity.activityId" class="list-item"
          @click="">
        <el-tag v-if="activity.status === 0" type="info">未支付</el-tag>
        <el-tag v-if="activity.status  === 1" type="primary">待接单</el-tag>
        <el-tag v-if="activity.status  === 2" type="success">进行中</el-tag>
        <el-tag type="danger" v-if="activity.status  === 3">已完结</el-tag>
        <div>{{ activity.createAt }}</div>
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

</style>