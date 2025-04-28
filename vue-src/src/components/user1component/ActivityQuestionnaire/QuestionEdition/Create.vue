<template>
  <div class="create-page">
    <el-row class="create-page-main-row">

    <el-col :lg="20" :md="16" :sm="16" :xl="20" class="create-page-question-list">
        <MainQuestionList ref="mainQuestionList"/>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import SelectBar from "./SelectBar.vue";
import MainQuestionList from "./MainQuestionList.vue";

const mainQuestionList = ref(null);

const addNewQuestion = (type) => {
  mainQuestionList.value?.addNewQuestion(type);
};
</script>

<style scoped>
.create-page {
  height: 100%
}

.create-page-main-row {
  height: 100%;
}

.create-page-question-list {
  height: 100%;
}

.create-page-select-bar {
  position: fixed;
}

.placeholder {
  min-width: 250px;
  min-height: 1px;
}
</style>
