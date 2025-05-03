<template>
  <!-- 面包屑导航 -->
  <el-breadcrumb separator="/">
    <el-breadcrumb-item><strong @click="$emit('closeActivityDetail')">活动审核</strong></el-breadcrumb-item>
    <el-breadcrumb-item><strong>活动审核</strong></el-breadcrumb-item>
  </el-breadcrumb>
  <br>
  <!-- 页面标题 -->
  <el-page-header @back="$emit('closeActivityDetail')" title="返回">
    <template #content>
      <span class="text-large font-600 mr-3">
        {{ activityTitle + '&nbsp&nbsp所属项目:' + props.nowActivity.projectName }}
        <el-button @click="withdrawSubmissionActivity(activityId)" type="danger" v-if="activityStatus === 1">驳回</el-button>
        <el-button @click="approvedActivity(activityId)" type="success" v-if="activityStatus === 1">审核通过</el-button>
      </span>
    </template>
  </el-page-header>
  <div v-loading="activityInfoLoading" :element-loading-text="loadingText">
    <br>
    <div class="flex flex-col items-start gap-4">
      <el-segmented v-model="nowStep" :options="stepOptions" :size="onMobile ? 'default' : 'large'" />
    </div>
    <br />
    <!-- 步骤表单 -->
    <el-form label-width="120px" @submit.native.prevent="">
      <!-- 第一步 -->
      <div v-if="nowStep === '描述'">
        <el-form-item label="活动名称" prop="activityName">
          <el-input
              v-model="activityTitle"
              placeholder="请输入活动名称"
              :disabled="!activityAllowEdit"
          />
        </el-form-item>
        <el-form-item label="活动通知" prop="notice">
          <el-input
              v-model="activityNoticeContent"
              type="textarea"
              :rows="4"
              placeholder="请输入活动通知内容"
              :disabled="!activityAllowEdit"
          />
        </el-form-item>
        <!-- 评论框 -->
        <div v-if="(activityStatus === 1)||(activityStatus === 2)">
        <el-divider>
          <el-icon><star-filled /></el-icon>
        </el-divider>
        <el-form-item label="审核评论">
          <el-input
              type="textarea"
              v-model="commentMap['描述']"
              :placeholder="(activityStatus !== 1)||(!store.state.user.role === 1)?'无评论':'请输入审核评论'"
              :rows="3"
              :disabled="(activityStatus !== 1)||(!store.state.user.role === 1)"
          />
        </el-form-item>
        </div>
      </div>

      <!-- 第二步 签到信息 -->
      <div v-if="nowStep === '签到'">
        <!-- 工作人员 -->
        <h4 class="form-section-title">工作人员签到</h4>
        <el-form-item label="人数">
          <el-input-number v-model="activityStaffCount" :min="0" :disabled="!activityAllowEdit" />
        </el-form-item>
        <el-form-item label="签到照片">
          <CustomUpload
              ref="uploadRef"
              v-model="activityFiles"
              :fileType="1"
              :fileTypeName="'图片'"
              :accept-file-type="'image/*'"
              :activityId="activityId"
              :allow-edit="activityAllowEdit"
          />
        </el-form-item>

        <!-- 志愿者 -->
        <h4 class="form-section-title">志愿者签到</h4>
        <el-form-item label="人数">
          <el-input-number
              v-model="activityVolunteerCount"
              :min="0"
              :disabled="!activityAllowEdit"
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
              :allow-edit="activityAllowEdit"
          />
        </el-form-item>

        <!-- 服务对象 -->
        <h4 class="form-section-title">服务对象签到</h4>
        <el-form-item label="人数">
          <el-input-number
              v-model="activityServiceObjectCount"
              :min="0"
              :disabled="!activityAllowEdit"
          />
        </el-form-item>
        <el-form-item label="签到照片">
          <CustomUpload
              ref="uploadRef"
              v-model="activityFiles"
              :fileType="3"
              :fileTypeName="'图片'"
              :activityId="activityId"
              :allow-edit="activityAllowEdit"
          />
        </el-form-item>
        <!-- 评论框 -->
        <div v-if="(activityStatus === 1)||(activityStatus === 2)">
        <el-divider>
          <el-icon><star-filled /></el-icon>
        </el-divider>
        <el-form-item label="审核评论">
          <el-input
              type="textarea"
              v-model="commentMap['签到']"
              :placeholder="(activityStatus !== 1)||(!store.state.user.role === 1)?'无评论':'请输入审核评论'"
              :rows="3"
              :disabled="(activityStatus !== 1)||(!store.state.user.role === 1)"
          />
        </el-form-item>
        </div>
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
              :allow-edit="activityAllowEdit"
          />
        </el-form-item>
        <!-- 评论框 -->
        <div v-if="(activityStatus === 1)||(activityStatus === 2)">
        <el-divider>
          <el-icon><star-filled /></el-icon>
        </el-divider>
        <el-form-item label="审核评论">
          <el-input
              type="textarea"
              v-model="commentMap['档案']"
              :placeholder="(activityStatus !== 1)||(!store.state.user.role === 1)?'无评论':'请输入审核评论'"
              :rows="3"
              :disabled="(activityStatus !== 1)||(!store.state.user.role === 1)"
          />
        </el-form-item>
        </div>
      </div>

      <!-- 第五步 新闻稿 -->
      <div v-if="nowStep === '新闻稿'" class="news-container">
        <el-form label-position="top">
          <!-- 添加滚动容器 -->
          <div class="news-scroll-container">
            <el-card class="news-card" shadow="hover">
              <div class="card-header">
                <span>图片新闻稿</span>
                <CustomUpload
                    ref="uploadRef"
                    v-model="activityFiles"
                    :fileType="5"
                    :fileTypeName="'图片'"
                    :accept-file-type="'image/*'"
                    :activityId="activityId"
                    :allow-edit="activityAllowEdit"
                />
              </div>
            </el-card>
            <el-card
                v-for="(item, index) in activityNews"
                :key="index"
                class="news-card"
                shadow="hover"
            >
              <template #header>
                <div class="card-header">
                  <span>链接新闻稿 #{{ index + 1 }}</span>
                  <el-button :icon="Delete" circle size="small" type="danger" />
                </div>
              </template>

              <el-form-item label="发布平台" prop="platform">
                <el-input
                    v-model="item.platform"
                    clearable
                    placeholder="如：微信公众号、今日头条等"
                    :disabled="!activityAllowEdit"
                />
              </el-form-item>

              <el-form-item label="文章链接" prop="link">
                <el-input
                    v-model="item.link"
                    clearable
                    placeholder="请输入完整的文章URL"
                    :disabled="!activityAllowEdit"
                >
                  <template #append>
                    <el-button :icon="Link" @click="openLink(item.link)" />
                  </template>
                </el-input>
              </el-form-item>
            </el-card>
          </div>
        </el-form>
        <!-- 评论框 -->
        <div v-if="(activityStatus === 1)||(activityStatus === 2)">
        <el-divider>
          <el-icon><star-filled /></el-icon>
        </el-divider>
        <el-form-item label="审核评论">
          <el-input
              type="textarea"
              v-model="commentMap['新闻稿']"
              :placeholder="(activityStatus !== 1)||(!store.state.user.role === 1)?'无评论':'请输入审核评论'"
              :rows="3"
              :disabled="(activityStatus !== 1)||(!store.state.user.role === 1)"
          />
        </el-form-item>
        </div>
      </div>

      <!-- 第六步 -->
      <div v-if="nowStep === '满意度'">
        <SatisfactionLevel :questionnaire_id="activityQuestionnaireId" />
        <!-- 评论框 -->
        <div v-if="(activityStatus === 1)||(activityStatus === 2)">
        <el-divider>
          <el-icon><star-filled /></el-icon>
        </el-divider>
        <el-form-item label="审核评论">
          <el-input
              type="textarea"
              v-model="commentMap['满意度']"
              :placeholder="(activityStatus !== 1)||(!store.state.user.role === 1)?'无评论':'请输入审核评论'"
              :rows="3"
              :disabled="(activityStatus !== 1)||(!store.state.user.role === 1)"
          />
        </el-form-item>
        </div>
      </div>

      <div v-if="nowStep === '附件'">
        <!-- 附件上传 -->
        <CustomUpload
            ref="uploadRef"
            v-model="activityFiles"
            :fileType="6"
            :fileTypeName="'压缩包'"
            :accept-file-type="'application/zip,application/x-zip,application/x-zip-compressed'"
            :activityId="activityId"
            :allow-edit="activityAllowEdit"
        />
        <!-- 评论框 -->
        <div v-if="(activityStatus === 1)||(activityStatus === 2)">
        <el-divider>
          <el-icon><star-filled /></el-icon>
        </el-divider>
        <el-form-item label="审核评论">
          <el-input
              type="textarea"
              v-model="commentMap['附件']"
              :placeholder="(activityStatus !== 1)||(!store.state.user.role === 1)?'无评论':'请输入审核评论'"
              :rows="3"
              :disabled="(activityStatus !== 1)||(!store.state.user.role === 1)"
          />
        </el-form-item>
        </div>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import {Delete, Link, StarFilled} from "@element-plus/icons-vue";
