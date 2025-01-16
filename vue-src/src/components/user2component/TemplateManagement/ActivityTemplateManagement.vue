<script setup>

</script>

<template>
  <h1>活动细节模版管理</h1><br />
  <el-button type="primary">创建新模板</el-button>&nbsp;
  <el-input v-model="input" style="width: auto" placeholder="Please input" />
  <el-button type="primary"><el-icon><Search /></el-icon></el-button>
  <el-table :data="tableData" style="width: 100%">
    <el-table-column prop="date" label="日期" width="180"></el-table-column>
  </el-table>
</template>

<style scoped>

</style>
