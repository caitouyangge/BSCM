package com.bscm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diagnosis_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = true)
  private Long userId;

  @Column(name = "symptoms", columnDefinition = "TEXT", nullable = false)
  private String symptoms;

  @Column(name = "diagnosis_result", columnDefinition = "TEXT")
  private String diagnosisResult;

  @Column(name = "image_paths", columnDefinition = "TEXT")
  private String imagePaths; // JSON格式存储多个图片路径

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
