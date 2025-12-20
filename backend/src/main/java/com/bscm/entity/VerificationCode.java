package com.bscm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "verification_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCode {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "phone", nullable = false, length = 20)
  private String phone;

  @Column(name = "code", nullable = false, length = 6)
  private String code;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Column(name = "used", nullable = false)
  private Boolean used = false;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    if (expiresAt == null) {
      expiresAt = LocalDateTime.now().plusMinutes(5); // 5分钟有效期
    }
  }
}
