<script setup>
import { onMounted, ref } from "vue";
import { ElButton, ElInput, ElMessage } from "element-plus";
import Analysis from "./Analysis/Analysis.vue";
import QRCode from "qrcode.vue";

const props = defineProps({
  questionnaire_id: {
    type: Number,
    required: true,
    default: 0
  }
});

const QuestionnaireUrl = ref("");
const setURL = (QuestionnaireId) => {
  return `${window.location.origin}/fillin/${QuestionnaireId}`;
};

const copyInviteUrl = async (textToCopy) => {
  try {
    await navigator.clipboard.writeText(textToCopy);
    ElMessage.success('邀请链接已复制成功！');
  } catch (err) {
    ElMessage.error('复制失败，请手动选择文本复制');
    console.error('复制失败:', err);
  }
};

onMounted(() => {
  QuestionnaireUrl.value = setURL(props.questionnaire_id);
});
</script>

<template>
  <div class="invite-container">
    <div class="invite-content">
      <div class="qrcode-wrapper">
        <QRCode :value="QuestionnaireUrl" :size="200" />
      </div>
      <div class="url-section">
        <el-input v-model="QuestionnaireUrl" type="text" readonly class="url-input" />
        <el-button @click="copyInviteUrl(QuestionnaireUrl)" size="small" class="copy-button">复制链接</el-button>
      </div>
    </div>
  </div>
  <Analysis :questionnaire_id="props.questionnaire_id" />
</template>

<style scoped>
.invite-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.invite-content {
  display: flex;
  width: 100%;
  max-width: 800px;
  align-items: center;
  justify-content: center;
  gap: 20px;
}

.qrcode-wrapper {
  border: 1px solid #dcdfe6;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  background-color: #fff;
  flex-shrink: 0;
}

.url-section {
  display: flex;
  flex-direction: column;
  flex: 1;
  gap: 10px;
}

.url-input {
  width: 100%;
}

.copy-button {
  align-self: flex-start;
  white-space: nowrap;
}

/* 响应式：手机端布局 */
@media (max-width: 767px) {
  .invite-content {
    flex-direction: column;
  }
  .url-section {
    width: 100%;
    max-width: 500px;
  }
}
</style>
