<script setup>
import {ref, watch, onUnmounted, computed, onMounted, nextTick} from 'vue'
import {ElDialog, ElScrollbar, ElInput, ElButton, ElAlert, ElIcon, ElNotification, ElMessage} from 'element-plus'
import {Connection, Document, DocumentAdd, Loading, RefreshRight} from '@element-plus/icons-vue'
import store from "../../store";
import axios from "axios";
import { EventBus } from '../../utils/event-bus';
// 状态管理
const isVisible = computed(() => store.getters.business.acceptExpertId)
const inputIsVisible = computed(() => store.state.expert.input.inputIsVisible)
const hiddenReasons = computed(() => store.state.expert.input.hiddenReasons)
const messages = ref([])
const inputMessage = ref('')
const socket = ref(null)
const connectionStatus = computed(() => store.state.connectionStatus)
let reconnectTimer = null
const MAX_RETRY = 5
let retryCount = 0
const scrollbarRef = ref(null)
const isMobile = ref(window.innerWidth < 768)
const receiveUserInfo = ref({name: '请等待',})
import { Howl } from 'howler';
const onClose = new Howl({
  src: ['TipSound/onClose.mp3'],
  preload: true, // 预加载
  volume: 0.7    // 音量
});
const onOpen = new Howl({
  src: ['TipSound/onOpen.mp3'],
  preload: true, // 预加载
  volume: 0.7    // 音量
});

// 从store获取用户信息
const sendUserId = computed(() => store.state.user.id)
const receiveUserId = computed(() => {
  if (store.state.user.role === 3) {
    return store.state.expert.business.acceptExpertId;
  } else if (store.state.user.role === 4) {
    return store.state.expert.business.applyUserId;
  } else {
    return store.state.expert.business.acceptExpertId;
  }
})
const businessId = computed(() => store.state.expert.business.id)

// 初始化WebSocket连接（无论弹窗是否打开）
onMounted(() => {
  initWebSocket()
})
const loadHistory = async () => {
  try {
    const response = await axios.get(
        `${import.meta.env.VITE_BACKEND_IP}/api/communication`,
        {
          params: {
            businessId: businessId.value,
            sendUserId: sendUserId.value,
            receiveUserId: receiveUserId.value
          },
          headers: {
            token: store.state.token
          }
        }
    )
    // 转换消息格式
    const historyMessages = response.data.data.map(msg => ({
      ...msg,
      time: new Date(msg.time).getTime() || Date.now(),
      status: 'success'
    }))

    // 合并历史消息（按时间升序排列）
    messages.value = [
      ...historyMessages,
      ...messages.value
    ].sort((a, b) => a.time - b.time)

    // Scroll to bottom
    await nextTick()
    const wrap = scrollbarRef.value?.$el.querySelector('.el-scrollbar__wrap')
    if (wrap) {
      wrap.scrollTop = wrap.scrollHeight
    }
  } catch (error) {
    ElNotification({
      title: '加载失败',
      message: '历史记录加载失败，请稍后重试',
      type: 'error'
    })
  }
}

// 监听store状态变化
watch(isVisible, async (newVal) => {
  if (newVal) {
    await loadHistory()
    await nextTick(() => {
      const wrap = scrollbarRef.value?.$el.querySelector('.el-scrollbar__wrap')
      if (wrap) {
        wrap.scrollTop = wrap.scrollHeight
      }
    })
    receiveUserInfo.value = await getUserInfo(receiveUserId.value);
  } else {
    messages.value = []
    receiveUserInfo.value = {name: '请等待',}
  }
})

