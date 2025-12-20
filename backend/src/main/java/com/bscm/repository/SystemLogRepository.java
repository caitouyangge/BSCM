package com.bscm.repository;

import com.bscm.entity.SystemLog;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/** 系统日志Repository */
@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {

  /** 根据日志级别查询 */
  List<SystemLog> findByLevel(String level);

  /** 根据用户ID查询 */
  List<SystemLog> findByUserId(Long userId);

  /** 根据时间范围查询 */
  @Query("SELECT l FROM SystemLog l WHERE l.createdAt BETWEEN ?1 AND ?2 ORDER BY l.createdAt DESC")
  List<SystemLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

  /** 分页查询 */
  Page<SystemLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

  /** 根据级别分页查询 */
  Page<SystemLog> findByLevelOrderByCreatedAtDesc(String level, Pageable pageable);
}
