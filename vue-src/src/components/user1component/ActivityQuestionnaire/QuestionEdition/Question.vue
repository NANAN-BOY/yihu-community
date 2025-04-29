<template>
  <div style="position: relative;">
    <el-card :body-style="{ padding: '0' }" class="box-card" shadow="hover">
      <div v-if="!isBoxSelected" class="box-is-not-selected-wrapper" @click="clickUnSelected">
        <div class="box-is-not-selected">
          <div class="question-title-div">
            <div style="display: inline">{{ questionIndex + 1 }}. {{ questionTitleValue }}</div>
            <div v-if="questionNullableValue === false" class="nullable-star" style="display: inline">*</div>
          </div>
          <div class="description-div">{{ questionDescriptionValue }}</div>

          <el-radio-group v-if="typeValue === 'single_check'" v-model="optionsValue">
            <el-radio v-for="(item, index) in optionsValue" :key="index">{{ item }}</el-radio>
          </el-radio-group>

          <el-checkbox-group v-else-if="typeValue === 'multi_check'" v-model="optionsValue">
            <el-checkbox v-for="(item, index) in optionsValue" :key="index">{{ item }}</el-checkbox>
          </el-checkbox-group>

          <el-input v-else-if="typeValue === 'single_line_text'" placeholder="请输入内容（单行文本）" style="max-width: 80%"
                    type="text"/>

          <el-input v-else-if="typeValue === 'multi_line_text'" placeholder="请输入内容（多行文本）" style="max-width: 80%"
                    type="textarea"/>

          <el-input v-else-if="typeValue === 'number'" placeholder="请输入数字" step="1" style="max-width: 200px"
                    type="number"/>

          <el-rate v-else-if="typeValue === 'grade'" :max="gradeMaxValue"/>

          <el-date-picker v-else-if="typeValue === 'date'" v-model="dateValue"/>

          <div v-else-if="typeValue === 'text_description'" class="description-div">
            {{ textDescriptionValue }}
          </div>

          <div v-if="frontChoose">
            <el-divider/>
            <div v-for="(item, index) in frontOptionsValue" :key="index">
              前置选项{{ index + 1 }}：
              <el-cascader :model-value="item" :options="frontOptions" disabled/>
            </div>
          </div>
        </div>

        <div class="edit-icon">
          <el-icon>
            <Edit/>
          </el-icon>
        </div>
      </div>

      <div v-else class="box-is-selected">
        <div class="question-index">{{ questionIndex + 1 }}.</div>
        <el-form>
          <el-form-item label="标题：">
            <el-input v-model="questionTitleValue" placeholder="请输入标题" size="medium" style="max-width: 60%"
                      type="text"/>
          </el-form-item>
          <el-form-item label="描述：">
            <el-input v-model="questionDescriptionValue" class="description-div" placeholder="请输入描述" size="medium"
                      type="text"/>
          </el-form-item>
          <el-form-item label="类型：">
            <el-select v-model="typeValue" placeholder="请选择" size="medium">
              <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value"/>
            </el-select>
          </el-form-item>
          <el-form-item label="是否选填：">
            <el-select v-model="questionNullableValue" placeholder="是否可以不填写">
              <el-option :value="true" label="是"/>
              <el-option :value="false" label="否"/>
            </el-select>
          </el-form-item>

          <el-form-item label="是否前置：">
            <el-tooltip content="是否启用级联选择（在用户选择了前置某些选项时出现）" effect="light" placement="top">
              <el-select v-model="frontChooseValue" placeholder="本题在用户选择了前置某些选项时出现">
                <el-option :value="true" label="是"/>
                <el-option :value="false" label="否"/>
              </el-select>
            </el-tooltip>
          </el-form-item>

          <template v-if="frontChooseValue">
            <el-form-item v-for="(item, index) in frontOptionsValue" :key="index"
                          :label="'前置选项' + (index + 1) + '：'">
              <div class="block">
                <el-cascader v-model="frontOptionsValue[index]" :options="frontOptions"
                             :props="{ expandTrigger: 'hover' }"/>
              </div>
              <el-button class="add-option-button" round size="medium" @click="addFrontOption">
                <el-icon>
                  <Plus/>
                </el-icon>
              </el-button>
              <el-button class="delete-option-button" round size="medium" @click="$emit('clickDelete')">
                <el-icon>
                  <Delete/>
                </el-icon>
              </el-button>
            </el-form-item>
          </template>

          <el-divider class="divider"/>

          <template v-if="typeValue === 'single_check' || typeValue === 'multi_check'">
            <el-form-item v-for="(item, index) in optionsValue" :key="index">
              选项{{ index + 1 }}：
              <el-input v-model="optionsValue[index]" style="max-width: 200px"/>
              <el-button class="add-option-button" round size="medium" @click="addOption">
                <el-icon>
                  <Plus/>
                </el-icon>
              </el-button>
              <el-button class="delete-option-button" round size="medium" @click="deleteOption(index)">
                <el-icon>
                  <Delete/>
                </el-icon>
              </el-button>
            </el-form-item>
          </template>

          <template v-if="typeValue === 'number'">
            <el-form-item label="数字类型：">
              <el-select v-model="numberTypeValue" placeholder="请选择数字类型" size="medium">
                <el-option v-for="item in numberTypeOptions" :key="item.value" :label="item.label" :value="item.value"/>
              </el-select>
            </el-form-item>
            <el-form-item label="默认数值：">
              <el-input-number v-model="defaultNumberValue"/>
            </el-form-item>
          </template>

          <template v-if="typeValue === 'grade'">
            <el-form-item>
              最大分数
              <el-input-number v-model="gradeMaxValue"/>
            </el-form-item>
          </template>

          <template v-if="typeValue === 'text_description'">
            <el-form-item label="内容：">
              <el-input v-model="textDescriptionValue" size="medium" style="max-width: 400px" type="textarea"/>
            </el-form-item>
          </template>

          <el-form-item>
            <el-button type="primary" @click="saveOneQuestion">保存</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-button circle class="delete-button" type="danger" @click="clickDelete">
        <el-icon>
          <Delete/>
        </el-icon>
      </el-button>
    </el-card>
  </div>
