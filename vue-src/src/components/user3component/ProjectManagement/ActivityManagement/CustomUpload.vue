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
        accept="image/*"
        multiple
        style="display: none"
        type="file"
        @change="handleFileChange"
    />

    <!-- 图片预览列表 -->
    <div class="preview-list">
      <div
          v-for="(file, index) in filteredFileList"
          :key="file.id"
          class="preview-item"
      >
        <img :src="file.url" class="preview-image"/>
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
import {Plus, Close} from '@element-plus/icons-vue';

const props = defineProps({
  modelValue: {
    type: String,
    required: true
  },
  fileType: String,
  fileTypeName: String,
});
const emit = defineEmits(['update:modelValue']);

const filteredFileList = computed(() => {
  // 过滤出符合fileType条件的文件
  if (!props.modelValue || !Array.isArray(props.modelValue)) return [];

  return props.modelValue
      .filter(file => file.fileSort === props.fileType)
      .map(file => ({
        ...file,
        // 处理文件列表，添加URL
        url: `${import.meta.env.VITE_BACKEND_IP}/api/activity/download/${file.id}`
      }));
});

const fileInput = ref(null);
const fileList = ref([]);

const uploadFileLoading = ref(false)
// 点击添加按钮
const handleAddClick = () => {
  uploadFileLoading.value = true;
  console.log(props.modelValue);
  console.log(props.fileType)
  fileInput.value.click();
};

// 文件选择变化
const handleFileChange = (e) => {
  const files = Array.from(e.target.files);
  if (files.length === 0) return;

  files.forEach(file => {
    const reader = new FileReader();
    reader.onload = (e) => {
      const newFile = {
        id: Date.now() + Math.random().toString(36).substr(2, 9),
        file: file,
        url: e.target.result
      };
      fileList.value.push(newFile);
      emit('add', newFile);
    };
    reader.readAsDataURL(file);
  });

  // 清空input以便重复选择相同文件
  e.target.value = '';
};

// 删除文件
const handleRemove = (index) => {
  const removedFile = fileList.value[index];
  fileList.value.splice(index, 1);
  emit('remove', removedFile);
};

// 暴露方法给父组件
defineExpose({
  clearFiles: () => {
    fileList.value = [];
  }
});
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
