<template>
  <div
      :style="{ top: position.top + 'px', left: position.left + 'px' }"
      class="select-menu"
      @mousedown="startDrag"
      @mousemove="drag"
      @mouseup="stopDrag"
  >
    <div class="header">
      <i class="el-icon-move drag-icon"></i>
      <p class="title">选项</p>
      <el-icon style="margin-left: auto;">
        <Rank/>
      </el-icon>
    </div>
    <div class="item-list">
      <div
          v-for="item in items"
          :key="item.type"
          class="select-item"
          @click="addNewQuestion(item.type)"
      >
        <i :class="item.icon" class="item-icon"></i>
        <p class="item-label">{{ item.label }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {Rank} from "@element-plus/icons-vue";

const emit = defineEmits(['addNewQuestion']);
const position = ref({top: window.innerHeight - 400, left: 20});
const isDragging = ref(false);
const offset = ref({x: 0, y: 0});

const items = [
  {type: 'single_check', label: '单选', icon: 'el-icon-circle-check'},
  {type: 'multi_check', label: '多选', icon: 'el-icon-edit-outline'},
  {type: 'single_line_text', label: '单行文本', icon: 'el-icon-document-remove'},
  {type: 'multi_line_text', label: '多行文本', icon: 'el-icon-document'},
  {type: 'number', label: '数字', icon: 'el-icon-s-data'},
  {type: 'grade', label: '评分', icon: 'el-icon-star-off'},
  {type: 'date', label: '日期', icon: 'el-icon-date'},
  {type: 'text_description', label: '文本描述', icon: 'el-icon-warning-outline'},
];

function startDrag(event) {
  isDragging.value = true;
  offset.value = {
    x: event.clientX - position.value.left,
    y: event.clientY - position.value.top,
  };
}

function drag(event) {
  if (isDragging.value) {
    position.value = {
      top: event.clientY - offset.value.y,
      left: event.clientX - offset.value.x,
    };
  }
}

function stopDrag() {
  isDragging.value = false;
}

function addNewQuestion(type) {
  emit('addNewQuestion', type);
}
</script>

<style scoped>
.select-menu {
  width: 200px;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: fixed;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  cursor: grab;
  z-index: 999;
}

.header {
  display: flex;
  align-items: center;
  background-color: #f5f7fa;
  padding: 8px 10px;
  border-bottom: 1px solid #ebeef5;
  cursor: grab;
}

.drag-icon {
  font-size: 16px;
  margin-right: 10px;
}

.title {
  font-size: 14px;
  font-weight: bold;
  margin: 0;
}

.item-list {
  padding: 8px;
}

.select-item {
  display: flex;
  align-items: center;
  padding: 8px 10px;
  margin-bottom: 6px;
  border-radius: 6px;
  font-size: 13px;
  color: #606266;
  transition: background-color 0.3s;
  cursor: pointer;
}

.select-item:hover {
  background-color: #f0f9ff;
}

.item-icon {
  font-size: 18px;
  margin-right: 8px;
  color: #409eff;
}

.item-label {
  margin: 0;
  font-size: 13px;
}

.select-menu:active {
  cursor: grabbing;
}
</style>
