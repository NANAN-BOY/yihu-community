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
          v-for="(item,index) in props.questionList"
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
import AnalysisCard from "./AnalysisCard.vue"

const props = defineProps({
  questionnaire_id: {
    type: Number,
    required: true
  },
  questionList: {
    type: Array,
    required: true
  },
  questionnaire: {
    default: () => ({
      title: '',
      description: '描述',
      status: '已完成',
      createTime: null,
      endTime: null,
      fillCount: 299,
    }),
    type: Object,
    required: true
  }
})

const translateLabel = {
  'collecting': '收集中',
  'editing': '编辑中',
  'closed': '已关闭'
}

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
