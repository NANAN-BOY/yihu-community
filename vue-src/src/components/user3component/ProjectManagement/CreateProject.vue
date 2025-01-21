<template>
  <div>
    <br/>
    <!-- 创建表单 -->
    <el-form :model="formData" ref="formRef" label-width="120px">
      <el-form-item label="项目名称*" prop="project_name">
        <el-input v-model="formData.project_name" placeholder="请输入项目名称" />
      </el-form-item>

      <el-form-item
          v-for="(field, index) in templateData.fields"
          :key="index"
          :label="getLabel(field.templateFields_name, field.templateFields_isRequired)"
          :prop="'field_' + index"
      >
        <!-- 根据字段类型动态渲染表单控件 -->
        <el-input
            v-if="field.templateFields_type === 1"
            v-model="formData['field_' + index].value"
            :placeholder="`请输入${field.templateFields_name}`"
            :required="field.templateFields_isRequired"
        />

        <el-input
            v-if="field.templateFields_type === 2"
            type="textarea"
            v-model="formData['field_' + index].value"
            :placeholder="`请输入${field.templateFields_name}`"
            :required="field.templateFields_isRequired"
        />

        <el-select
            v-if="field.templateFields_type === 3"
            v-model="formData['field_' + index].value"
            placeholder="请选择"
            :required="field.templateFields_isRequired"
        >
          <el-option v-for="(option, idx) in JSON.parse(field.templateFields_options)" :key="idx" :label="option" :value="option" />
        </el-select>

        <el-upload
            v-if="field.templateFields_type === 4"
            :action="uploadUrl"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
        >
          <el-button size="small" type="primary">点击上传</el-button>
        </el-upload>
      </el-form-item>
    </el-form>

    <!-- 提交按钮 -->
    <el-button type="primary" @click="submitForm">提交</el-button>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, defineProps, defineEmits } from 'vue';
import axios from 'axios';
import { ElForm, ElInput, ElSelect, ElOption, ElButton, ElUpload, ElMessageBox, ElMessage } from 'element-plus';
import store from '../../../store';

const templateData = ref<any>({});
const formData = ref<any>({ project_name: '' });
const uploadUrl = ref(`${import.meta.env.VITE_BACKEND_IP}/api/upload`); // 上传文件的 URL
const emit = defineEmits<{
  (e: 'closeForm', formData: any): void;
}>();

// 获取启用的模板数据
const fetchTemplateData = async () => {
  try {
    const response = await axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/template/getEnableProjectTemplate`);
    templateData.value = response.data;

    // 1. 初始化 `formData` 中的项目名称
    formData.value.project_name = '';  // 确保项目名称字段存在并初始化

    // 2. 初始化动态字段
    templateData.value.fields.forEach((field: any, index: number) => {
      formData.value['field_' + index] = {
        id: field.templateFields_id,   // 存储字段 ID
        value: field.templateFields_type === 3 ? null : '',  // 根据字段类型初始化值
      };
    });
  } catch (error) {
    console.error('获取模板数据失败:', error);
  }
};

// 获取标签内容（带 * 的必填项）
const getLabel = (label: string, isRequired: boolean) => {
  return isRequired ? `${label} *` : label;
};

// 表单提交
const submitForm = async () => {
  ElMessageBox.confirm(
      '你确定要提交表单吗？',
      '确认提交',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    // 校验必填项
    const invalidFields = templateData.value.fields.filter((field: any, index: number) => {
      if (field.templateFields_isRequired && !formData.value['field_' + index].value) {
        return true;
      }
      return false;
    });
    if (invalidFields.length > 0) {
      ElMessage.error('有必填项未填写！');
      return;
    }

    // 创建一个包含所有数据的对象
    const submitData = {
      project_name: formData.value.project_name,  // 确保提交的字段是formData.project_name
      template_id: templateData.value.template_id,
      projectDeclare_user: store.state.user.user_id,
      projectDeclare_create_at: new Date().toISOString(),
      projectDeclare_draftEnable: true,  // 默认为草稿
      fields: templateData.value.fields.map((field: any, index: number) => ({
        templateFields_id: formData.value['field_' + index].id,
        value: formData.value['field_' + index].value,
      })),
    };
    try {
      const response = await axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/project/submitProjectDeclare`, submitData);
      if (response.data.success) {
        ElMessage.success('提交成功！');
        emit('closeForm', formData);
      } else {
        ElMessage.error('提交失败，请稍后再试！');
      }
    } catch (error) {
      console.error('提交失败:', error);
      ElMessage.error('提交失败，请稍后再试！');
    }
  }).catch(() => {
    ElMessage.info('你取消了提交');
  });
};

// 上传文件成功后的回调
const handleUploadSuccess = (response: any, file: any, fileList: any) => {
  console.log('上传成功', response);
};

// 上传文件前的校验
const beforeUpload = (file: any) => {
  const isLt2M = file.size / 1024 / 1024 < 2; // 限制上传文件大小小于 2MB
  if (!isLt2M) {
    ElMessage.error('上传文件大小不能超过 2MB!');
  }
  return isLt2M;
};

onMounted(() => {
  fetchTemplateData();
});
</script>
