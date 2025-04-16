<template>
  <div class="custom-upload">
    <!-- 上传按钮 -->
    <div
        v-loading="uploadFileLoading"
        class="upload-btn"
        @click="handleUpload()"
    >
      <el-icon>
        <Plus/>
      </el-icon>
      <span>添加{{ props.fileTypeName }}</span>
    </div>

    <!-- 文件预览列表 -->
    <div class="preview-list">
      <div
          v-for="(file, index) in filteredFileList"
          :key="file.id"
          class="preview-item"
      >
        <img v-if="props.fileTypeName==='图片'" :src="file.url" alt="" class="preview-image"  @click.stop="openFileDetailVisible(file)"/>
        <div v-else class="file-icon">
          <el-icon>
            <Document/>
          </el-icon>
        </div>
        <div class="preview-actions" @click.stop="openFileDetailVisible(file)" >
          <el-icon @click.stop="handleRemove(file,true)">
            <Close/>
          </el-icon>
        </div>
      </div>
    </div>
  </div>
  <el-dialog
      v-model="fileDetailVisible"
      title="详情"
      width="500"
      align-center
  >
    <span v-if="fileDetailVisible">
      <img v-if="props.fileTypeName==='图片'" :src="nowOnClickFile.url" alt="" class="preview-image"/>
    </span>
    <template #footer>
      <div class="dialog-footer">
        <el-button type=danger @click="handleRemove(nowOnClickFile,false)">删除文件</el-button>
        <el-button @click="clostFileDetailVisible">Cancel</el-button>
        <el-button type="primary" @click="clostFileDetailVisible">
          Confirm
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>


<script setup>
import {computed, h, ref} from 'vue';
import {Plus, Close, Document} from '@element-plus/icons-vue';
import axios from 'axios';
import {ElMessage, ElMessageBox} from "element-plus";
import store from "../../../../store";

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
  acceptFileType: {
    type: String,
    default: 'image/*'
  },
  activityId:{
    type: Number,
    required: true
  }
});
const emit = defineEmits(['update:modelValue', 'remove']);
const uploadFileLoading = ref(false);

const filteredFileList = computed(() => {
  return props.modelValue
      .filter(file => file.fileSort === props.fileType)
      .map(file => ({
        ...file,
        url: `${import.meta.env.VITE_BACKEND_IP}/api/activity/download/${file.id}`
      }));
});
const TYPE_EXTENSION_MAP = {
  '图片': ['jpg', 'jpeg', 'png', 'gif', 'bmp'],
  '压缩包': ['zip', 'rar', '7z']
};

async function uploadFile() {
  console.log('[1/4] 启动文件上传流程');
  // 创建文件输入元素
  const fileInput = document.createElement('input');
  fileInput.type = 'file';
  fileInput.accept = props.acceptFileType;
  fileInput.style.display = 'none';
  document.body.appendChild(fileInput);

  try {
    // 触发文件选择
    console.log('[2/4] 打开文件选择器');
    let file = null;
    try {
      fileInput.click();

      // 等待文件选择
      file = await new Promise((resolve, reject) => {
        fileInput.onchange = () => resolve(fileInput.files[0]);
        fileInput.oncancel = () => reject('用户取消选择');
      });

      console.log('[选择文件]', file ? file.name : '无文件');
    }catch (e){
      throw new Error('未选择文件');
    }

      // 基础验证
    if (!file) {throw new Error('未选择文件');}
    const maxSize = 50 * 1024 * 1024 // 50MB
    if (file.size > maxSize) throw new Error('文件大小不能超过50MB!');
    // 扩展名验证逻辑
    const allowedExts = TYPE_EXTENSION_MAP[props.fileTypeName] || [];
    const fileExt = file.name.split('.').pop().toLowerCase();

    if (!allowedExts.includes(fileExt)) {
      throw new Error(`仅支持${props.fileTypeName}格式: ${allowedExts.join('/')}`);
    }
    // 准备上传数据
    const formData = new FormData();
    formData.append('file', file);
    console.log('[3/4] 开始上传文件', file.name);
    // 使用axios发送请求
    const response = await axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/activity/uploadFile`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'token': store.state.token
      },
      params: {
        sort: props.fileType,
        activityId:props.activityId,
      },
      timeout: 10000
    });

    console.log('[4/4] 上传完成', response.data);
    return response.data;

  } catch (error) {
    console.error('[上传中断]', error.message);
    throw error; // 抛出错误供外部处理
  } finally {
    // 清理资源
    document.body.removeChild(fileInput);
    console.log('[清理] 移除临时元素');
  }
}
async function handleUpload() {
  try {
    uploadFileLoading.value = true
    const result = await uploadFile();
    console.log('上传成功结果:', result);
    const file = result.data

    if (file) {
      emit('update:modelValue', [...props.modelValue, file]);
      ElMessage.success('文件上传成功')
    } else {
      ElMessage.error('上传失败')
    }
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    uploadFileLoading.value = false; // 确保重置
  }
}

// 文件标签点击处理
const handleFileTagClick = (fileId) => {
  // 1. 创建隐藏的iframe（兼容所有浏览器）
  const iframe = document.createElement('iframe')
  iframe.style.display = 'none'


  iframe.src = `${import.meta.env.VITE_BACKEND_IP}/api/files/download/${fileId}`
  document.body.appendChild(iframe)

  setTimeout(() => {
    document.body.removeChild(iframe)
  }, 5000)

  // 6. 显示下载提示（可选）
  console.log(`开始下载文件 ${fileId}`)
  ElMessage.info('下载已开始，请查看浏览器下载列表')
}
const handleRemove = (file, photoVisible) => {
  ElMessageBox({
    title: '警告',
    message: h('div', null, [
      h('p', {style: 'color: red; margin-bottom: 10px'}, '确认要删除此文件吗？'),
      photoVisible && file.url && h('img', {
        src: file.url,
        style: 'max-width: 100%; max-height: 200px; display: block; margin: 0 auto; border-radius: 4px;',
        alt: '文件预览'
      }),
    ]),
    showCancelButton: true,
    confirmButtonText: '确认删除',
    cancelButtonText: '取消',
    beforeClose: async (action, instance, done) => {
      if (action === 'confirm') {
        try {
          instance.confirmButtonLoading = true;
          instance.confirmButtonText = '删除中...';

          const response = await axios.delete(`${import.meta.env.VITE_BACKEND_IP}/api/activity/deleteFileById`, {
            params: {fileId: file.id},
            headers: {
              'token': store.state.token
            }
          });
          console.log(response);
          if (response.data.code === 200) {
            done();
            ElMessage.success('删除成功');
            instance.confirmButtonLoading = false;
            instance.confirmButtonText = '确认删除';
            //如果打开文件详情
            clostFileDetailVisible()
            //从列表删除
            const newFiles = props.modelValue.filter(f => f.id !== file.id);
            emit('update:modelValue', newFiles);
          } else {
            ElMessage.error(response.data.data);
            instance.confirmButtonLoading = false;
            instance.confirmButtonText = '确认删除';
          }
        } catch (error) {
          ElMessage.error(error.response?.data?.message || '删除失败');
          instance.confirmButtonLoading = false;
          instance.confirmButtonText = '确认删除';
        }
      } else {
        done();
      }
    },
  }).catch(() => {
    // 取消操作不做处理
  });
};
const fileDetailVisible = ref(false);
const nowOnClickFile = ref(null)
const openFileDetailVisible = (file) => {
  nowOnClickFile.value=file;
  fileDetailVisible.value = true;
};
const clostFileDetailVisible = () => {
  nowOnClickFile.value=null;
  fileDetailVisible.value = false;
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
