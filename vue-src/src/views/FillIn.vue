<template>
  <el-row v-loading="isLoading" element-loading-text="问卷读取中，请等待。">
    <el-col :lg="{span:16,offset:4}" :md="{span:18,offset:3}" :sm="24" :xl="{span:14,offset:5}"
            class="main-answer-list">
      <el-card class="box-card" shadow="always">
        <div>
          <h1>{{ activityName }} 满意度调查</h1>
          <h3>感谢您参与 {{ activityName }}，请花几分钟时间完成本次问卷，帮助我们提升体验。</h3>
        </div>
      </el-card>
      <el-form :disabled="cannotSubmit">
        <div v-for="(item,index) in questionList"
             :key="index">
          <el-card v-if="ifShowByCheckingFront(index)" class="box-card"
                   shadow="hover"
          >

            <el-form-item>
              <div class="question-title-div">
                <div style="display: inline"><b>{{ index + 1 }}. {{ item.questionTitle }}</b></div>
                <div v-if="item.questionNullable===false" class="nullable-star" style="display: inline">
                  *
                </div>
              </div>
              <div class="description-div">{{ item.questionDescription }}</div>

            </el-form-item>

            <el-form-item>
              <el-radio-group v-if="item.questionType==='single_check'"
                              v-model="answerList[index].answerSingleCheck">
                <el-radio v-for="(optionItem,optionIndex) in item.questionOptions" :key="optionIndex"
                          :label="optionItem">{{ optionItem }}
                </el-radio>
              </el-radio-group>

              <el-checkbox-group v-else-if="item.questionType==='multi_check'"
                                 v-model="answerList[index].answerMultiCheck">
                <el-checkbox v-for="(optionItem,optionIndex) in item.questionOptions" :key="optionIndex"
                             :label="optionItem">{{ optionItem }}
                </el-checkbox>
              </el-checkbox-group>

              <el-input v-else-if="item.questionType==='single_line_text'"
                        v-model="answerList[index].answerText" placeholder="请输入内容（单行文本）"
                        style="max-width: 80%"
                        type="text"
              ></el-input>

              <el-input v-else-if="item.questionType==='multi_line_text'"
                        v-model="answerList[index].answerText" placeholder="请输入内容（多行文本）"
                        style="max-width: 80%"
                        type="textarea"
              ></el-input>

              <el-input v-else-if="item.questionType==='number'&&item.numberType==='integer'"
                        v-model="answerList[index].answerNumber"
                        oninput="this.value = this.value.replace(/[^0-9]/g, '');"
                        placeholder="请输入整数"
                        step="1"
                        style="max-width: 200px"
                        type="number"
              ></el-input>

              <el-input v-else-if="item.questionType==='number'&&item.numberType!=='integer'"
                        v-model="answerList[index].answerNumber"
                        placeholder="请输入数字"
                        step="1"
                        style="max-width: 200px"
                        type="number"
              ></el-input>

              <el-rate v-else-if="item.questionType==='grade'"
                       v-model="answerList[index].answerGrade"
                       :max="item.gradeMax"
              >
              </el-rate>

              <el-date-picker v-else-if="item.questionType==='date'"
                              v-model="answerList[index].answerDate"
              ></el-date-picker>

              <div v-else-if="item.questionType==='text_description'" class="description-div">
                {{ item.textDescriptionValue }}
              </div>

            </el-form-item>
          </el-card>
        </div>


        <el-form-item>
          <el-card class="box-card" style="width: 100%">

            <el-dialog
                v-model="submitVisible"
                append-to-body
                center
                title="确认提交？"
                width="70%"
            >
                            <span slot="footer" class="dialog-footer">
                                <el-button @click="submitVisible = false">取 消</el-button>
                                <el-button type="primary" @click="submitAnswer">确认提交</el-button>
                            </span>

            </el-dialog>
            <el-dialog
                v-model="resetVisible"
                append-to-body
                center
                title="确认重置？"
                width="70%"
            >
                            <span slot="footer" class="dialog-footer">
                                <el-button @click="resetVisible = false">取 消</el-button>
                                <el-button type="primary" @click="resetAnswer">确认重置</el-button>
                            </span>

            </el-dialog>
            <div>
              <el-button type="primary" @click="submitVisible = true">提 交</el-button>
              <el-button @click="resetVisible = true">重 置</el-button>
            </div>

          </el-card>
        </el-form-item>
      </el-form>

    </el-col>
  </el-row>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {useRoute} from 'vue-router'
import axios from 'axios'
import {ElMessage} from 'element-plus'

