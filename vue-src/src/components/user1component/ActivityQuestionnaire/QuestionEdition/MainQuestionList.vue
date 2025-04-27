<template>
  <div>
    <div class="main-question-list">
      <div style="padding: 0; display: inline">
        <el-card style="position: relative">
          <div class="box-is-not-selected-wrapper" @click="editTitle">
            <div v-if="!questionnaire.isBoxSelected">
              <div class="box-is-not-selected">
                <h1>{{ questionnaire.questionnaireTitle }}</h1>
                <h3>{{ questionnaire.questionnaireDescription }}</h3>
              </div>
              <div class="edit-icon"><i class="el-icon-edit-outline"></i></div>
            </div>
            <div v-else>
              <el-form>
                <el-form-item>
                  <el-input
                      v-model="questionnaire.questionnaireTitle"
                      placeholder="请输入问卷标题"
                      style="max-width: 50%"
                      type="text"
                  ></el-input>
                </el-form-item>
                <el-form-item>
                  <el-input
                      v-model="questionnaire.questionnaireDescription"
                      placeholder="请输入问卷描述"
                      style="max-width: 70%"
                      type="textarea"
                  ></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="saveQuestionnaireTitle">保存</el-button>
                  <el-button @click="resetQuestionnaireTitle">重置</el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </el-card>
      </div>

      <div style="height: 10px"></div>

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

      <el-card>
        <el-dialog
            v-model="deleteVisible"
            append-to-body
            center
            title="确认删除？"
            width="30%"
        >
          <template #footer>
            <el-button @click="deleteVisible = false">取 消</el-button>
            <el-button type="danger" @click="deleteQuestionnaire">确认删除</el-button>
          </template>
        </el-dialog>

        <el-dialog
            v-model="releaseVisible"
            append-to-body
            center
            title="发布成功"
            width="30%"
        >
          <div>
            问卷链接为：
            <el-link
                :data-clipboard-text="`${servername}/fillin/${route.params.id}`"
                class="copy-link"
                data-clipboard-action="copy"
                target="_blank"
                type="primary"
                @click="copy"
            >
              {{ questionnaire.questionnaireTitle }}-点击复制
            </el-link>
          </div>
          <template #footer>
            <el-button type="primary" @click="releaseEnd">关 闭</el-button>
          </template>
        </el-dialog>

        <el-button type="danger" @click="deleteVisible = true">删 除</el-button>
        <el-button @click="saveQuestionnaire">保 存</el-button>
        <el-button type="primary" @click="releaseQuestionnaire">发 布</el-button>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import Question from './Question.vue';
import Clipboard from 'clipboard';
import {ElMessage} from 'element-plus';
import axios from 'axios';
import store from "../../../../store";


const route = useRoute();
const router = useRouter();

const servername = location.origin;

const questionList = ref([]);
const questionnaire = reactive({
  isBoxSelected: false,
  questionnaireDescription: '请输入描述',
  questionnaireTitle: '请输入标题',
  questionnaireId: route.params.id,
});
const deleteVisible = ref(false);
const releaseVisible = ref(false);

const fetchData = async () => {
  try {
    const res = await axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/questionnaire/get-temp`, {
      headers: {
        token: store.state.token,
      },
    });
    console.log(res);
    if (res.data.code === 200) {
      questionList.value = res.data.data
      ElMessage({message: '问卷已读取', duration: 1000});
    } else
      throw new Error();
  } catch (e) {
    ElMessage({message: 'error！问卷读取失败！', duration: 1000});
  }
};

const selectOneBox = (index) => {
  questionList.value[index].isBoxSelected = !questionList.value[index].isBoxSelected;
};

const resetQuestion = () => {
};

const saveOneQuestion = async (data) => {
  const index = data.questionIndex;
  const oneQuestion = {...data, isBoxSelected: true};
  questionList.value.splice(index, 1, oneQuestion);

  try {
    await axios.post(`/api/saveOneQuestion?questionnaireId=${route.params.id}`, {question: oneQuestion});
    ElMessage({message: '问卷已保存', duration: 1000});
  } catch (e) {
    ElMessage({message: 'error！问卷未保存！', duration: 1000});
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

const saveQuestionnaireTitle = async () => {
  questionnaire.isBoxSelected = false;
  try {
    await axios.post('/api/saveQuestionnaireOutline', {questionnaire});
    ElMessage({message: '问卷已保存', duration: 1000});
  } catch (e) {
    ElMessage({message: 'error！问卷未保存！', duration: 1000});
  }
};

const resetQuestionnaireTitle = () => {
  Object.assign(questionnaire, {
    isBoxSelected: false,
    questionnaireDescription: '请输入描述',
    questionnaireTitle: '请输入标题',
    questionnaireId: route.params.id,
  });
};

const editTitle = () => {
  questionnaire.isBoxSelected = true;
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
    children: item.questionOptions.map(opt => ({value: opt, label: opt})),
  }));
};

const releaseQuestionnaire = async () => {
  try {
    await axios.post(`/api/releaseQuestionnaire?questionnaireId=${route.params.id}`);
    ElMessage({message: '问卷已发布', duration: 1000});
  } catch (e) {
    ElMessage({message: 'error！问卷未发布！', duration: 1000});
  }
  releaseVisible.value = true;
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
  height: 100%;
  background-color: white;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
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
