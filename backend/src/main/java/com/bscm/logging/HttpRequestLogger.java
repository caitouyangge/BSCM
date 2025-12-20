package com.bscm.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** HTTP请求日志记录器 */
@Component
public class HttpRequestLogger {

  private static final Logger logger = LoggerFactory.getLogger(HttpRequestLogger.class);

  /** 记录HTTP请求 */
  public void logRequest(HttpServletRequest request, Object body) {
    try {
      Map<String, Object> logData = new HashMap<>();
      logData.put("method", request.getMethod());
      logData.put("uri", request.getRequestURI());
      logData.put("queryString", request.getQueryString());
      logData.put("remoteAddr", getClientIpAddress(request));
      logData.put("userAgent", request.getHeader("User-Agent"));
      logData.put("contentType", request.getContentType());
      logData.put("contentLength", request.getContentLength());

      // 记录请求头（排除敏感信息）
      Map<String, String> headers = new HashMap<>();
      Enumeration<String> headerNames = request.getHeaderNames();
      while (headerNames.hasMoreElements()) {
        String headerName = headerNames.nextElement();
        String headerValue = request.getHeader(headerName);
        // 隐藏敏感信息
        if (headerName.toLowerCase().contains("authorization")) {
          headers.put(headerName, maskSensitiveData(headerValue));
        } else {
          headers.put(headerName, headerValue);
        }
      }
      logData.put("headers", headers);

      // 记录请求体（如果有）
      if (body != null) {
        logData.put("body", sanitizeRequestBody(body));
      }

      logger.info(
          "HTTP请求: {} {} | IP: {} | {}",
          request.getMethod(),
          request.getRequestURI(),
          getClientIpAddress(request),
          formatLogData(logData));
    } catch (Exception e) {
      logger.error("记录HTTP请求日志失败", e);
    }
  }

  /** 记录HTTP响应 */
  public void logResponse(
      HttpServletRequest request, HttpServletResponse response, Object body, long duration) {
    try {
      Map<String, Object> logData = new HashMap<>();
      logData.put("status", response.getStatus());
      logData.put("duration", duration + "ms");
      logData.put("contentType", response.getContentType());

      // 记录响应体（如果有，且不是文件）
      if (body != null && !(body instanceof byte[])) {
        logData.put("body", sanitizeResponseBody(body));
      }

      logger.info(
          "HTTP响应: {} {} | 状态: {} | 耗时: {}ms",
          request.getMethod(),
          request.getRequestURI(),
          response.getStatus(),
          duration);
    } catch (Exception e) {
      logger.error("记录HTTP响应日志失败", e);
    }
  }

  /** 记录HTTP错误 */
  public void logError(HttpServletRequest request, Exception exception, long duration) {
    try {
      Map<String, Object> logData = new HashMap<>();
      logData.put("error", exception.getClass().getName());
      logData.put("message", exception.getMessage());
      logData.put("duration", duration + "ms");

      logger.error(
          "HTTP错误: {} {} | 异常: {} | 消息: {} | 耗时: {}ms",
          request.getMethod(),
          request.getRequestURI(),
          exception.getClass().getSimpleName(),
          exception.getMessage(),
          duration);
    } catch (Exception e) {
      logger.error("记录HTTP错误日志失败", e);
    }
  }

  /** 获取客户端真实IP地址 */
  private String getClientIpAddress(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("X-Real-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    // 处理多个IP的情况
    if (ip != null && ip.contains(",")) {
      ip = ip.split(",")[0].trim();
    }
    return ip;
  }

  /** 隐藏敏感数据 */
  private String maskSensitiveData(String data) {
    if (data == null || data.length() < 10) {
      return "***";
    }
    return data.substring(0, 10) + "***";
  }

  /** 清理请求体中的敏感信息 */
  private Object sanitizeRequestBody(Object body) {
    if (body == null) {
      return null;
    }

    if (body instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, Object> map = (Map<String, Object>) body;
      Map<String, Object> sanitized = new HashMap<>(map);

      // 隐藏敏感字段
      String[] sensitiveFields = {"password", "token", "secret", "key", "authorization"};
      for (String field : sensitiveFields) {
        if (sanitized.containsKey(field)) {
          sanitized.put(field, "***");
        }
      }

      return sanitized;
    }

    return body;
  }

  /** 清理响应体中的敏感信息 */
  private Object sanitizeResponseBody(Object body) {
    // 响应体通常不需要特殊处理，但可以在这里添加逻辑
    return body;
  }

  /** 格式化日志数据 */
  private String formatLogData(Map<String, Object> logData) {
    StringBuilder sb = new StringBuilder();
    logData.forEach(
        (key, value) -> {
          if (sb.length() > 0) {
            sb.append(" | ");
          }
          sb.append(key).append("=").append(value);
        });
    return sb.toString();
  }
}
