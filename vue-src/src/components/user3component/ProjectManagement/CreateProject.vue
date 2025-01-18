<template>
  <div>
    <br/>
    <!-- 创建表单 -->
    <el-form :model="formData" ref="formRef" label-width="120px">
      <el-form-item v-for="(field, index) in templateData.fields" :key="index" :label="field.templateFields_name" :prop="'field_' + index">
        <!-- 根据字段类型动态渲染表单控件 -->
        <el-input v-if="field.templateFields_type === 1" v-model="formData['field_' + index]" :placeholder="`请输入${field.templateFields_name}`" :disabled="!field.templateFields_isRequired" />

        <el-input v-if="field.templateFields_type === 2" type="textarea" v-model="formData['field_' + index]" :placeholder="`请输入${field.templateFields_name}`" :disabled="!field.templateFields_isRequired" />

        <el-select v-if="field.templateFields_type === 3" v-model="formData['field_' + index]" placeholder="请选择" :disabled="!field.templateFields_isRequired">
          <el-option v-for="(option, idx) in JSON.parse(field.templateFields_options)" :key="idx" :label="option" :value="option" />
        </el-select>

        <el-upload v-if="field.templateFields_type === 4" :action="uploadUrl" :disabled="!field.templateFields_isRequired" :on-success="handleUploadSuccess" :before-upload="beforeUpload">
          <el-button size="small" type="primary">点击上传</el-button>
        </el-upload>
      </el-form-item>
    </el-form>

    <!-- 提交按钮 -->
    <el-button type="primary" @click="submitForm">提交</el-button>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { ElForm, ElInput, ElSelect, ElOption, ElButton, ElUpload } from 'element-plus';

const templateData = ref<any>({});
const formData = ref<any>({}); // 用于保存表单数据
const uploadUrl = ref('http://localhost:3001/api/upload'); // 上传文件的 URL

// 获取启用的模板数据
const fetchTemplateData = async () => {
  try {
    const response = await axios.get('http://localhost:3001/api/template/getEnableProjectTemplate');
    templateData.value = response.data;
    // 根据字段动态初始化表单数据
    templateData.value.fields.forEach((field: any, index: number) => {
      formData.value['field_' + index] = field.templateFields_type === 3 ? null : ''; // 选择框初始化为 null，其他字段初始化为空字符串
    });
  } catch (error) {
    console.error('获取模板数据失败:', error);
  }
};

// 表单提交
const submitForm = () => {
  // 提交表单数据的逻辑
  console.log('提交的表单数据:', formData.value);
};

// 上传文件成功后的回调
const handleUploadSuccess = (response: any, file: any, fileList: any) => {
  console.log('上传成功', response);
};

// 上传文件前的校验
const beforeUpload = (file: any) => {
  const isLt2M = file.size / 1024 / 1024 < 2; // 限制上传文件大小小于 2MB
  if (!isLt2M) {
    alert('上传文件大小不能超过 2MB!');
  }
  return isLt2M;
};

onMounted(() => {
  fetchTemplateData();
});
</script>

<style scoped>
/* 自定义样式 */
</style>
