<template>
  <div>
    <el-card class="box-card" shadow="hover">
      <div class="analysis-title">{{ questionIndex }}. {{ questionTitle }}</div>

      <el-divider></el-divider>

      <div v-if="questionType==='date'||questionType==='single_line_text'||questionType==='multi_line_text'">
        <el-table
            :data="writeValueList"
            border
            height="250"
            style="width: 80%; margin: auto">
          <el-table-column
              label="填写内容"
              prop="writeValue">
          </el-table-column>
        </el-table>
      </div>

      <div v-if="questionType==='single_check'||questionType==='multi_check'">

        <el-radio-group v-model="showBar" class="choose-show">
          <el-radio :label="true">数量</el-radio>
          <el-radio :label="false">占比</el-radio>
        </el-radio-group>

        <el-row>
          <el-col :lg="18" :md="18" :sm="18" :xl="18" :xs="24">
            <div v-show="showBar===true" :id="'barChart'+questionIndex" :style="{height:barHeight}"
                 class="bar-chart"></div>

            <div v-show="showBar===false" :id="'pieChart'+questionIndex" class="pie-chart"></div>
          </el-col>

          <el-col :lg="6" :md="6" :sm="6" :xl="6" :xs="24">
            <el-table
                :data="questionValueList"
                :default-sort="{prop:'value',order:'descending'}"
                class="analysis-table">
              <el-table-column
                  label="选项"
                  prop="name">
              </el-table-column>
              <el-table-column
                  label="人数"
                  prop="value">
              </el-table-column>
            </el-table>
          </el-col>

        </el-row>

      </div>

      <div v-if="questionType==='number'||questionType==='grade'">

        <el-row>
          <el-col :lg="18" :md="18" :sm="18" :xl="18" :xs="24">
            <div v-show="showBar===true" :id="'barChart'+questionIndex" :style="{height:barHeight}"
                 class="bar-chart"></div>

            <div v-show="showBar===false" :id="'pieChart'+questionIndex" class="pie-chart"></div>
          </el-col>

          <el-col :lg="6" :md="6" :sm="6" :xl="6" :xs="24">
            <el-table
                :data="questionValueList"
                :default-sort="{prop:'value',order:'descending'}"
                class="analysis-table">
              <el-table-column
                  label="内容"
                  prop="name">
              </el-table-column>
              <el-table-column
                  label="数值"
                  prop="value">
              </el-table-column>
            </el-table>
          </el-col>

        </el-row>

      </div>

    </el-card>
  </div>
</template>

<script setup>
import {ref, onMounted, watch} from 'vue'
import * as echarts from 'echarts'
import {ElMessage} from 'element-plus'
import axios from 'axios'

const props = defineProps({
  questionIndex: Number,
  questionId: Number,
  questionTitle: String,
  questionType: String
})

const showBar = ref(true)
const writeValueList = ref(null)
const questionValueList = ref([])
const barHeight = ref('250px')

const fetchData = async () => {
  try {
    if (props.questionType === 'single_check' || props.questionType === 'multi_check' || props.questionType === 'number' || props.questionType === 'grade') {
      const res = await axios.get("/api/getQuestionValueList", {
        params: {
          questionId: props.questionId
        }
      })

      const temp = res.data
      const dataList = []

      for (const oneName in temp) {
        dataList.push({'name': oneName, 'value': temp[oneName]})
      }

      dataList.sort((a, b) => a.value - b.value)
      questionValueList.value = dataList
      barHeight.value = `${questionValueList.value.length * 33 + 150}px`
      drawBar()
      drawPie()
    } else {
      const res = await axios.get("/api/getWriteValue", {
        params: {
          questionId: props.questionId
        }
      })
      writeValueList.value = res.data
    }
  } catch (error) {
    ElMessage({message: "error！读取失败！", duration: 1000})
  }
}

const drawBar = () => {
  const myBarChart = echarts.init(document.getElementById('barChart' + props.questionIndex), 'light')
  myBarChart.setOption({
    tooltip: {},
    grid: {left: '15%'},
    xAxis: {},
    yAxis: {
      data: questionValueList.value.map(x => x['name'])
    },
    series: [{
      name: '选择人数',
      type: 'bar',
      data: questionValueList.value
    }]
  })
}

const drawPie = () => {
  const myPieChart = echarts.init(document.getElementById('pieChart' + props.questionIndex), 'light')
  myPieChart.setOption({
    tooltip: {},
    grid: {left: 20},
    series: [{
      name: '选择人数',
      type: 'pie',
      data: questionValueList.value
    }]
  })
}

onMounted(() => {
  fetchData()
})

watch(() => props.questionId, () => {
  fetchData()
})
</script>

<style scoped>
.bar-chart {
  width: 100%;
}

.pie-chart {
  margin-left: calc(25% - 100px);
  width: 400px;
  height: 300px;
}

.box-card {
  transition: all ease 300ms;
  text-align: left;
}

.analysis-table {
}

.choose-show {
  margin-left: 80px;
}

.analysis-title {
  margin: 15px;
  font-size: 14px;
}
</style>
