package com.bscm.repository;

import com.bscm.entity.VerificationCode;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

  /** 根据手机号和验证码查找（未使用且未过期） */
  @Query(
      "SELECT v FROM VerificationCode v WHERE v.phone = ?1 AND v.code = ?2 AND v.used = false AND v.expiresAt > ?3")
  Optional<VerificationCode> findValidCode(String phone, String code, LocalDateTime now);

  /** 标记验证码为已使用 */
  @Modifying
  @Query("UPDATE VerificationCode v SET v.used = true WHERE v.phone = ?1 AND v.code = ?2")
  void markAsUsed(String phone, String code);

  /** 删除过期的验证码 */
  @Modifying
  @Query("DELETE FROM VerificationCode v WHERE v.expiresAt < ?1")
  void deleteExpiredCodes(LocalDateTime now);
}
