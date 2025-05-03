<script setup>
//Data required for activity list
import {computed, nextTick, onMounted, ref, watch} from "vue";
import axios from "axios";
import store from "../../../store";
import ProjectActivityList from "./ProjectActivityList.vue";
import {ElButton, ElMessage, ElMessageBox} from "element-plus";
import ActivityDetail from "./ActivityDetail.vue";

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
const NowActivity = ref(null)
const openActivityDetail = async (activity) => {
  console.log(activity)
  NowActivity.value = activity
  pageNum.value = 3
}
const closeActivityDetail = () => {
  pageNum.value = 2
  NowActivity.value = null
}
//驳回项目
const withdrawSubmission = async (project) => {
    await ElMessageBox.prompt(
        '请输入驳回备注',
        '驳回项目',
        {
          confirmButtonText: '提交',
          cancelButtonText: '取消',
          inputPattern: /.+/,
          inputErrorMessage: '驳回备注不能为空',
          showCancelButton: true,
          // 在这里拦截关闭：只有在 done() 被调用后才会真正关闭弹窗
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              // 开启按钮 loading，并修改文案
              instance.confirmButtonLoading = true
              instance.confirmButtonText = '提交中...'
              // 取到用户输入
              const reason = (instance.inputValue || '').trim()
              // 调用后端接口
              axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/project/withdrawSubmission`, {},
                  {
                    params: {
                      projectId: project.id,
                      reason: reason,
                    },
                    headers: {
                      token: store.state.token,
                    },
                  })
                  .then((response) => {
                    console.log(response)
                    if (response.data.code === 200) {
                      instance.confirmButtonLoading = false
                      instance.confirmButtonText = '提交'
                      ElMessage.success('成功驳回')
                      closeActivityDetail()
                      closeProjectDetail()
                      refreshProjectList()
                      done()
                    } else if (response.data.code === 400) {
                      instance.confirmButtonLoading = false
                      instance.confirmButtonText = '提交'
                      ElMessage.error('提交失败，请重试')
                      done()
                    }
                  })
                  .catch((err) => {
                    console.error(err)
                    // 接口失败，先重置按钮
                    instance.confirmButtonLoading = false
                    instance.confirmButtonText = '提交'
                    ElMessage.error('提交失败，请重试')
                  })
            } else {
              done()
            }
          }
        }
    )
}
</script>

<template>
  <div v-if="pageNum === 1">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item><strong @click="">项目审核</strong></el-breadcrumb-item>
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
          </div>
        </li>
        <li v-if="loading" v-loading="loading" class="list-item"></li>
      </ul>
      <p v-if="loading">加载中...</p>
      <p v-if="noMore">没有更多数据了</p>
      <p v-if="error" style="color: red">{{ error }}</p>
    </div>
  </div>
  <div v-if="pageNum === 2">
    <!-- 面包屑导航 -->
    <el-breadcrumb separator="/">
      <el-breadcrumb-item><strong @click="">项目审核</strong></el-breadcrumb-item>
      <el-breadcrumb-item><strong>{{NowProject.name}}</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <br>
    <!-- 页面标题 -->
    <el-page-header @back="closeProjectDetail" title="返回">
      <template #content>
      <span class="text-large font-600 mr-3">
        {{ NowProject.name }}
        <el-button @click="withdrawSubmission(NowProject)" type="danger" v-if="NowProject.status === 1">驳回项目</el-button>
        <el-button @click="" type="success" v-if="NowProject.status === 1">审核通过</el-button>
      </span>
      </template>
    </el-page-header><br>
    <ProjectActivityList
        :project-id="NowProject.id"
        :project-name="NowProject.name"
        @openActivityDetail="openActivityDetail"
    />
  </div>
    <div v-if="pageNum === 3">
      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item><strong @click="">项目审核</strong></el-breadcrumb-item>
        <el-breadcrumb-item><strong>{{NowProject.name}}</strong></el-breadcrumb-item>
        <el-breadcrumb-item><strong>{{NowActivity.title}}</strong></el-breadcrumb-item>
      </el-breadcrumb>
      <br>
      <!-- 页面标题 -->
      <el-page-header @back="closeActivityDetail" title="返回">
        <template #content>
      <span class="text-large font-600 mr-3">
        {{NowActivity.title}}
        <el-button @click="withdrawSubmission(NowProject)" type="danger" v-if="NowProject.status === 1">驳回项目</el-button>
        <el-button @click="" type="success" v-if="NowProject.status === 1">审核通过</el-button>
      </span>
        </template>
      </el-page-header><br>
      <ActivityDetail
          :nowActivity="NowActivity"
          :topIsVisible="false"
      />
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