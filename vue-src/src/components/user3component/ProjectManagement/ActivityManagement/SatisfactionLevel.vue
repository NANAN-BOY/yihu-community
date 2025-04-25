<script setup>


import axios from "axios";
import store from "../../../../store";
import {ref} from "vue";
import {ElMessage} from "element-plus";

const props = defineProps({
  activityId:{
    type: Number,
    required: true
  }
});

const releaseQuestionnaireLoading = ref(false);
const releaseQuestionnaire = async () => {
  releaseQuestionnaireLoading.value = true;
  console.log("发布问卷");
  try {
    const response = await axios.put(`${import.meta.env.VITE_BACKEND_IP}/api/questionnaire/release`, {},
        {
      params: {
        activityId: props.activityId
      },
      headers: {
        token: store.state.token
      }
    });
    console.log(response.data);
    if (response.data.code === 200 && response.data.data) {
      ElMessage.success("发布成功")
    } else {
      ElMessage.success("发布失败，")
    }
  } catch (error) {
    console.error('API 请求失败:', error);
    ElMessage.success("发布失败")
  } finally {
    releaseQuestionnaireLoading.value = false; // 结束加载状态
  }
}
</script>

<template>
  <div>
    <div>
      <el-button type="primary" @click="releaseQuestionnaire">发布问卷</el-button>
    </div>
  </div>
</template>

<style scoped>

</style>