const route = useRoute()
const questionList = ref([])
const answerList = ref([])
const activityName = ref('未命名活动')
const submitVisible = ref(false)
const resetVisible = ref(false)
const ip = ref(localStorage.getItem('deviceId'))
const alreadySubmit = ref(null)
const cannotSubmit = ref(null)
const isLoading = ref(false)
const fetchData = async () => {
  isLoading.value = true
  try {
    const [questionRes, activityRes] = await Promise.all([
      axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/questionnaire/get-question`, {
        params: { questionnaireId: route.params.id }
      }),
      axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/activity/getActivityByQuestionnaireId`, {
        params: { questionnaireId: route.params.id }
      })
    ])

    // 1. 解析第一层 data 字段
    const responseData = JSON.parse(questionRes.data.data)

    // 2. 获取 questionList 并处理嵌套 JSON
    const tempList = responseData.questionList.map(item => {
      const details = JSON.parse(item.details)
      return {
        ...item,
        details: {
          ...details,
          date: details.date ? new Date(details.date) : null
        },
        date: item.date ? new Date(item.date) : null
      }
    })

    // 3. 创建答案列表
    const ansList = tempList.map(t => ({
      questionId: t.questionId,
      questionTitle: t.questionTitle,
      questionType: t.questionType,
      questionNullable: t.questionNullable,
      answerSingleCheck: '',
      answerMultiCheck: [],
      answerText: '',
      answerNumber: t.details.defaultNumber || 0,
      answerGrade: 0,
      answerDate: t.details.date || new Date()
    }))

    questionList.value = tempList
    answerList.value = ansList
    activityName.value = activityRes.data.data
    ElMessage({ message: "问卷已读取", duration: 1000 })
  } catch (e) {
    console.error("解析或请求错误:", e)
    ElMessage({ message: "数据读取失败！", duration: 1000 })
  } finally {
    isLoading.value = false
  }
}


const submitAnswer = async () => {
  console.log(answerList.value)
  if (checkValidate()) {
    try {
      const req = await axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/questionnaire/submit`, answerList.value, {
        params: {
          questionnaireId: route.params.id,
          ip: ip.value
        }
      })
      if (req.data.code === 200) {
        ElMessage({message: req.data.msg, duration: 1000})
      } else {
        ElMessage({message: req.data.msg, duration: 1000})
      }
      console.log(req)
      submitVisible.value = false
      alreadySubmit.value = true
      cannotSubmit.value = true
    } catch (e) {
      ElMessage({message: "提交失败，请重试！", duration: 1000})
    }
  }
}

const resetAnswer = () => {
  fetchData()
  resetVisible.value = false
}

const ifShowByCheckingFront = (index) => {
  const thisQuestion = questionList.value[index]
  if (thisQuestion.frontChoose === false)
    return true

  for (const oneFront of thisQuestion.frontOptions) {
    const frontIndex = oneFront[0] - 1
    const frontValue = oneFront[1]
    const frontQuestion = questionList.value[frontIndex]
    let checkList = null
    if (frontQuestion.questionType === 'single_check') {
      checkList = answerList.value[frontIndex].answerSingleCheck
      if (frontValue != checkList) return false
    } else if (frontQuestion.questionType === 'multi_check') {
      checkList = answerList.value[frontIndex].answerMultiCheck
      for (const oneOfMulti of frontValue) {
        if (checkList.indexOf(oneOfMulti) === -1)
          return false
      }
    }
  }
  return true
}

const checkValidate = () => {
  for (const oneAnswer of answerList.value) {
    if (oneAnswer.questionNullable === true) {
      console.log('nullable', oneAnswer)
      continue
    } else {
      console.log(oneAnswer.questionType === 'single_check')
      if (oneAnswer.questionType === 'single_check' && oneAnswer.answerSingleCheck === '' ||
          oneAnswer.questionType === 'multi_check' && oneAnswer.answerMultiCheck === [] ||
          oneAnswer.questionType === 'single_line_text' && oneAnswer.answerText === '' ||
          oneAnswer.questionType === 'multi_line_text' && oneAnswer.answerText === '' ||
          oneAnswer.questionType === 'number' && oneAnswer.answerNumber == null ||
          oneAnswer.questionType === 'grade' && oneAnswer.answerGrade === 0 ||
          oneAnswer.questionType === 'date' && oneAnswer.answerGrade == null
      ) {
        console.log('error')
        ElMessage.error(oneAnswer.questionId % 1000 + 1 + ' ' + oneAnswer.questionTitle + ' 是必填字段！')
        return false
      }
    }
  }
  return true
}

onMounted(async () => {
  await fetchData()
})
</script>

<style scoped>
.main-answer-list {
  position: relative;
  text-align: left;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.box-card {
  padding-left: 5%;
}

.nullable-star {
  color: red;
}
</style>
