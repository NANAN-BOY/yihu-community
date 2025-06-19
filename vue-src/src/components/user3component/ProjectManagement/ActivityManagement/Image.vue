<script setup>

import {onMounted, ref} from "vue";
import axios from "axios";

const props = defineProps({
  DownloadUrl: {
    type: String,
    default: () => ''
  },
});
const fileSuccess = ref(false)
const OssUrl = ref('')
onMounted(() => {
  getFileOSSUrl(props.DownloadUrl)
  fileSuccess.value = true
  console.log(props.DownloadUrl);
});
const getFileOSSUrl = async (url) => {
  const response = await axios.get(`${url}`);
  console.log('[获取文件URL]', response.data)
  OssUrl.value = response.data.toString();
};
</script>

<template>
  <img v-if="fileSuccess" :src=OssUrl alt="" />
</template>

<style scoped>

</style>