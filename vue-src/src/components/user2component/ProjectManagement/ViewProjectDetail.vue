<template>
  <el-dialog
      v-model="NowOnClickProject"
      title="详细信息"
      width="500"
      align-center
  >
    <div v-if="project">
      <!-- 显示项目的基本信息 -->
      <p><strong>项目名称:</strong> {{ project.project_name }}</p>
      <p><strong>提交时间:</strong> {{ formattedCreateAt }}</p>
      <p><strong>提交组织:</strong> {{ project.project_creator_name }}</p>

      <!-- 显示字段信息 -->
      <div v-for="field in fields" :key="field.field_id">
        <p><strong>{{ field.field_name }}:</strong> {{ field.field_value }}</p>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="NowOnClickProject = null">关闭</el-button>
        <el-button type="primary" @click="NowOnClickProject = null">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watchEffect } from 'vue';
import { ElMessage } from 'element-plus';

// NowOnClickProject NEED
const props = defineProps({modelValue: { type: String, required: true }});
const emit = defineEmits(['update:modelValue']);
const NowOnClickProject = computed({get: () => props.modelValue, set: (newValue) => emit('update:modelValue', newValue)});

// 保存项目数据
const project = ref(null);
const fields = ref([]);

// 格式化时间
const formatDate = (dateString) => {
  const match = dateString.match(/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})/);
  if (match) {
    const [, year, month, day, hours, minutes, seconds] = match;
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  }
  return '无效时间';
};

// 获取项目详情
const getProjectDetails = async (projectId) => {
  console.log(projectId);

  try {
    const response = await fetch(`${import.meta.env.VITE_BACKEND_IP}/api/project/getProjectDetails/${projectId}`);
    const data = await response.json();
    if (data.success) {
      project.value = data.data.project;
      fields.value = data.data.fields;
      formattedCreateAt.value = formatDate(project.value.project_create_at);
    } else {
      ElMessage.error('获取项目详情失败');
    }
  } catch (error) {
    ElMessage.error('请求失败，请稍后重试');
    console.error('Error fetching project details:', error);
  }
};

// 格式化提交时间
const formattedCreateAt = ref('');

// 监听 NowOnClickProject 变化，获取对应项目详情
watchEffect(() => {
  if (NowOnClickProject.value) {
    const projectId = NowOnClickProject.value.projectDeclare_id; // 假设传递的是项目ID
    getProjectDetails(projectId);
  }
});
</script>
