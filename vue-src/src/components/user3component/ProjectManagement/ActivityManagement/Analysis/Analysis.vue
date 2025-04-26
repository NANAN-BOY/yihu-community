<template>
  <div v-loading="analysisLoading"
       class="analysis-result"
       element-loading-text="分析中...(这可能需要一点时间)">
    <el-button
        type="primary"
        @click="analysisResult"
    >查看问卷分析结果
    </el-button>
    <div v-if="analysisLoading" class="outlineData">
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
const questionList = ref([])
const questionnaire = ref({
  title: '',
  description: '描述',
  status: '已完成',
  createTime: null,
  endTime: null,
  fillCount: 299,
})

const analysisResult = async () => {
  analysisLoading.value = true;
  setTimeout(() => {
    analysisLoading.value = false;
  }, 6000);
  // try {
  //   const [questionListRes, questionnaireRes] = await Promise.all([
  //     axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/getQuestionList`, {
  //       params: {
  //         questionnaireId: props.questionnaire_id,
  //       },
  //       headers: {
  //         token: store.state.token
  //       }
  //     }),
  //     axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/getQuestionnaireOutline`, {
  //       params: {
  //         questionnaireId: props.questionnaire_id,
  //       },
  //       headers: {
  //         token: store.state.token
  //       }
  //     })
  //   ]);
  //
  //   questionList.value = questionListRes.data['questionList'];
  //   questionnaire.value = questionnaireRes.data['questionnaire'];
  // } catch (error) {
  //   ElMessage({ message: "数据加载失败，请重试", duration: 1000 });
  // } finally {
  //   analysisLoading.value = false;
  // }
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
