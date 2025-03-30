<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const messages = ref([])
const inputMessage = ref('')
const socket = ref(null)
const sendUserId = "1"
const receiveUserId = "21"

// 格式化时间戳
const formatTime = (timestamp) => {
  return new Date(timestamp).toLocaleTimeString()
}

// 初始化 WebSocket 连接
const initWebSocket = () => {
  const token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMXwxMjN8MyIsImV4cCI6MTc0MzI2NTU1NH0.YeMmlLq97G2QDqWCRyo0vFhgoA4PK_TJFV0ozSfeadg"
  socket.value = new WebSocket(`ws://localhost:8080/imserverSingle?token=${token}`)

  socket.value.onopen = () => {
    console.log("连接成功！")
  }

  socket.value.onmessage = (event) => {
    const newMessage = JSON.parse(event.data)
    messages.value.push(newMessage)
  }

  socket.value.onclose = (event) => {
    console.log("连接关闭:", event.reason)
  }
}

// 发送消息
const sendMessage = () => {
  if (!inputMessage.value.trim()) return

  const message = {
    sendUserId,
    receiveUserId,
    content: inputMessage.value
  }

  socket.value.send(JSON.stringify(message))
  inputMessage.value = ''
}

onMounted(() => {
  initWebSocket()
})

onUnmounted(() => {
  if (socket.value) {
    socket.value.close()
  }
})
</script>

<template>
  <div class="chat-container">
    <div class="messages">
      <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['message', msg.sendUserId === sendUserId ? 'sent' : 'received']"
      >
        <div class="content">{{ msg.content }}</div>
        <div class="time">{{ formatTime(msg.time) }}</div>
      </div>
    </div>

    <div class="input-area">
      <input
          v-model="inputMessage"
          @keyup.enter="sendMessage"
          placeholder="输入消息..."
      />
      <button @click="sendMessage">发送</button>
    </div>
  </div>
</template>

<style scoped>
.chat-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f5f5f5;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  margin-bottom: 20px;
}

.message {
  margin-bottom: 15px;
  max-width: 70%;
}

.message.sent {
  margin-left: auto;
  text-align: right;
}

.message.received {
  margin-right: auto;
}

.content {
  padding: 10px 15px;
  border-radius: 15px;
  display: inline-block;
}

.sent .content {
  background-color: #409eff;
  color: white;
}

.received .content {
  background-color: #e4e7ed;
}

.time {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.input-area {
  display: flex;
  gap: 10px;
}

input {
  flex: 1;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 20px;
  outline: none;
}

button {
  padding: 12px 24px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #66b1ff;
}
</style>