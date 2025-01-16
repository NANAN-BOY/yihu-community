<script setup>
import { ArrowDown, ArrowUp, Close, Document, DocumentAdd, FolderAdd } from '@element-plus/icons-vue';
import { reactive, ref } from 'vue';
import store from '../../../../store';
import { ElNotification } from 'element-plus'; // 假设你已经配置了 Vuex 存储

let onSteps = ref(1);

// 定义表单和字段
const form = reactive({
  name: '',
  description: '',
  fields: [], // 保存字段数据
});

const currentField = reactive({
  fieldName: '',
  fieldType: '1',
  isRequired: false,
  options: [],
});
const userId = store.state.user.user_id;

// 添加选项
const addOption = (option) => {
  if (option.trim() === '') return;
  currentField.options.push(option);
};

// 添加字段
const addField = () => {
  if (!currentField.fieldName.trim()) {
    alert('字段名称是必填项！');
    return;
  }
  form.fields.push({
    fieldName: currentField.fieldName,
    fieldType: currentField.fieldType,
    isRequired: currentField.isRequired,
    options: currentField.fieldType === '3' ? [...currentField.options] : null,  // 如果是下拉选择，确保 options 为空时是 null
  });

  // 重置字段输入
  currentField.fieldName = '';
  currentField.fieldType = '1';
  currentField.isRequired = false;
  currentField.options = [];
};

// 删除字段
const removeField = (index) => {
  form.fields.splice(index, 1);
};

// 修改字段顺序
const moveField = (fromIndex, toIndex) => {
  const field = form.fields.splice(fromIndex, 1)[0];
  form.fields.splice(toIndex, 0, field);
};

// 格式化字段类型
const formatFieldType = (row) => {
  return row.fieldType === '1'
      ? '文本框'
      : row.fieldType === '2'
          ? '多行文本框'
          : row.fieldType === '3'
              ? '下拉选择'
              : '文件上传';
};

// 格式化是否必填
const formatIsRequired = (row) => (row.isRequired ? '是' : '否');

// 提交表单
const submitForm = () => {
  const requestBody = {
    template_name: form.name,
    template_description: form.description,
    template_create_user: userId, // 使用 Vuex 获取的用户 ID
    TemplateFields: form.fields.map(field => ({
      templateFields_name: field.fieldName,
      templateFields_type: field.fieldType,
      templateFields_isRequired: field.isRequired,
      options: field.fieldType === '3' && field.options.length > 0 ? field.options : null,  // 如果 options 为空，使用 null
    })),
  };

  // 调用后端接口提交数据
  fetch('http://localhost:3001/api/template/createProjectTemplate', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(requestBody),
  })
      .then((response) => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
      .then((data) => {
        ElNotification({
          title: '成功',
          message: '模板保存成功！',
          type: 'success',
          duration: 3000, // 自动关闭时间
        });
        onSteps.value = 3; // 提交成功，切换到步骤三
      })
      .catch((error) => {
        ElNotification({
          title: '失败',
          message: '模板保存失败，请重试！' + error,
          type: 'error',
          duration: 3000, // 自动关闭时间
        });
        // 失败时不切换步骤，保持当前步骤
      });
};
</script>




<template>
  <el-steps style="max-width: 600px" :active="onSteps">
    <el-step title="模板描述" :icon="Document" />
    <el-step title="创建字段" :icon="DocumentAdd" />
    <el-step title="上传模板文件" :icon="FolderAdd" />
  </el-steps>

  <!--第一步-->
  <div v-if="onSteps === 1">
    <el-form :model="form" label-width="auto" style="max-width: 600px">
      <el-form-item label="模板名称">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="模板描述">
        <el-input v-model="form.description" />
      </el-form-item>
      <el-form-item>
        <el-button
            type="primary"
            @click="onSteps = 2"
            :disabled="!form.name.trim()"
        >下一步</el-button>
      </el-form-item>
    </el-form>
  </div>


  <!--第二步-->
  <div v-if="onSteps === 2">
    <el-form label-width="auto" style="max-width: 600px">
      <el-form-item label="字段名称">
        <el-input v-model="currentField.fieldName" placeholder="请输入字段名称" />
      </el-form-item>
      <el-form-item label="字段类型">
        <el-select v-model="currentField.fieldType" placeholder="请选择字段类型">
          <el-option label="文本框" value="1" />
          <el-option label="多行文本框" value="2" />
          <el-option label="下拉选择" value="3" />
          <el-option label="文件上传" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="必填">
        <el-checkbox v-model="currentField.isRequired">是否必填</el-checkbox>
      </el-form-item>
      <el-form-item v-if="currentField.fieldType === '3'" label="选项">
        <el-input
            placeholder="请输入选项并回车确认"
            @keyup.enter="addOption($event.target.value); $event.target.value = ''"
        />
        <el-tag
            v-for="(option, index) in currentField.options"
            :key="index"
            closable
            @close="currentField.options.splice(index, 1)"
        >
          {{ option }}
        </el-tag>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="addField">添加字段</el-button>
        <el-button @click="onSteps = 1">上一步</el-button>
        <el-button type="success" @click="submitForm">下一步</el-button>
      </el-form-item>
    </el-form>

    <h3>已添加的字段：</h3>
    <div v-for="(field, index) in form.fields" :key="index" style="border: 1px solid #ebeef5; border-radius: 4px; padding: 16px; margin-bottom: 8px;">
      <div style="display: flex; justify-content: space-between;">
        <span><strong>字段名称:</strong> {{ field.fieldName }}</span>
        <span><strong>字段类型:</strong> {{ formatFieldType(field) }}</span>
        <span><strong>必填:</strong> {{ formatIsRequired(field) }}</span>
      </div>
      <div style="display: flex; justify-content: flex-end; margin-top: 8px;">
        <el-button type="danger" size="small" @click="removeField(index)" style="margin-right: 8px;">删除</el-button>
        <el-button type="primary" size="small" @click="moveField(index, index - 1)" :disabled="index === 0" style="margin-right: 8px;">上移</el-button>
        <el-button type="primary" size="small" @click="moveField(index, index + 1)" :disabled="index === form.fields.length - 1">下移</el-button>
      </div>
    </div>
  </div>

  <!--第三步-->
  <div v-if="onSteps === 3">
    <el-button type="primary" @click="submitForm">保存模板</el-button>
  </div>
</template>

<style scoped></style>