import SatisfactionLevel from "../../user3component/ProjectManagement/ActivityManagement/SatisfactionLevel.vue";
import {ElButton, ElInput, ElMessage} from "element-plus";
import CustomUpload from "../../user3component/ProjectManagement/ActivityManagement/CustomUpload.vue";
import store from "../../../store";
import {onMounted, ref} from "vue";
import axios from "axios";
const onMobile = ref(typeof window !== 'undefined' && window.matchMedia('(max-width: 768px)').matches)
if (typeof window !== 'undefined') {window.matchMedia('(max-width: 768px)').addListener(e => {onMobile.value = e.matches})}

const props = defineProps({
  nowActivity: {
    type: Object,
    required: true,
    default: () => {
      return {
        id: 0,
      }
    }
  }
})
const emit = defineEmits(['closeActivityDetail'])
//活动信息编辑页面所需信息
const nowStep = ref('描述')
const stepOptions = ['描述', '签到', '档案', '新闻稿', '满意度', '附件']
//step 1 data
const activityId = ref(null)
const activityTitle = ref(props.nowActivity.title)
const activityNoticeContent = ref(null)
//step 2 data
const activityStaffCount = ref(null)
const activityVolunteerCount = ref(null)
const activityServiceObjectCount = ref(null)
const activityQuestionnaireId = ref(null)
// News Data
const activityNews = ref([]);
//File
const activityFiles = ref([])
//OtherInfo
const activityCreateTime = ref(null)
const activityUpdateTime = ref(null)
const activityStatus = ref(null)
const activityAllowEdit = ref(false)

