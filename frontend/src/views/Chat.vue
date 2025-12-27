<template>
  <div class="chat">
    <el-container class="chat-layout">
      <!-- 头部 -->
      <el-header class="header-section">
        <el-page-header @back="goBack">
          <template #content>
            <span class="text-large font-600 mr-3">AI智能助手</span>
          </template>
        </el-page-header>
      </el-header>

      <!-- 聊天消息区域 -->
      <el-main class="chat-main" ref="chatMainRef">
        <div class="chat-messages" ref="messagesContainerRef">
          <!-- 欢迎消息 -->
          <div
            class="message-wrapper message-ai-wrapper"
            v-if="messages.length === 0"
          >
            <div class="message-avatar">
              <el-icon><Avatar /></el-icon>
            </div>
            <div class="message-bubble message-ai-bubble">
              <div class="message-content">
                您好！我是AI智能诊断助手，专注于脑干海绵状血管畸形的诊断咨询。我可以为您提供专业的医疗建议和解答相关问题。请问有什么可以帮助您的吗？
              </div>
            </div>
            <div class="message-time">{{ formatTime(Date.now()) }}</div>
          </div>

          <!-- 消息列表 -->
          <div
            v-for="(message, index) in messages"
            :key="index"
            class="message-wrapper"
            :class="
              message.role === 'user'
                ? 'message-user-wrapper'
                : 'message-ai-wrapper'
            "
          >
            <!-- AI消息：头像在左 -->
            <div class="message-avatar" v-if="message.role === 'assistant'">
              <el-icon><Avatar /></el-icon>
            </div>
            <!-- 用户消息：时间在左 -->
            <div class="message-time" v-if="message.role === 'user'">
              {{ formatTime(message.timestamp) }}
            </div>
            <!-- 消息气泡 -->
            <div
              class="message-bubble"
              :class="
                message.role === 'user'
                  ? 'message-user-bubble'
                  : 'message-ai-bubble'
              "
            >
              <div
                class="message-content"
                v-html="formatMessage(message.content)"
              ></div>
            </div>
            <!-- 用户消息：头像在右 -->
            <div class="message-avatar" v-if="message.role === 'user'">
              <el-icon><User /></el-icon>
            </div>
            <!-- AI消息：时间在右 -->
            <div class="message-time" v-if="message.role === 'assistant'">
              {{ formatTime(message.timestamp) }}
            </div>
          </div>

          <!-- 加载指示器 -->
          <div v-if="isLoading" class="message-wrapper message-ai-wrapper">
            <div class="message-avatar">
              <el-icon><Avatar /></el-icon>
            </div>
            <div class="message-bubble message-ai-bubble">
              <div class="message-content">
                <span class="typing-indicator">
                  <span></span>
                  <span></span>
                  <span></span>
                </span>
              </div>
            </div>
            <div class="message-time">{{ formatTime(Date.now()) }}</div>
          </div>
        </div>
      </el-main>

      <!-- 底部输入区域 -->
      <el-footer class="chat-footer">
        <div class="input-container">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="3"
            :maxlength="1000"
            placeholder="请输入您的问题..."
            class="chat-input"
            @keydown.enter.exact.prevent="handleSendMessage"
            @keydown.enter.shift.exact="inputMessage += '\n'"
            :disabled="isLoading"
            resize="none"
          />
          <div class="input-actions">
            <span class="char-count">{{ inputMessage.length }}/1000</span>
            <el-button
              type="primary"
              :loading="isLoading"
              :disabled="!inputMessage.trim() || isLoading"
              @click="handleSendMessage"
              class="send-button"
            >
              <el-icon v-if="!isLoading"><Promotion /></el-icon>
              <span>{{ isLoading ? "发送中..." : "发送" }}</span>
            </el-button>
          </div>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Avatar, User, Promotion } from "@element-plus/icons-vue";
import { aiChatApi } from "../api/chat";

const router = useRouter();

const messages = ref([]);
const inputMessage = ref("");
const isLoading = ref(false);
const chatMainRef = ref(null);
const messagesContainerRef = ref(null);

// 格式化消息内容（支持换行）
const formatMessage = (content) => {
  if (!content) return "";
  return content
    .replace(/\n/g, "<br>")
    .replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>")
    .replace(/\*(.*?)\*/g, "<em>$1</em>");
};

