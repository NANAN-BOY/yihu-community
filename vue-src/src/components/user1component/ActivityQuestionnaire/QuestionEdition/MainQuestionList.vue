<template>
  <div>
    <el-breadcrumb separator="/">
      <el-breadcrumb-item><strong>满意度调查模板管理</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <br>
    <div v-loading=questionnaireLoading class="main-question-list">
      <Question
          v-for="(item, index) in questionList"
          :key="index"
          :date="item.date"
          :default-number="item.defaultNumber"
          :front-choose="item.frontChoose"
          :front-options="frontOptions(index)"
          :front-options-init-value="item.frontOptions"
          :grade-max="item.gradeMax"
          :is-box-selected="item.isBoxSelected"
          :number-type="item.numberType"
          :question-description="item.questionDescription"
          :question-index="index"
          :question-nullable="item.questionNullable"
          :question-options="item.questionOptions"
          :question-title="item.questionTitle"
          :question-type="item.questionType"
          :text-description="item.textDescription"
          @clickDelete="deleteOneBox(index)"
          @clickSelected="selectOneBox(index)"
          @clickUnSelected="selectOneBox(index)"
          @resetQuestion="resetQuestion(index)"
          @saveOneQuestion="saveOneQuestion"
      />

      <el-card :body-style="{ padding: '10px' }" class="add-question" shadow="hover">
        <div class="add-question-inner" @click="addNewQuestion('not_selected')">
          <i class="el-icon-plus"></i> 添加问题
        </div>
      </el-card>

    </div>
  </div>
  <el-col :lg="4" :md="6" :sm="8" :xl="4" class="create-page-select-bar-wrapper hidden-xs-only">
    <div class="placeholder"></div>
    <SelectBar
        v-if="!questionnaireLoading"
        class="create-page-select-bar"
        @addNewQuestion="addNewQuestion"
    />
  </el-col>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import Question from './Question.vue';
import Clipboard from 'clipboard';
import {ElMessage} from 'element-plus';
import axios from 'axios';
import store from "../../../../store";
import SelectBar from "./SelectBar.vue";


const route = useRoute();
const router = useRouter();


const questionList = ref([]);
const questionnaire = reactive({
  isBoxSelected: false,
  questionnaireDescription: '请输入描述',
  questionnaireTitle: '请输入标题',
  questionnaireId: route.params.id,
});
const deleteVisible = ref(false);
const releaseVisible = ref(false);

