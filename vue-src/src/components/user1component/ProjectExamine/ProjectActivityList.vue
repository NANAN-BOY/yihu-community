<script setup>

import {computed, onMounted, ref} from "vue";
import axios from "axios";
import store from "../../../store";
import {f} from "vue-verify-slider/dist/vue-verify-slider.common";

const props = defineProps({
  projectId:{
    type: Number,
    required: true
  },
  projectName:{
    type: String,
    required: true
  },
});
const emits = defineEmits(['openActivityDetail'])
//Data required for activity list
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
        `${import.meta.env.VITE_BACKEND_IP}/api/activity/getActivityByProjectId`,
        {
          params: {
            projectId: props.projectId,
            pageNum: currentPage.value,
            pageSize: 10
          },
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
      // Order not found , list is empty
      hasMore.value = false

      if (currentPage.value === 1) activityList.value = []
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
const refreshActivityList = async () => {
  currentPage.value = 1
  activityList.value = []
  hasMore.value = true
  error.value = ''
  loading.value = false
  await activityListLoad()
}
onMounted(() => {
  activityListLoad()
})
const openActivityDetail = async (activity) => {
  emits('openActivityDetail', activity)
}

</script>

<template>
  <!-- 活动列表显示无限滚动列表 -->
  <div class="infinite-list-wrapper" style="overflow: auto" >
    <ul
        v-infinite-scroll="activityListLoad"
        class="list"
        :infinite-scroll-disabled="disabled"
        v-loading="loading"
    >
      <li v-for="activity in activityList" :key="activity.id" class="list-item"
          @click="openActivityDetail(activity)">
        <div>
          <el-tag v-if="activity.status === 0" type="info">未提交</el-tag>
          <el-tag v-if="activity.status === 1" type="primary">审核中</el-tag>
          <el-tag v-if="activity.status === 2" type="danger">已驳回</el-tag>
          <el-tag v-if="activity.status === 3" type="success">已通过</el-tag>
          {{ !activity.title ? "未命名项目": activity.title}}
        </div>
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
  height: calc(100vh - 224px);
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
  min-height: 50px;
  border-radius: 10px;
  padding: 10px;
  display: flex;
  align-items: center;
  background: #f5f5f5;
  color: #000000;
  margin-bottom: 5px;
  justify-content: space-between;
}
@media (max-width: 768px) {
  .infinite-list-wrapper {
    height: calc(100vh - 222px);
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
    margin-top: 5px;
  }
}
</style>