// 初始化WebSocket连接
const initWebSocket = () => {
  clearTimeout(reconnectTimer)
  store.state.connectionStatus = 'connecting'
  const token = store.state.token

  try {
    socket.value = new WebSocket(`${import.meta.env.VITE_BACKEND_WebSocket}/imserverSingle?token=${token}`)

    socket.value.onopen = () => {
      store.state.connectionStatus = 'connected'
      retryCount = 0
    }

    socket.value.onmessage = async (event) => {
      const serverMsg = JSON.parse(event.data)
      const index = messages.value.findIndex(msg => msg.tempId === serverMsg.tempId)
      if (index > -1) {
        messages.value[index].status = 'success'
        messages.value[index].serverTime = serverMsg.time
      } else {
        console.log(serverMsg)
        if((serverMsg.sendUserId === 0 )&& (serverMsg.status === 1) ){
          endOrder(serverMsg.businessId);
        }
        if (isVisible.value) {
          if (serverMsg.businessId === businessId.value) {
          messages.value.push(serverMsg)
          await nextTick()
          const wrap = scrollbarRef.value?.$el.querySelector('.el-scrollbar__wrap')
          if (wrap) {
            wrap.scrollTop = wrap.scrollHeight
          }
          } else {
            await showNewMessageNotification(serverMsg)
          }
          onOpen.play()
        }
        if (!isVisible.value && serverMsg.sendUserId !== sendUserId.value) {
          await showNewMessageNotification(serverMsg)
          onClose.play()
        }
      }
    }

    socket.value.onclose = (event) => {
      store.state.connectionStatus = 'disconnected'
      if (!event.wasClean && retryCount < MAX_RETRY) {
        scheduleReconnect()
      }
    }

    socket.value.onerror = () => {
      store.state.connectionStatus = 'disconnected'
      socket.value?.close()
    }
  } catch (error) {
    store.state.connectionStatus =  'disconnected'
    scheduleReconnect()
  }
}

// 显示新消息通知
const showNewMessageNotification = async (message) => {
  const userInfo = await getUserInfo(message.sendUserId)
  ElNotification({
    title: '服务信息',
    message: `${userInfo.name}: ${message.content.slice(0, 20)}...`,
    onClick: async () => {
      store.commit('SET_BUSINESS', {
        acceptExpertId: null,
        applyUserId: null,
      })
      const businessInfo = await checkBusinessCommunicate(message.businessId);
      store.commit('SET_BUSINESS', businessInfo)
      if (businessInfo.status === 0) {
        store.state.expert.input = {
          inputIsVisible: true,
          hiddenReasons: "",
        }
      }
      if (businessInfo.status === 1) {
        store.state.expert.input = {
          inputIsVisible: false,
          hiddenReasons: "订单已经结束，有疑问请联系客服",
        }
      }
    }
  })
}
const checkBusinessCommunicate = (businessId) => {
  // 返回整个 Promise 链
  return axios.get(
      `${import.meta.env.VITE_BACKEND_IP}/api/order/get-businessById`,
      {
        params: {id: businessId},
        headers: {'token': store.state.token}
      }
  )
      .then(response => {
        if (response.data.code === 200) {
          return response.data.data
        } else if (response.data.code === 404) {
          ElMessage.error('Error！')
          return null
        }
      })
      .catch(error => {
        ElMessage.error(`${error.message}`)
        return null
      })
}

// 关闭连接（仅在组件卸载时）
const closeConnection = () => {
  if (socket.value) {
    socket.value.close()
    socket.value = null
  }
  clearTimeout(reconnectTimer)
}

// 自动重连逻辑
const scheduleReconnect = () => {
  if (retryCount < MAX_RETRY) {
    retryCount++
    reconnectTimer = setTimeout(initWebSocket, Math.min(5000 * retryCount, 30000))
  }
}

// 手动重连
const manualReconnect = () => {
  retryCount = 0
  initWebSocket()
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim()) return

  const tempId = Date.now()
  const newMsg = {
    tempId,
    sendUserId: sendUserId.value,
    receiveUserId: receiveUserId.value,
    businessId: businessId.value,
    content: inputMessage.value,
    time: Date.now()
  }

  messages.value.push(newMsg)

  try {
    socket.value.send(JSON.stringify({
      ...newMsg,
      status: undefined
    }))
    await nextTick()
    const wrap = scrollbarRef.value?.$el.querySelector('.el-scrollbar__wrap')
    if (wrap) {
      wrap.scrollTop = wrap.scrollHeight
    }
  } catch (error) {
    newMsg.status = 'failed'
    messages.value = [...messages.value]
  }

  inputMessage.value = ''
}

