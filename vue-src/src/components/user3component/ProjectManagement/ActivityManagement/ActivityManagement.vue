<script setup>

import {ElButton, ElInput, ElMessage} from "element-plus";
import {Plus, Search} from "@element-plus/icons-vue";
import {computed, reactive, ref} from "vue";
import axios from "axios";
import store from "../../../../store";
const pageNum = ref(1)


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
        `${import.meta.env.VITE_BACKEND_IP}/api/activity/queryByCreateId`,
        {
          params: {pageNum: currentPage.value, pageSize: 10},
          headers: {token: store.state.token}
        }
    )
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
  await activityListLoad()
}

//创建新活动
const createNewActivityLoading = ref(false);
const createNewActivity = async () => {
  createNewActivityLoading.value = true;
  try {
    const response = await axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/activity/addActivity`, {},
    {
      headers:{
        token:store.state.token
      }
    });
    console.log(response);
    if (response.data.code === 200 && response.data.data) {
      const id = response.data.data;
      await refreshActivityList();
      await openActivityDetail(id);
      ElMessage.success("创建成功")
    } else {
      ElMessage.error("创建失败")
    }
  } catch (error) {
    console.error('API 请求失败:', error);
  } finally {
    createNewActivityLoading.value = false; // 结束加载状态
  }
}
//活动信息编辑页面所需信息
const nowStep = ref('描述')
const stepOptions = ['描述', '签到', '档案', '新闻稿', '满意度', '7']
const activityForm = reactive({
  activityName: '',
  notice: '',
  staffCount: 0,
  volunteerCount: 0,
  clientCount: 0,
  staffFiles: [],
  volunteerFiles: [],
  clientFiles: [],
  processFiles: [],
  platform: '',
  articleUrl: '',
  newsFiles: []
})
//step 1 data
const activityId = ref(null)
const activityTitle = ref(null)
const activityNoticeContent = ref(null)
//step 2 data
const activityStaffCount = ref(null)
const activityVolunteerCount = ref(null)
const activityServiceObjectCount = ref(null)
//File
const activityFiles = ref([])
const activityNews = ref([])


const openActivityDetail = async (id) => {
  const nowActivity = await getActivityInfo(id)
  if(nowActivity !== 0){
    activityId.value = id;
    activityTitle.value = nowActivity.activity.title;
    activityNoticeContent.value = nowActivity.activity.notice;
    activityStaffCount.value = nowActivity.activity.staffCount;
    activityVolunteerCount.value = nowActivity.activity.volunteerCount;
    activityServiceObjectCount.value = nowActivity.activity.serviceObjectCount;
    activityFiles.value = nowActivity.files;
    activityNews.value = nowActivity.news;
    pageNum.value = 2;
  }
}
const closeActivityDetail = ()=>{
  pageNum.value = 1;
  activityId.value = null;
  activityTitle.value  = null;
  activityNoticeContent.value  = null;
  activityStaffCount.value = null;
  activityVolunteerCount.value  = null;
  activityServiceObjectCount.value  = null;
  activityFiles.value  = null;
  activityNews.value = null;
  nowStep.value = '描述';
}
//查看或编辑活动信息
const ActivityInfoLoading = ref(false);
const getActivityInfo = async (activityId) => {
  ActivityInfoLoading.value = true;
  try {
    const response = await axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/activity/getActivityById`, {
      params: {
        activityId: activityId
      },
      headers: {
        token: store.state.token
      }
    });
    console.log(response);
    if (response.data.code === 200 && response.data.data) {
          return response.data.data;
    } else {
      return 0;
    }
  } catch (error) {
    console.error('API 请求失败:', error);
    return 0;
  } finally {
    ActivityInfoLoading.value = false; // 结束加载状态
  }
};
</script>

