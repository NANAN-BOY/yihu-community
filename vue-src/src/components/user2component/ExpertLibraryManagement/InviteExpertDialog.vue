<script setup>
import { ref } from 'vue';
import { ElDialog, ElDatePicker, ElButton } from 'element-plus';

// 控制弹窗的显示与关闭
const dialogVisible = ref(false);

// 存储选择的截止时间
const inviteDeadline = ref(null);

// 弹窗关闭时的回调
const closeDialog = () => {
  dialogVisible.value = false;
  inviteDeadline.value = null; // 重置选择的截止时间
};

// 邀请操作
const inviteExpert = () => {
  if (!inviteDeadline.value) {
    return alert('请选择邀请截止时间');
  }

  // 执行邀请操作 (这里可以添加 API 请求来处理邀请)
  console.log('邀请截止时间:', inviteDeadline.value);
  closeDialog(); // 关闭弹窗
};
</script>

<template>
  <!-- 按钮触发弹窗 -->
  <el-button type="primary" @click="dialogVisible = true">邀请专家</el-button>

  <!-- 弹窗 -->
  <el-dialog
      title="邀请专家"
      :visible.sync="dialogVisible"
      width="400px"
      @close="closeDialog"
  >
    <div>
      <el-form>
        <el-form-item label="邀请截止时间" prop="deadline">
          <el-date-picker
              v-model="inviteDeadline"
              type="datetime"
              placeholder="选择截止时间"
              style="width: 100%;"
          />
        </el-form-item>
      </el-form>
    </div>

    <span slot="footer" class="dialog-footer">
      <el-button @click="closeDialog">取消</el-button>
      <el-button type="primary" @click="inviteExpert">确认邀请</el-button>
    </span>
  </el-dialog>
</template>

<style scoped>
/* 可以自定义弹窗样式，保证其适配手机端 */
.el-dialog {
  max-width: 100%;
  padding: 20px;
}
</style>