const commentMap = ref({
  描述: '',
  签到: '',
  档案: '',
  新闻稿: '',
  满意度: '',
  附件: ''
});
onMounted(
    async () => {
      console.log(props.nowActivity),
      await openActivityDetail(props.nowActivity.id)
    }
)
const activityInfoLoading  = ref(false)
const openActivityDetail = async (id) => {
  activityInfoLoading.value=true
  const nowActivity = await getActivityInfo(id)
  console.log(nowActivity)
  if (nowActivity !== 0) {
    //Data
    activityId.value = id;
    activityTitle.value = nowActivity.activity.title;
    activityNoticeContent.value = nowActivity.activity.noticeContent;
    activityStaffCount.value = nowActivity.activity.staffCount;
    activityStatus.value = nowActivity.activity.status;
    activityVolunteerCount.value = nowActivity.activity.volunteerCount;
    activityServiceObjectCount.value = nowActivity.activity.serviceObjectCount;
    activityFiles.value = nowActivity.files;
    activityQuestionnaireId.value = nowActivity.activity.questionnaireId;
    activityNews.value = nowActivity.news;
    activityCreateTime.value = nowActivity.activity.createTime;
    activityUpdateTime.value = nowActivity.activity.updateTime;
    if (nowActivity.activity.rejectReason)
        commentMap.value = JSON.parse(nowActivity.activity.rejectReason)
  }
  if(nowActivity.activity.status===1){
    activityAllowEdit.value = false;
  }
  activityInfoLoading.value=false
}
//Get activity info
const ActivityInfoLoading = ref(false);
const getActivityInfo = async (activityId) => {
  ActivityInfoLoading.value = true;
  try {
    const [response] = await Promise.all([axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/activity/getActivityById`, {
      params: {
        activityId: activityId
      },
      headers: {
        token: store.state.token
      }
    })]);
    if (response.data.code === 200 && response.data.data) {
      console.log(response.data.data);
      return response.data.data;
    } else {
      ElMessage.error('获取活动信息失败');
      return 0;
    }
  } catch (error) {
    console.error('API 请求失败:', error);
    return 0;
  } finally {
    ActivityInfoLoading.value = false; // 结束加载状态
  }
};

const openLink = (url) => {
  if (url) {
    window.open(url, '_blank')
  } else {
    ElMessage.warning('请先输入有效的链接')
  }
}
const approvedActivity = async (activityId) => {
  try {
    const response = await axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/activity/approved`, {},
        {
      params: {
        activityId: activityId
      },
      headers: {
        token: store.state.token
      }
    })
    if (response.data.code === 200) {
      if (response.data.data) {
        ElMessage.success("活动审核通过")
        //提交事件关闭页面
        emit('closeActivityDetail')
      } else {
        ElMessage.error(response.data.data)
      }
    } else {
      ElMessage.error("请求失败")
    }
  }catch (error){
    ElMessage.error("请求失败")
  }
}
const withdrawSubmissionActivity = async (activityId) => {
  // —— 校验：commentMap 里至少有一项非空
  const hasOne = Object.values(commentMap.value).some(val => val.trim() !== '')
  if (!hasOne) {
    ElMessage.error('请至少填写一项备注！')
    return
  }

  try {
    // 整个 commentMap 串成字符串发给后端
    const payload = {
      activityId,
      reason: JSON.stringify(commentMap.value)
    }
    const response = await axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/activity/withdrawSubmission`, {},
        {
          params: payload,
          headers: {
            token: store.state.token
          }
        })
    console.log(response.data)
    if (response.data.code === 200) {
      if (response.data.data) {
        ElMessage.success("活动驳回成功！")
        //提交事件关闭页面
        emit('closeActivityDetail')
      } else {
        ElMessage.error(response.data.data)
      }
    } else {
      ElMessage.error("请求失败")
    }
  }catch (error){
    ElMessage.error("请求失败")
  }
}
</script>