const questionnaireLoading = ref(false);
// MainQuestionList.vue 中修改 fetchData 方法
const fetchData = async () => {
  questionnaireLoading.value = true;
  try {
    const res = await axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/questionnaire/get-temp`, {
      headers: {token: store.state.token},
    });
    console.log(res.data);
    if (res.data.code === 200) {
      //提取转换格式Json
      const tempList = res.data.data;
      questionList.value = tempList.map(item => {
        // 1. 解析 details 字符串
        const detailObj = JSON.parse(item.details);
        // 2. 把 detailObj.date 转成 Date 对象
        detailObj.date = new Date(detailObj.date);
        // 3. 返回一个新的对象，保留原 item 其余字段，并用解析后的 detailObj
        return {
          ...item,
          ...detailObj
        };
      });
    } else throw new Error();
  } catch (e) {
    console.error(e);
    ElMessage({message: '问卷读取失败', duration: 1000});
  } finally {
    questionnaireLoading.value = false;
  }
};

const selectOneBox = (index) => {
  questionList.value[index].isBoxSelected = !questionList.value[index].isBoxSelected;
};

const resetQuestion = () => {
};

const saveOneQuestion = async (data) => {
  console.log(data);
  const index = data.questionIndex;

  // 构建details对象
  const details = {
    questionOptions: data.questionOptions || [],
    frontOptions: data.frontOptions || [],
    frontChoose: data.frontChoose || false,
    numberType: data.numberType || 'integer',
    defaultNumber: data.defaultNumber || 0,
    gradeMax: data.gradeMax || 5,
    date: data.date ? new Date(data.date).toISOString() : new Date().toISOString(),
    textDescription: data.textDescription || ''
  };

  // 构造符合后端DTO的对象
  const oneQuestion = {
    questionTitle: data.questionTitle,
    questionDescription: data.questionDescription,
    questionNullable: data.questionNullable,
    questionType: data.questionType,
    details: JSON.stringify(details),
    isBoxSelected: true // 前端状态字段，不发送到后端
  };

  questionList.value.splice(index, 1, {...data, ...oneQuestion});
  console.log(oneQuestion);
  try {
    const res = await axios.post(
        `${import.meta.env.VITE_BACKEND_IP}/api/questionnaire/add-question`,
        oneQuestion, // 直接发送处理后的对象
        {headers: {token: store.state.token}}
    );
    console.log(res.data);
    ElMessage({message: '问卷已保存', duration: 1000});
  } catch (e) {
    ElMessage({message: '保存失败，请重试！', duration: 1000});
  }
};
const deleteOneBox = (index) => {
  questionList.value.splice(index, 1);
};

const addNewQuestion = (type) => {
  questionList.value.push({
    questionIndex: questionList.value.length,
    isBoxSelected: true,
    questionTitle: '',
    questionNullable: false,
    questionDescription: '',
    questionType: type,
    questionOptions: [''],
    frontOptions: [[]],
    frontChoose: false,
    numberType: 'integer',
    defaultNumber: 0,
    gradeMax: 5,
    date: new Date(),
    textDescription: '',
  });
};

const saveQuestionnaire = async () => {
  try {
    await axios.post('/api/saveQuestionnaire', {
      questionnaire,
      questionList: questionList.value,
    });
    ElMessage({message: '问卷已保存', duration: 1000});
    router.back();
  } catch (e) {
    ElMessage({message: 'error！问卷未保存！', duration: 1000});
  }
};

const deleteQuestionnaire = async () => {
  deleteVisible.value = false;
  try {
    await axios.get('/api/deleteQuestionnaire', {
      params: {questionnaireId: questionnaire.questionnaireId},
    });
    ElMessage({message: '问卷已删除', duration: 1000});
    router.back();
  } catch (e) {
    console.error(e);
  }
};

const frontOptions = (index) => {
  return questionList.value.slice(0, index).map((item, i) => ({
    label: item.questionTitle,
    value: i,
    children: (item.questionOptions || []).map(opt => ({value: opt, label: opt})),
  }));
};

const releaseEnd = () => {
  releaseVisible.value = false;
  router.back();
};

const copy = () => {
  const clipboard = new Clipboard('.copy-link');
  clipboard.on('success', () => {
    ElMessage({message: '复制成功', duration: 1000});
    clipboard.destroy();
  });
  clipboard.on('error', () => {
    ElMessage({message: '该浏览器不支持自动复制', duration: 1000});
    clipboard.destroy();
  });
};

onMounted(fetchData);
</script>

<style scoped>
.main-question-list {
  max-height: calc(100vh - 132px); /* 设置最大高度，避免撑满全屏，可根据实际情况调整 */
  overflow-y: auto; /* 开启内部垂直滚动 */
  background-color: white;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 10px;
  border-radius: 8px;
}

.add-question-inner {
  width: 50%;
  height: 50px;
  padding-top: 25px;
  text-align: center;
  margin: 0 auto;
  border: 2px dashed rgba(128, 128, 128, 0.5);
  color: rgba(128, 128, 128, 0.5);
  font-weight: bolder;
  cursor: pointer;
}

.box-is-not-selected {
  position: relative;
}

.box-is-not-selected-wrapper {
  cursor: pointer;
}

.box-is-not-selected-wrapper:hover .box-is-not-selected {
  filter: blur(8px);
}

.edit-icon {
  display: none;
}

.box-is-not-selected-wrapper:hover .edit-icon {
  display: inline;
  height: 100%;
  width: 100%;
  background-color: rgba(179, 229, 252, 0.3);
  position: absolute;
  top: 0;
  left: 0;
}

.el-icon-edit-outline {
  position: absolute;
  top: calc(50% - 30px);
  left: calc(50% - 30px);
  font-size: 60px;
  color: rgba(128, 0, 128, 0.6);
}
</style>
