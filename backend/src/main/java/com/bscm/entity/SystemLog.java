package com.bscm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 系统日志实体类 用于存储前端发送的客户端日志 */
@Entity
@Table(name = "system_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** 日志级别：DEBUG, INFO, WARN, ERROR */
  @Column(nullable = false, length = 10)
  private String level;

  /** 日志消息 */
  @Column(nullable = false, columnDefinition = "TEXT")
  private String message;

  /** 日志数据（JSON格式） */
  @Column(columnDefinition = "TEXT")
  private String data;

  /** 用户ID（如果有） */
  @Column(name = "user_id")
  private Long userId;

  /** 请求URL */
  @Column(name = "url", length = 500)
  private String url;

  /** 用户代理 */
  @Column(name = "user_agent", length = 500)
  private String userAgent;

  /** 创建时间 */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}
