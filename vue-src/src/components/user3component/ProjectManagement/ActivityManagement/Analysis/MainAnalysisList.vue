<template>
  <div>
    <div class="main-analysis-list">
      <el-card>
        <h1>{{ questionnaire.title }}</h1>
        <div class="description-box">
          <div>
            <b>问卷描述：</b>{{ questionnaire.description }}
          </div>
          <div>
            <b>问卷状态：</b>{{ translateLabel[questionnaire.status] }}
          </div>
          <div>
            <b>填写人数：</b>{{ questionnaire.fillCount }}
          </div>
          <div>
            <b>发布时间：</b>{{ questionnaire.createTime }}
          </div>
          <div>
            <b>截止时间：</b>{{ questionnaire.endTime }}
          </div>
        </div>
      </el-card>
      <AnalysisCard
          v-for="(item,index) in questionList"
          :key="index"
          :question-id="item.questionId"
          :question-index="index+1"
          :question-title="item.questionTitle"
          :question-type="item.questionType"
      />
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {useRoute} from 'vue-router'
import axios from 'axios'
import AnalysisCard from "./AnalysisCard.vue"
import {ElMessage} from "element-plus";

const route = useRoute()

const questionnaire = ref({
  title: '',
  description: '描述',
  status: '已完成',
  createTime: null,
  endTime: null,
  fillCount: 299,
})

const questionList = ref([])

const translateLabel = {
  'collecting': '收集中',
  'editing': '编辑中',
  'closed': '已关闭'
}

const fetchData = () => {
  axios.get("/api/getQuestionList", {
    params: {
      questionnaireId: route.params.id,
    }
  }).then((res) => {
    questionList.value = res.data['questionList']
    ElMessage({message: "问卷已读取", duration: 1000})
  }).catch(() => {
    ElMessage({message: "error！问卷读取失败！", duration: 1000})
  })

  axios.get("/api/getQuestionnaireOutline", {
    params: {
      questionnaireId: route.params.id
    }
  }).then((res) => {
    questionnaire.value = res.data['questionnaire']
  }).catch(() => {
    ElMessage({message: "error！问卷概况读取失败！", duration: 1000})
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.main-analysis-list {
  width: 70%;
  margin: auto;
}

.description-box {
  text-align: left;
  padding-left: 10%;
  line-height: 30px;
  font-size: 14px;
}
</style>
