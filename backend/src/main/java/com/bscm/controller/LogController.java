package com.bscm.controller;

import com.bscm.common.Result;
import com.bscm.entity.SystemLog;
import com.bscm.logging.BusinessLogger;
import com.bscm.repository.SystemLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** 日志控制器 用于接收前端发送的日志 */
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LogController {

  private final SystemLogRepository systemLogRepository;
  private final BusinessLogger businessLogger;
  private final ObjectMapper objectMapper;

  /** 接收客户端日志 */
  @PostMapping("/client")
  public Result<Void> receiveClientLog(@RequestBody ClientLogRequest request) {
    try {
      SystemLog log = new SystemLog();
      log.setLevel(request.getLevel());
      log.setMessage(request.getMessage());
      log.setUserId(request.getUserId());
      log.setUrl(request.getUrl());
      log.setUserAgent(request.getUserAgent());

      // 将data转换为JSON字符串
      if (request.getData() != null) {
        try {
          log.setData(objectMapper.writeValueAsString(request.getData()));
        } catch (JsonProcessingException e) {
          log.setData(request.getData().toString());
        }
      }

      systemLogRepository.save(log);

      // 只记录ERROR级别的日志到业务日志
      if ("ERROR".equals(request.getLevel())) {
        businessLogger.logBusinessError(
            "客户端错误", request.getMessage() + " | URL: " + request.getUrl());
      }

      return Result.success(null);
    } catch (Exception e) {
      // 静默失败，避免影响用户体验
      return Result.success(null);
    }
  }

  @Data
  static class ClientLogRequest {
    private String timestamp;
    private String level;
    private String message;
    private Map<String, Object> data;
    private String url;
    private String userAgent;
    private Long userId;
  }
}
