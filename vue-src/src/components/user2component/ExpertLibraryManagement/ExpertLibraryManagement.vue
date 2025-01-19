<script setup>
import { ref } from 'vue';
import { ElDialog, ElButton, ElSelect, ElOption, ElInput } from 'element-plus';
import axios from 'axios';
import store from "../../../store";  // 引入 store
import QRCode from 'qrcode.vue';  // 引入二维码生成器

// 控制弹窗的显示与关闭
const dialogVisible = ref(false);
const inviteUrlDialogVisible = ref(false);  // 控制显示邀请 URL 的弹窗
const inviteUrl = ref('');  // 存储生成的邀请 URL

const OpenInviteComponent = () => {
  dialogVisible.value = true;
};

const CloseCreateComponent = () => {
  dialogVisible.value = false;
};

const CloseInviteUrlDialog = () => {
  inviteUrlDialogVisible.value = false;
};

const value = ref('3');  // 默认选择3天
const options = [
  { value: '1', label: '1天' },
  { value: '3', label: '3天' },
  { value: '7', label: '7天' },
  { value: '30', label: '30天' }
];

// 计算截止日期
const calculateDeadline = () => {
  const currentDate = new Date();
  const deadlineDate = new Date(currentDate);
  deadlineDate.setDate(currentDate.getDate() + parseInt(value.value));  // 计算截止日期

  // 通过 toISOString 保证返回的日期格式符合后端要求
  const formattedDeadline = deadlineDate.toISOString().slice(0, 19);  // 去除毫秒部分
  console.log('截止日期:', formattedDeadline);
  return formattedDeadline;  // 返回 ISO 格式的截止日期字符串
};

// 提交邀请信息
const CreateInviteURL = async () => {
  const inviteDeadline = calculateDeadline();  // 计算截止日期
  const inviteUserId = store.state.user.user_id;
  const inviteData = {
    invite_user_id: inviteUserId,
    invite_deadline: inviteDeadline  // 发送ISO格式的截止日期
  };

  try {
    const response = await axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/ExpertLibrary/invite-expert`, inviteData);
    const inviteId = response.data.inviteSpecialisRrecord_id;  // 假设后端返回的邀请 ID
    // 生成邀请 URL
    inviteUrl.value = `${window.location.origin}/expertInvitedRegister/${inviteId}`;

    CloseCreateComponent();  // 关闭当前弹窗
    inviteUrlDialogVisible.value = true;  // 显示邀请 URL 的弹窗
  } catch (error) {
    console.error('邀请失败:', error);
    alert('邀请失败，请稍后重试');
  }
};
</script>

<template>
  <el-breadcrumb separator="/">
    <el-breadcrumb-item><strong>专家库管理</strong></el-breadcrumb-item>
  </el-breadcrumb>

  <br />

  <!-- 邀请专家按钮 -->
  <el-button type="primary" @click="OpenInviteComponent">邀请专家</el-button>&nbsp;

  <!-- 创建邀请弹窗 -->
  <el-dialog title="创建邀请链接" v-model="dialogVisible" width="380px" @close="CloseCreateComponent">
    <div>
      <p>邀请有效时间</p>
      <el-select v-model="value" placeholder="3天" size="large" style="width: 220px">
        <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"/>
      </el-select>
    </div><br />
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="CloseCreateComponent">取消</el-button>
      <el-button type="success" @click="CreateInviteURL">确认邀请</el-button>
    </span>
  </el-dialog>

  <!-- 显示邀请链接和二维码的弹窗 -->
  <el-dialog title="邀请链接与二维码" v-model="inviteUrlDialogVisible" width="380px" @close="CloseInviteUrlDialog">
    <div>
      <p>邀请链接已成功生成：</p>
      <el-input v-model="inviteUrl" type="text" readonly style="width: 100%;" />

      <!-- 使用 qrcode.vue 生成二维码 -->
      <div style="text-align: center; margin-top: 20px;">
        <QRCode :value="inviteUrl" size="200" />
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="CloseInviteUrlDialog">关闭</el-button>
    </span>
  </el-dialog>

  <h1>当前在职专家</h1>
</template>

<style scoped>
/* 这里可以添加更多样式 */
</style>
