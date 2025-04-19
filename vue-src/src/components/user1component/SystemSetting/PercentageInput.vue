<template>
  <div class="custom-percentage-input">
    <!-- 显示框（只读，显示百分数） -->
      <el-button
          @click="decrease"
          :disabled="isMin"
      >
      <el-icon><Minus /></el-icon>
    </el-button>
    <el-input
        :model-value="displayValue"
        readonly
    />
    <el-button
        @click="increase"
        :disabled="isMax"
    >
      <el-icon><Plus /></el-icon>
      </el-button>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import {Minus, Plus} from "@element-plus/icons-vue";

const props = defineProps({
  modelValue: {
    type: Number,
    required: true
  },
  min: {
    type: Number,
    default: 0
  },
  max: {
    type: Number,
    default: 1
  },
  step: {
    type: Number,
    default: 0.01
  },
  precision: {
    type: Number,
    default: 2
  }
})

const emit = defineEmits(['update:modelValue'])

const displayValue = computed(() => {
  return `${(props.modelValue * 100).toFixed(props.precision)}%`
})

const isMin = computed(() => props.modelValue <= props.min)
const isMax = computed(() => props.modelValue >= props.max)

const adjustValue = (delta) => {
  let newValue = props.modelValue + delta
  // 确保值在范围内
  newValue = Math.max(props.min, Math.min(props.max, newValue))
  // 处理浮点数精度问题
  newValue = parseFloat(newValue.toFixed(props.precision))
  emit('update:modelValue', newValue)
}

const increase = () => adjustValue(props.step)
const decrease = () => adjustValue(-props.step)
</script>

<style scoped>
.custom-percentage-input {
  display: flex;
  align-items: center;

  .el-input {
    width: 80px;

    :deep(.el-input__inner) {
      background-color: #f5f7fa;
      cursor: not-allowed;
    }
  }

    .el-button {
      padding: 10px;
      margin-left: 0;
      background-color: #f1f1f1;

      & + .el-button {
        margin-top: 2px;
      }
    }
}
</style>