<template>
  <div v-if="pageNum===1">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item><strong>活动管理</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <br />
    <el-button type="primary" @click="createNewActivity()" v-loading="createNewActivityLoading">创建新活动</el-button>&nbsp;
    <el-input v-model="input" style="width: auto" placeholder="请输入" @input="searchProjects" />
    <el-button type="primary" @click="searchProjects">
      <el-icon><Search /></el-icon>
    </el-button>
    <br /><br />

    <!-- 活动列表显示无限滚动列表 -->
    <div class="infinite-list-wrapper" style="overflow: auto" v-loading="ActivityInfoLoading">
      <ul
          v-infinite-scroll="activityListLoad"
          class="list"
          :infinite-scroll-disabled="disabled"
          v-loading="businessLoading"
      >
        <li v-for="activity in activityList" :key="activity.id" class="list-item"
            @click="openActivityDetail(activity.id)">
          <div>{{ !activity.title ? "未命名活动": activity.title}}</div>
        </li>
        <li v-if="loading" v-loading="loading" class="list-item"></li>
      </ul>
      <p v-if="loading">加载中...</p>
      <p v-if="noMore">没有更多数据了</p>
      <p v-if="error" style="color: red">{{ error }}</p>
    </div>
  </div>

  <div v-if="pageNum===2">
    <!-- 面包屑导航 -->
    <el-breadcrumb separator="/">
      <el-breadcrumb-item @click="closeActivityDetail"><a>活动管理</a></el-breadcrumb-item>
      <el-breadcrumb-item><strong>活动编辑</strong></el-breadcrumb-item>
    </el-breadcrumb><br>

    <!-- 页面标题 -->
    <el-page-header @back="closeActivityDetail" title="返回">
      <template #content>
        <span class="text-large font-600 mr-3">活动编辑</span>
      </template>
    </el-page-header><br>
    <div class="flex flex-col items-start gap-4">
      <el-segmented v-model="nowStep" :options="stepOptions" size="large" />
    </div>
    <!-- 步骤表单 -->
    <el-form :model="activityForm" label-width="120px">
      <!-- 第一步 -->
      <div v-if="nowStep === '描述'">
        <el-form-item label="活动名称" prop="activityName">
          <el-input v-model="activityTitle" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动通知" prop="notice">
          <el-input
              v-model="activityNoticeContent"
              type="textarea"
              :rows="4"
              placeholder="请输入活动通知内容"
          />
        </el-form-item>
      </div>

      <!-- 第二步 签到信息 -->
      <div v-if="nowStep === '签到'">
        <!-- 工作人员 -->
        <h4 class="form-section-title">工作人员签到</h4>
        <el-form-item label="人数">
          <el-input-number v-model="activityStaffCount" :min="0" />
        </el-form-item>
        <el-form-item label="签到照片">
          <el-upload
              v-model:file-list="activityForm.staffFiles"
              action="#"
              list-type="picture-card"
              multiple
              :auto-upload="false"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <!-- 志愿者 -->
        <h4 class="form-section-title">志愿者签到</h4>
        <el-form-item label="人数">
          <el-input-number v-model="activityVolunteerCount" :min="0" />
        </el-form-item>
        <el-form-item label="签到照片">
          <el-upload
              v-model:file-list="activityForm.volunteerFiles"
              action="#"
              list-type="picture-card"
              multiple
              :auto-upload="false"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <!-- 服务对象 -->
        <h4 class="form-section-title">服务对象签到</h4>
        <el-form-item label="人数">
          <el-input-number v-model="activityServiceObjectCount" :min="0" />
        </el-form-item>
        <el-form-item label="签到照片">
          <el-upload
              v-model:file-list="activityForm.clientFiles"
              action="#"
              list-type="picture-card"
              multiple
              :auto-upload="false"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </div>

      <!-- 第四步 活动过程档案 -->
      <div v-if="nowStep === '档案'">
        <el-form-item label="活动照片">
          <el-upload
              v-model:file-list="activityForm.processFiles"
              action="#"
              list-type="picture-card"
              multiple
              :auto-upload="false"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </div>

      <!-- 第五步 新闻稿 -->
      <div v-if="nowStep === '新闻稿'">
        <el-form-item label="发布平台">
          <el-input v-model="activityForm.platform" placeholder="请输入平台名称" />
        </el-form-item>
        <el-form-item label="文章链接">
          <el-input
              v-model="activityForm.articleUrl"
              type="text"
              placeholder="请输入文章链接"
          />
        </el-form-item>
        <el-form-item label="新闻图片">
          <el-upload
              v-model:file-list="activityForm.newsFiles"
              action="#"
              list-type="picture-card"
              multiple
              :auto-upload="false"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </div>

      <!-- 第六步 -->
      <div v-if="nowStep === '满意度'">
        <!-- 满意度调查和附件上传 -->
      </div>
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
/* 第二页样式 */
.form-section-title {
  margin: 20px 0 15px;
  color: #606266;
  font-size: 16px;
}
</style>