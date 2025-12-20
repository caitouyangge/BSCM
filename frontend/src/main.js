import { createApp } from "vue";
import { createPinia } from "pinia";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
import App from "./App.vue";
import router from "./router";
import "./utils/rem"; // 引入rem适配
import { isMobile, disableZoom } from "./utils/mobile";
import logger from "./utils/logger";

const app = createApp(App);
const pinia = createPinia();

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

// 移动端禁用缩放
if (isMobile()) {
  disableZoom();
}

// 初始化日志系统
logger.info("应用启动", {
  userAgent: navigator.userAgent,
  url: window.location.href,
  timestamp: new Date().toISOString(),
});

// 全局错误处理
window.addEventListener("error", (event) => {
  logger.error("全局错误", {
    message: event.message,
    filename: event.filename,
    lineno: event.lineno,
    colno: event.colno,
    error: event.error?.stack,
  });
});

window.addEventListener("unhandledrejection", (event) => {
  logger.error("未处理的Promise拒绝", {
    reason: event.reason,
    promise: event.promise,
  });
});

app.use(pinia);
app.use(router);
app.use(ElementPlus);

app.mount("#app");
