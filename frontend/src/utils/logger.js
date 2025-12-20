/**
 * 前端日志工具类
 * 支持不同级别的日志记录，可发送到后端或存储在本地
 */

const LOG_LEVELS = {
  DEBUG: 0,
  INFO: 1,
  WARN: 2,
  ERROR: 3,
};

class Logger {
  constructor() {
    this.logLevel = LOG_LEVELS.INFO; // 默认日志级别
    this.enableConsole = true; // 是否输出到控制台
    this.enableStorage = true; // 是否存储到localStorage
    this.enableServer = true; // 是否发送到服务器
    this.maxStorageSize = 1000; // localStorage最大存储条数
    this.storageKey = "bscm_logs";
  }

  /**
   * 设置日志级别
   * @param {string} level - DEBUG | INFO | WARN | ERROR
   */
  setLevel(level) {
    this.logLevel = LOG_LEVELS[level] || LOG_LEVELS.INFO;
  }

  /**
   * 格式化日志消息
   */
  formatMessage(level, message, data = null) {
    const timestamp = new Date().toISOString();
    const logEntry = {
      timestamp,
      level,
      message,
      data,
      url: window.location.href,
      userAgent: navigator.userAgent,
      userId: this.getUserId(),
    };
    return logEntry;
  }

  /**
   * 获取当前用户ID
   */
  getUserId() {
    try {
      const token = localStorage.getItem("token");
      if (token) {
        // 简单解析JWT token获取用户ID（实际应该从后端获取）
        const payload = JSON.parse(atob(token.split(".")[1]));
        return payload.userId || payload.sub || null;
      }
    } catch (e) {
      // 忽略解析错误
    }
    return null;
  }

  /**
   * 输出到控制台
   */
  outputToConsole(level, formattedMessage) {
    if (!this.enableConsole) return;

    const consoleMethod =
      {
        DEBUG: console.debug,
        INFO: console.info,
        WARN: console.warn,
        ERROR: console.error,
      }[level] || console.log;

    consoleMethod(
      `[${formattedMessage.timestamp}] [${level}] ${formattedMessage.message}`,
      formattedMessage.data || "",
    );
  }

  /**
   * 存储到localStorage
   */
  storeToLocal(formattedMessage) {
    if (!this.enableStorage) return;

    try {
      let logs = JSON.parse(localStorage.getItem(this.storageKey) || "[]");
      logs.push(formattedMessage);

      // 限制存储数量
      if (logs.length > this.maxStorageSize) {
        logs = logs.slice(-this.maxStorageSize);
      }

      localStorage.setItem(this.storageKey, JSON.stringify(logs));
    } catch (e) {
      console.error("存储日志失败:", e);
    }
  }

  /**
   * 发送到服务器
   */
  async sendToServer(formattedMessage) {
    if (!this.enableServer) return;

    try {
      // 使用sendBeacon确保日志能够发送（即使页面关闭）
      const blob = new Blob([JSON.stringify(formattedMessage)], {
        type: "application/json",
      });
      navigator.sendBeacon("/api/logs/client", blob);
    } catch (e) {
      // 如果sendBeacon失败，尝试使用fetch
      try {
        await fetch("/api/logs/client", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(formattedMessage),
          keepalive: true,
        });
      } catch (err) {
        // 静默失败，避免影响用户体验
        console.debug("发送日志到服务器失败:", err);
      }
    }
  }

  /**
   * 记录日志
   */
  async log(level, message, data = null) {
    if (LOG_LEVELS[level] < this.logLevel) return;

    const formattedMessage = this.formatMessage(level, message, data);

    // 输出到控制台
    this.outputToConsole(level, formattedMessage);

    // 存储到localStorage
    this.storeToLocal(formattedMessage);

    // 发送到服务器（异步，不阻塞）
    if (level === "ERROR" || level === "WARN") {
      this.sendToServer(formattedMessage);
    }
  }

  /**
   * 调试日志
   */
  debug(message, data = null) {
    return this.log("DEBUG", message, data);
  }

  /**
   * 信息日志
   */
  info(message, data = null) {
    return this.log("INFO", message, data);
  }

  /**
   * 警告日志
   */
  warn(message, data = null) {
    return this.log("WARN", message, data);
  }

  /**
   * 错误日志
   */
  error(message, data = null) {
    return this.log("ERROR", message, data);
  }

  /**
   * 记录API请求
   */
  logApiRequest(config) {
    this.info("API请求", {
      method: config.method,
      url: config.url,
      baseURL: config.baseURL,
      params: config.params,
      data: this.sanitizeData(config.data),
    });
  }

  /**
   * 记录API响应
   */
  logApiResponse(response) {
    this.info("API响应", {
      status: response.status,
      statusText: response.statusText,
      url: response.config?.url,
      data: this.sanitizeData(response.data),
    });
  }

  /**
   * 记录API错误
   */
  logApiError(error) {
    const errorData = {
      message: error.message,
      url: error.config?.url,
      method: error.config?.method,
      status: error.response?.status,
      statusText: error.response?.statusText,
      responseData: this.sanitizeData(error.response?.data),
    };
    this.error("API错误", errorData);
  }

  /**
   * 清理敏感数据
   */
  sanitizeData(data) {
    if (!data) return data;
    if (typeof data !== "object") return data;

    const sensitiveFields = [
      "password",
      "token",
      "authorization",
      "secret",
      "key",
    ];
    const sanitized = { ...data };

    for (const field of sensitiveFields) {
      if (sanitized[field]) {
        sanitized[field] = "***";
      }
    }

    return sanitized;
  }

  /**
   * 获取存储的日志
   */
  getStoredLogs() {
    try {
      return JSON.parse(localStorage.getItem(this.storageKey) || "[]");
    } catch (e) {
      return [];
    }
  }

  /**
   * 清空存储的日志
   */
  clearStoredLogs() {
    localStorage.removeItem(this.storageKey);
  }

  /**
   * 导出日志
   */
  exportLogs() {
    const logs = this.getStoredLogs();
    const blob = new Blob([JSON.stringify(logs, null, 2)], {
      type: "application/json",
    });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `bscm-logs-${new Date().toISOString().split("T")[0]}.json`;
    a.click();
    URL.revokeObjectURL(url);
  }
}

// 创建单例实例
const logger = new Logger();

// 在开发环境下设置为DEBUG级别
if (import.meta.env.DEV) {
  logger.setLevel("DEBUG");
}

export default logger;
