package com.bscm.logging;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** 业务日志记录器 用于记录业务操作相关的日志 */
@Component
public class BusinessLogger {

  private static final Logger logger = LoggerFactory.getLogger(BusinessLogger.class);

  /** 记录用户操作 */
  public void logUserOperation(String operation, Long userId, String details) {
    logger.info("用户操作: {} | 用户ID: {} | 详情: {}", operation, userId, details);
  }

  /** 记录用户操作（带额外数据） */
  public void logUserOperation(
      String operation, Long userId, String details, Map<String, Object> extraData) {
    logger.info("用户操作: {} | 用户ID: {} | 详情: {} | 额外数据: {}", operation, userId, details, extraData);
  }

  /** 记录业务事件 */
  public void logBusinessEvent(String event, String details) {
    logger.info("业务事件: {} | 详情: {}", event, details);
  }

  /** 记录业务事件（带额外数据） */
  public void logBusinessEvent(String event, String details, Map<String, Object> extraData) {
    logger.info("业务事件: {} | 详情: {} | 额外数据: {}", event, details, extraData);
  }

  /** 记录业务警告 */
  public void logBusinessWarning(String warning, String details) {
    logger.warn("业务警告: {} | 详情: {}", warning, details);
  }

  /** 记录业务错误 */
  public void logBusinessError(String error, String details, Throwable throwable) {
    logger.error("业务错误: {} | 详情: {}", error, details, throwable);
  }

  /** 记录业务错误（无异常） */
  public void logBusinessError(String error, String details) {
    logger.error("业务错误: {} | 详情: {}", error, details);
  }
}
