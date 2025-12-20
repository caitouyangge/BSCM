package com.bscm.config;

import com.bscm.logging.HttpRequestLogger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/** HTTP请求日志拦截器 */
@Component
@RequiredArgsConstructor
public class LoggingInterceptor implements HandlerInterceptor {

  private final HttpRequestLogger httpRequestLogger;
  private static final ThreadLocal<Long> startTime = new ThreadLocal<>();

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    startTime.set(System.currentTimeMillis());

    // 记录请求日志（不读取body，避免性能问题）
    try {
      httpRequestLogger.logRequest(request, null);
    } catch (Exception e) {
      // 忽略日志记录错误，避免影响业务
    }

    return true;
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView) {
    // 可以在这里记录额外的信息
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    try {
      Long start = startTime.get();
      if (start != null) {
        long duration = System.currentTimeMillis() - start;

        if (ex != null) {
          // 记录错误日志
          httpRequestLogger.logError(request, ex, duration);
        } else {
          // 记录响应日志（不读取body，避免性能问题）
          httpRequestLogger.logResponse(request, response, null, duration);
        }
      }
    } catch (Exception e) {
      // 忽略日志记录错误
    } finally {
      startTime.remove();
    }
  }
}