</template>

<script setup>
import {ref, computed, watch} from 'vue'
import {ElMessage} from 'element-plus'
import {Delete, Edit, Plus} from '@element-plus/icons-vue'

const props = defineProps({
  isBoxSelected: Boolean,
  questionIndex: Number,
  questionTitle: String,
  questionNullable: Boolean,
  questionType: String,
  questionDescription: String,
  questionOptions: {
    type: Array,
    default: () => []  // 添加默认空数组
  },
  frontChoose: Boolean,
  frontOptions: Array,
  frontOptionsInitValue: {
    type: Array,
    default: () => []  // 添加默认空数组
  },
  numberType: String,
  defaultNumber: Number,
  gradeMax: Number,
  date: Date,
  textDescription: String,
  tempId: Number,
})

const emit = defineEmits(['clickUnSelected', 'clickSelected', 'saveOneQuestion', 'resetQuestion', 'clickDelete'])

const typeOptions = [
  {value: 'not_selected', label: '请输入'},
  {value: 'single_check', label: '单选'},
  {value: 'multi_check', label: '多选'},
  {value: 'single_line_text', label: '单行文本'},
  {value: 'multi_line_text', label: '多行文本'},
  {value: 'number', label: '数字'},
  {value: 'grade', label: '评分'},
  {value: 'date', label: '日期'},
  {value: 'text_description', label: '文本描述'},
]

const numberTypeOptions = [
  {value: 'integer', label: '整数'},
  {value: 'fraction', label: '小数'},
]

const questionTitleValue = ref(props.questionTitle)
const questionNullableValue = ref(props.questionNullable)
const questionDescriptionValue = ref(props.questionDescription)
const typeValue = ref(props.questionType)
const optionsValue = ref([...(props.questionOptions || [])])
const numberTypeValue = ref(props.numberType)
const defaultNumberValue = ref(props.defaultNumber)
const gradeMaxValue = ref(props.gradeMax)
const dateValue = ref(props.date)
const textDescriptionValue = ref(props.textDescription)
const frontChooseValue = ref(props.frontChoose)
const frontOptionsValue = ref([...(props.frontOptionsInitValue || [])])

const clickUnSelected = () => emit('clickUnSelected')
const saveOneQuestion = () => {
  emit('saveOneQuestion', questionData.value)
  emit('clickSelected')
}
const clickDelete = () => emit('clickDelete')

const addOption = () => optionsValue.value.push('')
const deleteOption = (index) => optionsValue.value.splice(index, 1)

const addFrontOption = () => frontOptionsValue.value.push([])

const questionData = computed(() => ({
  tempId: props.tempId,
  questionIndex: props.questionIndex,
  questionOptions: optionsValue.value,
  questionDescription: questionDescriptionValue.value,
  questionTitle: questionTitleValue.value,
  questionType: typeValue.value,
  frontChoose: frontChooseValue.value,
  frontOptions: frontOptionsValue.value,
  questionNullable: questionNullableValue.value,
  numberType: numberTypeValue.value,
  defaultNumber: defaultNumberValue.value,
  gradeMax: gradeMaxValue.value,
  date: dateValue.value,
  textDescription: textDescriptionValue.value,
}))
</script>

<style scoped>
.box-is-not-selected {
  position: relative;
  text-align: left;
  margin-left: 20%;
  line-height: 40px;
}

.nullable-star {
  color: red;
}

.delete-button {
  position: absolute;
  right: 10%;
  top: 20px;
}

.question-index {
  margin-bottom: 20px;
}

.box-card {
  transition: all ease 300ms;
}

.box-is-selected {
  text-align: left;
  margin: 30px 0 30px 20%;
}

.box-is-not-selected-wrapper {
  padding-top: 20px;
  padding-bottom: 20px;
  cursor: pointer;
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

.divider {
  max-width: 80%;
}

.add-option-button {
  margin-left: 10px;
}

.description-div {
  max-width: 60%;
}
</style>
