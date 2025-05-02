<script setup>
//Data required for activity list
import {computed, nextTick, onMounted, ref, watch} from "vue";
import axios from "axios";
import store from "../../../store";

const pageNum = ref(1)
const ProjectList = ref([])
const currentPage = ref(1)
const loading = ref(false)
const error = ref('')
const hasMore = ref(true)
const noMore = computed(() => !hasMore.value)
const disabled = computed(() => loading.value || noMore.value)
//project loading method
const projectListLoad = async () => {
  const status = await ProjectStatusConvert(ProjectStatusValue.value)
  if (disabled.value) return
  if (loading.value) return
  try {
    loading.value = true
    error.value = ''

    const response = await axios.get(
        `${import.meta.env.VITE_BACKEND_IP}/api/project/queryByCreateId`,
        {
          params: {
            status: status,
            pageNum: currentPage.value,
            pageSize: 20
          },
          headers: {token: store.state.token}
        }
    )
    console.log(response.data)
    if (response.data.code === 200) {
      // success
      ProjectList.value = [...ProjectList.value, ...response.data.data.list]
      hasMore.value = response.data.data.hasNextPage
      currentPage.value++
    } else if (response.data.code === 404) {
      // Project not found , list is empty
      hasMore.value = false

      if (currentPage.value === 1) ProjectList.value = []
    } else {
      // Project error
      error.value = response.data.msg || '请求出现异常'
    }

  } catch (err) {
    // Network error
    error.value = '数据加载失败，请稍后再试'
  } finally {
    loading.value = false
  }
}
//Refresh Project list
const refreshProjectList = async () => {
  ProjectList.value = []
  currentPage.value = 1
  hasMore.value = true
  loading.value = false
  error.value = ''
  await nextTick()
  await projectListLoad()
}
onMounted(() => {
  projectListLoad()
})
//Project status classification
const ProjectStatusValue = ref('未审核')
const ProjectStatusOptions = ['未审核','已驳回','已通过']
watch(ProjectStatusValue, () => {
  refreshProjectList()
})
const ProjectStatusConvert = async (status) => {
  switch (status) {
    case '未审核':
      return 1
    case '已驳回':
      return 2
    case '已通过':
      return 3
    default:
      return null
  }
}
const NowProject = ref(null)
const openProjectDetail = async (Project) => {
  NowProject.value = Project
  console.log(Project)
  pageNum.value = 2
}
const closeProjectDetail = () => {
  pageNum.value = 1
  NowProject.value = null
  refreshProjectList()
}
</script>

<template>
  <div v-if="pageNum === 1">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item><strong @click="">活动审核</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <div class="flex flex-col items-start gap-4" style="margin-bottom: 4px;margin-top: 4px">
      <el-segmented v-model="ProjectStatusValue" :options="ProjectStatusOptions" size="large"/>
    </div>
    <div class="infinite-list-wrapper" style="overflow: auto">
      <ul
          v-infinite-scroll="projectListLoad"
          class="list"
          :infinite-scroll-disabled="disabled"
          infinite-scroll-immediate="false"
          infinite-scroll-distance="100"
      >
        <li v-for="Project in ProjectList" :key="Project.activityId" class="list-item"
            @click="openProjectDetail(Project)">
          <div>
            {{ Project.name }}&nbsp;&nbsp;
            <a style="color: #8f8f8f">所属项目：{{Project.projectName}}</a>
          </div>
        </li>
        <li v-if="loading" v-loading="loading" class="list-item"></li>
      </ul>
      <p v-if="loading">加载中...</p>
      <p v-if="noMore">没有更多数据了</p>
      <p v-if="error" style="color: red">{{ error }}</p>
    </div>
  </div>
</template>

<style scoped>
.infinite-list-wrapper {
  height: calc(100vh - 212px);
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
    height: calc(100vh - 212px);
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