// 格式化时间
const formatTime = (timestamp) => {
  if (!timestamp) return "";
  const date = new Date(timestamp);
  const hours = date.getHours().toString().padStart(2, "0");
  const minutes = date.getMinutes().toString().padStart(2, "0");
  return `${hours}:${minutes}`;
};

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainerRef.value) {
      messagesContainerRef.value.scrollTop =
        messagesContainerRef.value.scrollHeight;
    }
  });
};

// 发送消息
const handleSendMessage = async () => {
  const message = inputMessage.value.trim();
  if (!message || isLoading.value) return;

  // 添加用户消息
  messages.value.push({
    role: "user",
    content: message,
    timestamp: Date.now(),
  });

  inputMessage.value = "";
  scrollToBottom();

  // 准备AI消息
  let aiMessageIndex = messages.value.length;
  messages.value.push({
    role: "assistant",
    content: "",
    timestamp: Date.now(),
  });

  isLoading.value = true;

  try {
    // 构建消息历史（只包含最近的对话，最多保留10轮）
    const recentMessages = messages.value
      .slice(0, -1)
      .slice(-20)
      .map((msg) => ({
        role: msg.role === "user" ? "user" : "assistant",
        content: msg.content,
      }));

    // 添加系统提示词
    const systemPrompt = {
      role: "system",
      content:
        "你是一位专业的AI医疗诊断助手，专注于脑干海绵状血管畸形的诊断咨询。请用专业但易懂的语言回答问题，提供准确的医疗建议。如果问题超出你的专业范围，请如实告知。",
    };

    const chatMessages = [systemPrompt, ...recentMessages];

    // 发送消息并获取回复
    try {
      const result = await aiChatApi.sendMessage(chatMessages);
      if (messages.value[aiMessageIndex]) {
        // Result格式: { code: 200, data: "...", message: "..." }
        if (result.code === 200) {
          messages.value[aiMessageIndex].content = result.data || "";
        } else {
          messages.value[aiMessageIndex].content =
            `[错误: ${result.message || "未知错误"}]`;
          ElMessage.error(result.message || "AI回复失败");
        }
        messages.value[aiMessageIndex].timestamp = Date.now();
      }
    } catch (error) {
      console.error("发送消息失败:", error);
      if (messages.value[aiMessageIndex]) {
        messages.value[aiMessageIndex].content =
          `[错误: ${error.message || "请求失败"}]`;
        messages.value[aiMessageIndex].timestamp = Date.now();
      }
      ElMessage.error("发送失败，请稍后重试");
      throw error;
    }
  } catch (error) {
    console.error("发送消息失败:", error);
    ElMessage.error("发送失败，请稍后重试");

    // 移除失败的AI消息
    if (
      messages.value[aiMessageIndex] &&
      !messages.value[aiMessageIndex].content
    ) {
      messages.value.splice(aiMessageIndex, 1);
    } else if (messages.value[aiMessageIndex]) {
      messages.value[aiMessageIndex].content +=
        "\n\n[抱歉，AI响应出错，请重试]";
    }
  } finally {
    isLoading.value = false;
    scrollToBottom();
  }
};

// 返回
const goBack = () => {
  router.back();
};

onMounted(() => {
  scrollToBottom();
});
</script>

<style scoped>
.chat {
  min-height: 100vh;
  background: #ededed;
}

.chat-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #ededed;
}

/* 头部 */
.header-section {
  background: var(--primary-color);
  color: white;
  padding: 0;
  box-shadow: 0 2px 8px rgba(150, 120, 217, 0.2);
  height: 60px;
  display: flex;
  align-items: center;
}

.header-section :deep(.el-page-header__content) {
  color: white;
}

.header-section :deep(.el-page-header__left) {
  color: white;
}

.header-section :deep(.el-page-header__left:hover) {
  color: rgba(255, 255, 255, 0.8);
}

/* 聊天消息区域 */
.chat-main {
  flex: 1;
  padding: 10px 16px;
  overflow: hidden;
  background: #ededed;
}

.chat-messages {
  height: 100%;
  overflow-y: auto;
  padding-bottom: 10px;
  scroll-behavior: smooth;
}

/* 消息包装器 - 微信风格 */
.message-wrapper {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
  animation: fadeIn 0.3s ease-in;
  position: relative;
  width: 100%;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-user-wrapper {
  justify-content: flex-end;
  flex-direction: row;
}

.message-user-wrapper .message-bubble {
  margin-left: 8px;
  margin-right: 8px;
}

.message-ai-wrapper {
  justify-content: flex-start;
  flex-direction: row;
}

.message-ai-wrapper .message-bubble {
  margin-left: 8px;
  margin-right: 8px;
}

/* 头像 - 微信风格圆形 */
.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 20px;
  margin: 0 8px;
  background: var(--primary-color);
  color: white;
}