// 重新发送消息
const resendMessage = (msg) => {
  msg.status = 'sending'
  try {
    socket.value.send(JSON.stringify(msg))
  } catch (error) {
    msg.status = 'failed'
  }
}

// 关闭弹窗
const closeDialog = () => {
  store.commit('SET_BUSINESS', false)
}
//订单结束动作
const endOrder = (businessId1) => {
  EventBus.emit('endOrder1');
  if (businessId.value === businessId1) {
    store.state.expert.input.inputIsVisible = false;
    store.state.expert.input.hiddenReasons = "订单已经结束，有疑问请联系客服";
  }
}
const getUserInfo = async (userId) => {
  try {
    const response = await axios.get(
        `${import.meta.env.VITE_BACKEND_IP}/api/user/get-info`,
        {params: {userId}}
    )
    return response.data.data
  } catch (error) {
    return "ERROR"
  }
}


// 文件上传
const addFileDialogVisible = ref(false)
const openaddFileDialog = () => {
  addFileDialogVisible.value = true
}
const uploadFileLoading = ref(false)

async function uploadFile() {
  console.log('[1/4] 启动文件上传流程');
  // 创建文件输入元素
  const fileInput = document.createElement('input');
  fileInput.type = 'file';
  fileInput.style.display = 'none';
  document.body.appendChild(fileInput);

  try {
    // 触发文件选择
    console.log('[2/4] 打开文件选择器');
    let file = null;
    try {
      fileInput.click();

      // 等待文件选择
      file = await new Promise((resolve, reject) => {
        fileInput.onchange = () => resolve(fileInput.files[0]);
        fileInput.oncancel = () => reject('用户取消选择');
      });

      console.log('[选择文件]', file ? file.name : '无文件');
    }catch (e){
      throw new Error('未选择文件');
    }

    // 基础验证
    if (!file)
      throw new Error('未选择文件');
    const maxSize = 50 * 1024 * 1024 // 50MB
    if (file.size > maxSize) throw new Error('件大小不能超过50MB!');

    // 准备上传数据
    const formData = new FormData();
    formData.append('file', file);
    console.log('[3/4] 开始上传文件', file.name);

    // 使用axios发送请求
    const response = await axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/files/upload`, formData, {
      headers: {'Content-Type': 'multipart/form-data'},
      timeout: 10000
    });
    if(response.data.code===200){
      console.log('[4/4] 上传完成', response.data);
      return response.data;
    }else{
      throw new Error(response.data.data);
    }
  } catch (error) {
    console.error('[上传中断]', error.message);
    throw error; // 抛出错误供外部处理
  } finally {
    // 清理资源
    document.body.removeChild(fileInput);
    console.log('[清理] 移除临时元素');
  }
}
async function handleUpload() {
  try {
    uploadFileLoading.value = true
    const result = await uploadFile();
    console.log('上传成功结果:', result);
    const fileId = result.data

    if (fileId) {
      inputMessage.value += `\${fileid=${fileId}}%`
      ElMessage.success('文件上传成功')
      await sendMessage();
      uploadFileLoading.value = false
      addFileDialogVisible.value = false
    } else {
      ElMessage.error('上传失败')
    }
  } catch (error) {
    uploadFileLoading.value = false
    ElMessage.error(error.message)
  }
}

// 文件标签点击处理
const handleFileTagClick = (fileId) => {
  // 1. 创建隐藏的iframe（兼容所有浏览器）
  const iframe = document.createElement('iframe')
  iframe.style.display = 'none'


  iframe.src = `${import.meta.env.VITE_BACKEND_IP}/api/files/download/${fileId}`
  document.body.appendChild(iframe)

  setTimeout(() => {
    document.body.removeChild(iframe)
  }, 5000)

  // 6. 显示下载提示（可选）
  console.log(`开始下载文件 ${fileId}`)
  ElMessage.info('下载已开始，请查看浏览器下载列表')
}


//处理聊天框的显示
const parseMessageContent = (content) => {
  const regex = /\${fileid=(\d+)}%/g
  const segments = []
  let lastIndex = 0
  let match

  while ((match = regex.exec(content)) !== null) {
    // 添加前面的普通文本
    if (match.index > lastIndex) {
      segments.push({
        type: 'text',
        content: content.slice(lastIndex, match.index)
      })
    }

    // 添加文件标签
    segments.push({
      type: 'file',
      id: parseInt(match[1]),
      raw: match[0]
    })

    lastIndex = match.index + match[0].length
  }

  // 添加剩余文本
  if (lastIndex < content.length) {
    segments.push({
      type: 'text',
      content: content.slice(lastIndex)
    })
  }

  return segments
}
const timeVisible = ref(false)
const formatTime = (timestamp) =>  {
  const msgDate = new Date(timestamp)
  const now = new Date()
  const currentYear = now.getFullYear()

  // 辅助函数：补零操作
  const pad = n => n.toString().padStart(2, '0')

  // 判断年份
  if (msgDate.getFullYear() < currentYear) {
    return `${msgDate.getFullYear()}-${pad(msgDate.getMonth() + 1)}-${pad(msgDate.getDate())}`
  }

  // 判断是否同一天
  const isSameDay = now.toDateString() === msgDate.toDateString()

  return isSameDay
      ? `${pad(msgDate.getHours())}:${pad(msgDate.getMinutes())}`
      : `${pad(msgDate.getMonth() + 1)}-${pad(msgDate.getDate())}`
}
const formatFullTime = (timestamp) => {
  const msgDate = new Date(timestamp)
  const pad = n => n.toString().padStart(2, '0')

  // 直接返回完整时间格式
  return `${msgDate.getFullYear()}-${pad(msgDate.getMonth() + 1)}-${pad(msgDate.getDate())} ` +
      `${pad(msgDate.getHours())}:${pad(msgDate.getMinutes())}:${pad(msgDate.getSeconds())}`
}

onUnmounted(closeConnection)
</script>

<template>
  <el-dialog
      :model-value="isVisible"
      @update:model-value="closeDialog"
      :title="receiveUserInfo.name"
      :close-on-click-modal="false"
      class="chat-dialog"
      :fullscreen="isMobile"
      draggable
      width="700px"
  >
    <div class="chat-container">
      <el-alert
          :title="{
          connecting: '正在连接服务器...',
          connected: '连接成功',
          disconnected: '连接已断开'
        }[connectionStatus]"
          :type="{
          connecting: 'info',
          connected: 'success',
          disconnected: 'error'
        }[connectionStatus]"
          :closable="false"
          show-icon
          class="status-bar"
      >
        <template #icon>
          <el-icon v-if="connectionStatus === 'connecting'">
            <Loading class="spin-icon"/>
          </el-icon>
          <el-icon v-else>
            <Connection/>
          </el-icon>
        </template>

        <el-button
            v-if="connectionStatus === 'disconnected'"
            type="danger"
            size="small"
            :icon="RefreshRight"
            @click="manualReconnect"
            class="reconnect-btn"
        >
          重新连接
        </el-button>
      </el-alert>

      <el-scrollbar ref="scrollbarRef" class="main-area">
        <div
            v-for="(msg, index) in messages"
            :key="index"
            :class="['message', msg.sendUserId === sendUserId ? 'sent' : 'received']"
        >
          <div class="message-content">
            <!-- 消息内容显示 -->
            <div class="message-text">
              <template v-for="(segment, segIndex) in parseMessageContent(msg.content)" :key="segIndex">
                <!-- 普通文本 -->
                <span v-if="segment.type === 'text'">{{ segment.content }}</span>

                <!-- 文件标签 -->
                <el-tag
                    v-else
                    class="file-tag"
                    :type="msg.sendUserId === sendUserId ? 'primary' : 'info'"
                    @click="handleFileTagClick(segment.id)"
                >
                  <el-icon>
                    <Document/>
                  </el-icon>
                  <span class="file-name">文件#{{ segment.id }}</span>
                </el-tag>
              </template>
            </div>

            <!-- 原有状态显示 -->
            <div class="message-status">
              <span class="time" @click="timeVisible=true" v-if="!timeVisible" style="cursor: pointer">{{ formatTime(msg.time) }}</span>
              <span class="time" @click="timeVisible=false" v-else style="cursor: pointer">{{ formatFullTime(msg.time) }}</span>
              <template v-if="msg.sendUserId === sendUserId">
                <el-icon v-if="msg.status === 'sending'" class="loading-icon">
                  <Loading/>
                </el-icon>
                <span v-else-if="msg.status === 'failed'" class="failed-tip">
                发送失败
                <el-button type="danger" size="small" @click="resendMessage(msg)">
                 重试
                </el-button>
              </span>
              </template>
            </div>
          </div>
        </div>
      </el-scrollbar>

      <div v-if="inputIsVisible" class="input-area">
        <el-input
            v-model="inputMessage"
            :autosize="{ minRows: 1, maxRows: 4 }"
            placeholder="输入消息..."
            resize="none"
            type="textarea"
            @keyup.enter="sendMessage"
        />
        <el-button @click="openaddFileDialog">
          <el-icon>
            <DocumentAdd/>
          </el-icon>
        </el-button>
        <el-button
            type="primary"
            @click="sendMessage"
            :disabled="connectionStatus !== 'connected'"
        >
          发送
        </el-button>
      </div>
      <div v-else class="input-area">
        <h2>{{ hiddenReasons }}</h2>
      </div>
    </div>
    <!--     Upload file inner dialog-->
    <el-dialog
        v-model="addFileDialogVisible"
        title="添加文件"
        append-to-body
        width="200"
        align-center
    >
      <el-button @click="handleUpload()" v-loading="uploadFileLoading">
        <el-icon>
          <DocumentAdd/>
        </el-icon>
        点击上传
      </el-button>
    </el-dialog>
  </el-dialog>
</template>

<style scoped>
.chat-dialog {
  --el-dialog-padding-primary: 0;
}

/* PC 及大屏设备 */
.chat-container {
  display: flex;
  flex-direction: column;
  height: 70vh;
}

.status-bar {
  margin: -6px 0 10px 0;
}

.main-area {
  flex: 1;
  padding: 0 20px;
}

.message {
  margin-bottom: 15px;
  max-width: 75%;
}

.message.sent {
  margin-left: auto;
}

.message.received {
  margin-right: auto;
}


.message-content {
  padding: 12px 16px;
  border-radius: 4px;
  background: var(--el-color-primary-light-9);
}


.message.sent .message-content {
  background: var(--el-color-primary);
  color: white;
}

.message-status {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
  font-size: 12px;
}

.time {
  opacity: 0.8;
}

.input-area {
  padding: 16px 20px;
  border-top: 1px solid var(--el-border-color);
  display: flex;
  gap: 12px;
}

.spin-icon,
.loading-icon {
  animation: icon-spin 1.5s linear infinite;
}

@keyframes icon-spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.failed-tip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--el-color-danger);
}

/* 移动端样式 */
@media (max-width: 768px) {
  .chat-dialog {
    top: 0;
    margin: 0;
    padding: 0;
  }

  .chat-container {
    height: calc(100vh - 72px);
  }

  .main-area {
    padding: 0 10px;
    margin-bottom: 30px; /* 避免被固定的输入区遮挡 */
  }

  .input-area {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 10px 20px;
    background-color: #fff;
    z-index: 10;
    border-top: 1px solid var(--el-border-color);
  }
}



.file-tag {
  margin: 2px;
  vertical-align: middle;
}

.file-tag :deep(.el-tag__content) {
  display: flex;
  align-items: center;
}



/* 文件标签样式 */
.message-text .file-tag {
  cursor: pointer;
  margin: 2px;
  vertical-align: baseline;
  transition: all 0.2s;
}

.message-text .file-tag:hover {
  opacity: 0.8;
  transform: translateY(-1px);
}

.file-name {
  margin-left: 6px;
}

/* 适配消息背景色 */
.sent .file-tag {
  background-color: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary-light-7);
}

.received .file-tag {
  background-color: var(--el-color-info-light-9);
  border-color: var(--el-color-info-light-7);
}
</style>
