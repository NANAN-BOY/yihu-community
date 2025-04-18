<script setup>

import {ElButton, ElInput, ElMessage, ElMessageBox} from "element-plus";
import {Delete, Link, Plus} from "@element-plus/icons-vue";
import {computed, h, ref} from "vue";
import axios from "axios";
import store from "../../../../store";
import CustomUpload from "./CustomUpload.vue";
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
const stepOptions = ['描述', '签到', '档案', '新闻稿', '满意度', '管理']
//NewData
//step 1 data
const activityId = ref(null)
const activityTitle = ref(null)
const activityNoticeContent = ref(null)
//step 2 data
const activityStaffCount = ref(null)
const activityVolunteerCount = ref(null)
const activityServiceObjectCount = ref(null)
// News Data
const activityNews = ref([]);
//File
const activityFiles = ref([])
//OtherInfo
const activityCreateTime = ref(null)
const activityUpdateTime = ref(null)


const lastUpdateTime = ref(new Date())


// 新闻稿
const addNewsItem = () => {
  activityNews.value.push({
    platform: "",
    link: "",
  });
  updateActivityInfo('news','activityNews', activityNews.value)
};
const removeNewsItem = (index) => {
  activityNews.value.splice(index, 1);
  updateActivityInfo('news','activityNews', activityNews.value)
};
const openLink = (url) => {
  if (url) {
    window.open(url, '_blank')
  } else {
    ElMessage.warning('请先输入有效的链接')
  }
}
const openActivityDetail = async (id) => {
  const nowActivity = await getActivityInfo(id)
  if(nowActivity !== 0){
    //Data
    activityId.value = id;
    activityTitle.value = nowActivity.activity.title;
    activityNoticeContent.value = nowActivity.activity.noticeContent;
    activityStaffCount.value = nowActivity.activity.staffCount;
    activityVolunteerCount.value = nowActivity.activity.volunteerCount;
    activityServiceObjectCount.value = nowActivity.activity.serviceObjectCount;
    activityFiles.value = nowActivity.files;
    activityNews.value = JSON.parse(JSON.stringify(nowActivity.news));
    activityCreateTime.value = nowActivity.activity.createTime;
    activityUpdateTime.value = nowActivity.activity.updateTime;
    pageNum.value = 2;
  }
}
const closeActivityDetail = ()=>{
  pageNum.value = 1;
  //Data
  activityId.value = null;
  activityTitle.value  = null;
  activityNoticeContent.value  = null;
  activityStaffCount.value = null;
  activityVolunteerCount.value  = null;
  activityServiceObjectCount.value  = null;
  activityFiles.value  = null;
  activityNews.value = null;
  activityCreateTime.value = null;
  activityUpdateTime.value = null;
  //step
  nowStep.value = '描述';
  refreshActivityList();
}
//Get activity info
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
const updateActivityInfoLoading = ref(false);
// update activity info
const updateActivityInfo = async (apiParamName, paramValue) => {
  updateActivityInfoLoading.value = true;
  try {
    const requestBody = {
      activityId: activityId.value,
      [apiParamName]: paramValue
    };
    await axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/activity/update`, requestBody
        , {headers: {token: store.state.token}});
  } catch (error) {
    console.error('API 请求失败:', error);
  }
    activityUpdateTime.value = Date.now();
    lastUpdateTime.value = Date.now();
    updateActivityInfoLoading.value = false;
};
const deleteActivityWarning = () => {
  ElMessageBox({
    title: '删除确认',
    message: h('div', null, [
      h('p', null, `确认删除“${!activityTitle.value ? "未命名活动": activityTitle.value}”活动吗？`),
      h('p', { style: 'color: #ff4d4f; margin-top: 8px;' }, '此操作不可撤销')
    ]),
    showCancelButton: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    confirmButtonClass: 'confirm-delete-button',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true
        instance.confirmButtonText = '删除中...'
        axios.delete(`${import.meta.env.VITE_BACKEND_IP}/api/activity/deleteById`,
            {
              headers: {token: store.state.token},
              params: {activityId: activityId.value}
            })
            .then(response => {
              if (response.data.code === 200) {
                ElMessage.success("删除成功")
                closeActivityDetail();
                done()
              } else {
                ElMessage.error("删除失败")
                instance.confirmButtonLoading = false
              }
            })
            .catch(error => {
              console.error('API 请求失败:', error);
            })
      } else {
        done()
      }
    }
  })
}
</script>

<template>
  <div v-if="pageNum===1">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item><strong>活动管理</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <br />
    <el-button :loading="createNewActivityLoading" type="primary" @click="createNewActivity()">创建新活动</el-button>&nbsp;
    <!--    <el-input v-model="input" style="width: auto" placeholder="请输入" @input="searchProjects" />-->
    <!--    <el-button type="primary" @click="">-->
    <!--      <el-icon><Search /></el-icon>-->
    <!--    </el-button>-->
    <br /><br />

    <!-- 活动列表显示无限滚动列表 -->
    <div class="infinite-list-wrapper" style="overflow: auto" v-loading="ActivityInfoLoading">
      <ul
          v-infinite-scroll="activityListLoad"
          class="list"
          :infinite-scroll-disabled="disabled"
          v-loading=""
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
        <span class="text-large font-600 mr-3">
          活动编辑
            {{ updateActivityInfoLoading ?
                '(保存中)' :
                '(最后保存于'+ (
                    ( (new Date() - new Date(lastUpdateTime)) < 60000
                            ? '1分钟前'
                            : ( (new Date() - new Date(lastUpdateTime)) < 3600000
                                    ? Math.floor( (new Date() - new Date(lastUpdateTime)) / 60000 ) + '分钟前'
                                    : Math.floor( (new Date() - new Date(lastUpdateTime)) / 3600000 ) + '小时前'
                            )
                    )
                ) + ')' }}
</span>
      </template>
    </el-page-header><br>
    <div class="flex flex-col items-start gap-4">
      <el-segmented v-model="nowStep" :options="stepOptions" size="large" />
    </div>
    <!-- 步骤表单 -->
    <el-form label-width="120px" @submit.native.prevent="">
      <!-- 第一步 -->
      <div v-if="nowStep === '描述'">
        <el-form-item label="活动名称" prop="activityName">
          <el-input
              v-model="activityTitle"
              placeholder="请输入活动名称"
              @blur="updateActivityInfo('title', activityTitle)"
              @keyup.enter.native.prevent="updateActivityInfo('title', activityTitle)"
          />
        </el-form-item>
        <el-form-item label="活动通知" prop="notice">
          <el-input
              v-model="activityNoticeContent"
              type="textarea"
              :rows="4"
              placeholder="请输入活动通知内容"
              @blur="updateActivityInfo('noticeContent', activityNoticeContent)"
          />
        </el-form-item>
      </div>

      <!-- 第二步 签到信息 -->
      <div v-if="nowStep === '签到'">
        <!-- 工作人员 -->
        <h4 class="form-section-title">工作人员签到</h4>
        <el-form-item label="人数">
          <el-input-number v-model="activityStaffCount"
                           :min="0"
                           @change="updateActivityInfo('staffCount', activityStaffCount)"
          />
        </el-form-item>
        <el-form-item label="签到照片">
          <CustomUpload
              ref="uploadRef"
              v-model="activityFiles"
              :fileType="1"
              :fileTypeName="'图片'"
              :accept-file-type="'image/*'"
              :activityId="activityId"/>
        </el-form-item>

        <!-- 志愿者 -->
        <h4 class="form-section-title">志愿者签到</h4>
        <el-form-item label="人数">
          <el-input-number
              v-model="activityVolunteerCount"
              :min="0"
              @change="updateActivityInfo('volunteerCount', activityVolunteerCount)"
          />
        </el-form-item>
        <el-form-item label="签到照片">
          <CustomUpload
              ref="uploadRef"
              v-model="activityFiles"
              :fileType="2"
              :fileTypeName="'图片'"
              :accept-file-type="'image/*'"
              :activity-id="activityId"
          />
        </el-form-item>

        <!-- 服务对象 -->
        <h4 class="form-section-title">服务对象签到</h4>
        <el-form-item label="人数">
          <el-input-number
              v-model="activityServiceObjectCount"
              :min="0"
              @change="updateActivityInfo('serviceObjectCount', activityServiceObjectCount)"
          />
        </el-form-item>
        <el-form-item label="签到照片">
          <CustomUpload
              ref="uploadRef"
              v-model="activityFiles"
              :fileType="3"
              :fileTypeName="'图片'"
              :activityId="activityId"
          />
        </el-form-item>
      </div>

      <!-- 第四步 活动过程档案 -->
      <div v-if="nowStep === '档案'">
        <el-form-item label="活动照片">
          <CustomUpload
              ref="uploadRef"
              v-model="activityFiles"
              :fileType="4"
              :fileTypeName="'图片'"
              :accept-file-type="'image/*'"
              :activityId="activityId"
          />
        </el-form-item>
      </div>

      <!-- 第五步 新闻稿 -->
      <div v-if="nowStep === '新闻稿'" class="news-container">
        <el-form
            class="news-form"
            label-position="top"
            label-width="120px"
        >
          <el-card
              v-for="(item, index) in activityNews"
              :key="index"
              class="news-card"
              shadow="hover"
          >
            <template #header>
              <div class="card-header">
                <span>新闻稿 #{{ index + 1 }}</span>
                <el-button
                    :icon="Delete"
                    circle
                    size="small"
                    type="danger"
                    @click="removeNewsItem(index)"
                />
              </div>
            </template>

            <el-form-item label="发布平台" prop="platform">
              <el-input
                  v-model="item.platform"
                  clearable
                  placeholder="如：微信公众号、今日头条等"
                  @blur="updateActivityInfo('news',activityNews)"
              />
            </el-form-item>

            <el-form-item label="文章链接" prop="link">
              <el-input
                  v-model="item.link"
                  clearable
                  placeholder="请输入完整的文章URL"
                  @blur="updateActivityInfo('news', activityNews)"
              >
                <template #append>
                  <el-button :icon="Link" @click="openLink(item.link)"/>
                </template>
              </el-input>
            </el-form-item>
          </el-card>

          <div class="form-actions">
            <el-button
                :icon="Plus"
                type="primary"
                @click="addNewsItem"
            >
              添加新闻稿
            </el-button>
          </div>
        </el-form>
      </div>

      <!-- 第六步 -->
      <div v-if="nowStep === '满意度'">
        <!-- 满意度调查和附件上传 -->
        <CustomUpload
            ref="uploadRef"
            v-model="activityFiles"
            :fileType="5"
            :fileTypeName="'压缩包'"
            :accept-file-type="'application/zip,application/x-zip,application/x-zip-compressed'"
            :activityId="activityId"
        />
      </div>

      <div v-if="nowStep === '管理'">
        <br><h4>活动管理</h4>
        <el-button @click="deleteActivityWarning" type="danger">
          移除活动
        </el-button>
        <br><h4>活动信息</h4>
        活动创建时间：{{
          new Date(activityCreateTime).getFullYear() + "年" +
          (new Date(activityCreateTime).getMonth() + 1) + "月" +  // 月份+1
          new Date(activityCreateTime).getDate() + "日" +
          String(new Date(activityCreateTime).getHours()).padStart(2, '0') + ":" +  // 补零
          String(new Date(activityCreateTime).getMinutes()).padStart(2, '0') + ":" +
          String(new Date(activityCreateTime).getSeconds()).padStart(2, '0')
        }}
        <br>
        上次更新时间：{{
          new Date(activityUpdateTime).getFullYear() + "年" +
          (new Date(activityUpdateTime).getMonth() + 1) + "月" +  // 月份+1
          new Date(activityUpdateTime).getDate() + "日" +
          String(new Date(activityUpdateTime).getHours()).padStart(2, '0') + ":" +  // 补零
          String(new Date(activityUpdateTime).getMinutes()).padStart(2, '0') + ":" +
          String(new Date(activityUpdateTime).getSeconds()).padStart(2, '0')
        }}
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
