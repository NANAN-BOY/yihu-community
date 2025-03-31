<script setup>
import {ref, watch, onUnmounted, computed, onMounted, nextTick} from 'vue'
import {ElDialog, ElScrollbar, ElInput, ElButton, ElAlert, ElIcon, ElNotification} from 'element-plus'
import {Connection, Loading, RefreshRight} from '@element-plus/icons-vue'
import store from "../../store";
import axios from "axios";

// 状态管理
const isVisible = computed(() => store.getters.business.acceptExpertId)
const messages = ref([])
const inputMessage = ref('')
const socket = ref(null)
const connectionStatus = ref('disconnected')
let reconnectTimer = null
const MAX_RETRY = 5
let retryCount = 0
const scrollbarRef = ref(null)
const isMobile = ref(window.innerWidth < 768)

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
  } else {
    messages.value = []
  }
})

// 初始化WebSocket连接
const initWebSocket = () => {
  clearTimeout(reconnectTimer)
  connectionStatus.value = 'connecting'
  const token = store.state.token

  try {
    socket.value = new WebSocket(`${import.meta.env.VITE_BACKEND_WebSocket}/imserverSingle?token=${token}`)

    socket.value.onopen = () => {
      connectionStatus.value = 'connected'
      retryCount = 0
    }

    socket.value.onmessage = async (event) => {
      const serverMsg = JSON.parse(event.data)
      const index = messages.value.findIndex(msg => msg.tempId === serverMsg.tempId)
      if (index > -1) {
        messages.value[index].status = 'success'
        messages.value[index].serverTime = serverMsg.time
      } else {
        if (isVisible.value) {
          messages.value.push(serverMsg)
          await nextTick()
          const wrap = scrollbarRef.value?.$el.querySelector('.el-scrollbar__wrap')
          if (wrap) {
            wrap.scrollTop = wrap.scrollHeight
          }
        }
        if (!isVisible.value && serverMsg.sendUserId !== sendUserId.value) {
          await showNewMessageNotification(serverMsg)
        }
      }
    }

    socket.value.onclose = (event) => {
      connectionStatus.value = 'disconnected'
      if (!event.wasClean && retryCount < MAX_RETRY) {
        scheduleReconnect()
      }
    }

    socket.value.onerror = () => {
      connectionStatus.value = 'disconnected'
      socket.value?.close()
    }
  } catch (error) {
    connectionStatus.value = 'disconnected'
    scheduleReconnect()
  }
}

// 显示新消息通知
const showNewMessageNotification = async (message) => {
  const userInfo = await getUserInfo(message.sendUserId)
  ElNotification({
    title: '服务信息',
    message: `${userInfo.name}: ${message.content}`,
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

onUnmounted(closeConnection)
</script>

<template>
  <el-dialog
      :model-value="isVisible"
      @update:model-value="closeDialog"
      :title="receiveUserId"
      :close-on-click-modal="false"
      class="chat-dialog"
      :fullscreen="isMobile"
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
            <div class="message-text">{{ msg.content }}</div>
            <div class="message-status">
              <span class="time">{{ new Date(msg.time).toLocaleTimeString() }}</span>
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

      <div class="input-area">
        <el-input
            v-model="inputMessage"
            placeholder="输入消息..."
            @keyup.enter="sendMessage"
            resize="none"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 4 }"
        />
        <el-button
            type="primary"
            @click="sendMessage"
            :disabled="connectionStatus !== 'connected'"
            class="send-btn"
        >
          发送
        </el-button>
      </div>
    </div>
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

/* 按钮高度与 PC 保持一致 */
.send-btn {
  margin-left: 10px;
  height: 60px;
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
    width: 100% !important;
    height: 100vh;
    top: 0;
    margin: 0;
    padding: 0;
  }

  .chat-container {
    height: calc(100vh - 60px);
  }

  .main-area {
    padding: 0 10px;
    margin-bottom: 70px; /* 避免被固定的输入区遮挡 */
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

  .send-btn {
    height: 50px;
    font-size: 14px;
  }
}
</style>