.message-ai-wrapper .message-avatar {
  background: var(--primary-lighter);
  color: var(--primary-color);
}

.message-user-wrapper .message-avatar {
  background: var(--primary-color);
  color: white;
}

/* 消息气泡 - 微信风格 */
.message-bubble {
  max-width: 70%;
  min-width: 60px;
  position: relative;
  word-wrap: break-word;
  word-break: break-word;
  padding: 10px 14px;
  border-radius: 6px;
}

/* 用户消息气泡 - 右侧紫色 */
.message-user-bubble {
  background: var(--primary-color);
  color: white;
  border-radius: 6px 6px 0 6px;
  box-shadow: 0 1px 2px rgba(150, 120, 217, 0.3);
}

/* AI消息气泡 - 左侧浅紫色 */
.message-ai-bubble {
  background: #f0e6ff;
  color: var(--text-primary);
  border-radius: 6px 6px 6px 0;
  border: 1px solid rgba(150, 120, 217, 0.15);
  box-shadow: 0 1px 2px rgba(150, 120, 217, 0.15);
}

/* 消息内容 */
.message-content {
  font-size: 15px;
  line-height: 1.5;
  word-break: break-word;
}

.message-content :deep(strong) {
  font-weight: 600;
}

.message-content :deep(em) {
  font-style: italic;
}

.message-user-bubble .message-content {
  color: white;
}

.message-ai-bubble .message-content {
  color: var(--text-primary);
}

/* 时间戳 - 显示在消息旁边，微信风格 */
.message-time {
  font-size: 11px;
  color: #999;
  align-self: flex-end;
  margin: 0 4px;
  white-space: nowrap;
  padding-bottom: 4px;
  line-height: 1;
  flex-shrink: 0;
}

/* 打字指示器 - 紫色 */
.typing-indicator {
  display: inline-flex;
  gap: 4px;
  align-items: center;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--primary-color);
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) {
  animation-delay: 0s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%,
  60%,
  100% {
    transform: translateY(0);
    opacity: 0.5;
  }
  30% {
    transform: translateY(-8px);
    opacity: 1;
  }
}

/* 底部输入区域 - 微信风格，紫色主题 */
.chat-footer {
  background: #ededed;
  border-top: 1px solid rgba(150, 120, 217, 0.15);
  padding: 12px 16px;
  height: auto;
  box-shadow: 0 -1px 4px rgba(150, 120, 217, 0.08);
}

.input-container {
  max-width: 1200px;
  margin: 0 auto;
}

.chat-input {
  margin-bottom: 8px;
}

.chat-input :deep(.el-textarea__inner) {
  border: 1px solid rgba(150, 120, 217, 0.3);
  border-radius: 8px;
  font-size: 15px;
  padding: 12px;
  resize: none;
  background: white;
  color: var(--text-primary);
}

.chat-input :deep(.el-textarea__inner:focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(150, 120, 217, 0.1);
}

.chat-input :deep(.el-textarea__inner::placeholder) {
  color: #999;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.char-count {
  font-size: 12px;
  color: var(--primary-color);
}

.send-button {
  padding: 10px 24px;
  border-radius: 8px;
  font-weight: 500;
  background: var(--primary-color);
  border-color: var(--primary-color);
}

.send-button:hover {
  background: var(--primary-light);
  border-color: var(--primary-light);
}

.send-button:disabled {
  opacity: 0.5;
  background: var(--primary-color);
  border-color: var(--primary-color);
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .chat-main {
    padding: 8px 12px;
  }

  .message-bubble {
    max-width: 75%;
  }

  .message-avatar {
    width: 36px;
    height: 36px;
    font-size: 18px;
    margin: 0 6px;
  }

  .message-content {
    font-size: 14px;
  }

  .message-time {
    font-size: 10px;
  }

  .chat-footer {
    padding: 10px 12px;
  }

  .chat-input :deep(.el-textarea__inner) {
    font-size: 14px;
    padding: 10px;
  }
}

/* 滚动条样式 - 紫色 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(150, 120, 217, 0.3);
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: rgba(150, 120, 217, 0.5);
}
</style>
