<script setup>


import axios from "axios";
import store from "../../../../store";
import {ref} from "vue";
import {ElMessage} from "element-plus";

const props = defineProps({
  activityId:{
    type: Number,
    required: true
  }
});

const releaseQuestionnaireLoading = ref(false);
const releaseQuestionnaire = async () => {
  releaseQuestionnaireLoading.value = true;
  console.log("发布问卷");
  try {
    const response = await axios.put(`${import.meta.env.VITE_BACKEND_IP}/api/questionnaire/release`, {},
        {
      params: {
        activityId: props.activityId
      },
      headers: {
        token: store.state.token
      }
    });
    console.log(response.data);
    if (response.data.code === 200 && response.data.data) {
      ElMessage.success("发布成功")
    } else {
      ElMessage.success("发布失败，")
    }
  } catch (error) {
    console.error('API 请求失败:', error);
    ElMessage.success("发布失败")
  } finally {
    releaseQuestionnaireLoading.value = false; // 结束加载状态
  }
}

const analysisLoading = ref(false);
const analysisResult = async () => {
  analysisLoading.value = true;
  //等待3秒
  setTimeout(async () => {
    analysisLoading.value = false;
  }, 3000);
}
</script>

<template>
  <div>
    <div>
      <el-button type="primary" @click="releaseQuestionnaire">发布问卷</el-button>
    </div>
  </div>
  <!--  分析结果-->
  <div v-loading="analysisLoading"
       class="analysis-result"
       element-loading-text="分析中...(这可能需要一点时间)"
  >
    <el-button
        type="primary"
        @click="analysisResult"
    >查看问卷分析结果
    </el-button>

  </div>
</template>

<style scoped>
.analysis-result {
  min-height: 100px;
  margin: 10px;
  background-color: #f1f1f1;
  border-radius: 10px;
  padding: 10px;
}
</style>
