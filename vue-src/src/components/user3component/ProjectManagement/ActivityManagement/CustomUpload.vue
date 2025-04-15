<template>
  <div class="custom-upload">
    <!-- 上传按钮 -->
    <div
        v-loading="uploadFileLoading"
        class="upload-btn"
        @click="handleAddClick"
    >
      <el-icon>
        <Plus/>
      </el-icon>
      <span>添加{{ props.fileTypeName }}</span>
    </div>

    <!-- 隐藏的input用于实际选择文件 -->
    <input
        ref="fileInput"
        :accept="acceptType"
        multiple
        style="display: none"
        type="file"
        @change="handleFileChange"
    />

    <!-- 文件预览列表 -->
    <div class="preview-list">
      <div
          v-for="(file, index) in filteredFileList"
          :key="file.id"
          class="preview-item"
      >
        <img v-if="isImage" :src="file.url" alt="" class="preview-image"/>
        <div v-else class="file-icon">
          <el-icon>
            <Document/>
          </el-icon>
        </div>
        <div class="preview-actions">
          <el-icon @click="handleRemove(index)">
            <Close/>
          </el-icon>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import {computed, ref} from 'vue';
import {Plus, Close, Document} from '@element-plus/icons-vue';
import axios from 'axios';

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  fileType: {
    type: [String, Number],
    required: true
  },
  fileTypeName: String,
  isImage: {
    type: Boolean,
    default: true
  },
  accept: {
    type: String,
    default: 'image/*'
  }
});

const emit = defineEmits(['update:modelValue', 'remove']);

const fileInput = ref(null);
const uploadFileLoading = ref(false);
const acceptType = computed(() => props.isImage ? 'image/*' : props.accept);

const filteredFileList = computed(() => {
  return props.modelValue
      .filter(file => file.fileSort == props.fileType)
      .map(file => ({
        ...file,
        url: file.fileUrl || `${import.meta.env.VITE_BACKEND_IP}/api/activity/download/${file.id}`
      }));
});

const handleAddClick = () => {
  fileInput.value.value = null;
  fileInput.value.click();
};

const handleFileChange = async (e) => {
  const files = Array.from(e.target.files);
  if (files.length === 0) return;

  uploadFileLoading.value = true;
  try {
    const uploadPromises = files.map(file => uploadFile(file));
    const results = await Promise.all(uploadPromises);
    const newFiles = results.map(res => res.data);
    emit('update:modelValue', [...props.modelValue, ...newFiles]);
  } catch (error) {
    console.error('上传失败', error);
    ElMessage.error('文件上传失败');
  } finally {
    uploadFileLoading.value = false;
    e.target.value = '';
  }
};

const uploadFile = async (file) => {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('fileSort', props.fileType);

  try {
    const response = await axios.post('/api/your-upload-endpoint', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    return response.data;
  } catch (error) {
    console.error('Upload error:', error);
    throw error;
  }
};

const handleRemove = (index) => {
  const fileId = filteredFileList.value[index].id;
  const newFiles = props.modelValue.filter(file => file.id !== fileId);
  emit('update:modelValue', newFiles);
  emit('remove', fileId);
};
</script>

<style scoped>
.custom-upload {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.upload-btn {
  width: 100px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #8c939d;
  transition: border-color 0.3s;
}

.upload-btn:hover {
  border-color: #409eff;
}

.preview-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.preview-item {
  position: relative;
  width: 100px;
  height: 100px;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}

.preview-actions {
  position: absolute;
  top: 0;
  right: 0;
  width: 20px;
  height: 20px;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 0 6px 0 0;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: white;
  opacity: 0;
  transition: opacity 0.3s;
}

.preview-item:hover .preview-actions {
  opacity: 1;
}
</style>
