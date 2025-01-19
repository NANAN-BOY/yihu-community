<script setup>

import {ElButton} from "element-plus";
</script>

<template>
  <el-breadcrumb separator="/">
    <el-breadcrumb-item><strong>专家库管理</strong></el-breadcrumb-item>
  </el-breadcrumb>
  <br />
  <el-button type="primary" @click="toggleCreateComponent">邀请专家</el-button>&nbsp;
  <h1>当前在职专家</h1>
</template>

<style scoped>

</style>
