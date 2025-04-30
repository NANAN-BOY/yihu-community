<template>
  <div v-loading="analysisLoading"
       class="analysis-result"
       element-loading-text="分析中...(这可能需要一点时间)">
    <el-button
        type="primary"
        @click="analysisResult"
    >{{ analysisVisible?  '刷新结果' : '查看问卷分析结果'}}
    </el-button>
    <div v-if="!analysisLoading" class="outlineData">
      <MainAnalysisList
          v-if="analysisVisible"
          :questionList="questionList"
          :questionnaire="questionnaire"
          :questionnaire_id="props.questionnaire_id"
      />
    </div>
    <el-divider></el-divider>
    <div class="questionList"></div>
  </div>
</template>

<script setup>
import MainAnalysisList from "./MainAnalysisList.vue";
import {ref} from "vue";
import axios from "axios";
import {ElMessage} from "element-plus";
import store from "../../../../../store";

const props = defineProps({
  questionnaire_id: {
    type: Number,
    required: true
  }
})

const analysisVisible = ref(false);
const analysisLoading = ref(false);
const questionList = ref([]);
const questionnaire = ref({
  title: '',
  description: '描述',
  status: '已完成',
  createTime: null,
  endTime: null,
  fillCount: 0,
});

const analysisResult = async () => {
  analysisLoading.value = true;
  try {
    const backendUrl = import.meta.env.VITE_BACKEND_IP;
    const token = store.state.token;

    // 并发两个请求：问题列表 + 问卷概况
    const [qListRes, outlineRes] = await Promise.all([
      axios.get(`${backendUrl}/api/questionnaire/get-question`, {
        params: { questionnaireId: props.questionnaire_id },
        headers: { token }
      }),
      axios.get(`${backendUrl}/api/questionnaire/outline`, {
        params: { questionnaireId: props.questionnaire_id },
        headers: { token }
      }),
    ]);

    // 设置问题列表
    questionList.value = JSON.parse(qListRes.data.data).questionList;

    // 设置问卷基本信息
    questionnaire.value = outlineRes.data.data;

    analysisVisible.value = true;
  } catch (error) {
    console.error("加载失败", error);
    ElMessage({ message: "数据加载失败，请重试", duration: 1000 });
  } finally {
    analysisLoading.value = false;
  }
};


</script>

<style scoped>
.analysis-result {
  min-height: 200px;
  margin: 10px;
  background-color: #f1f1f1;
  border-radius: 10px;
  padding: 10px;
}
</style>
