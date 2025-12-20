package com.bscm.service.impl;

import com.bscm.entity.VerificationCode;
import com.bscm.repository.VerificationCodeRepository;
import com.bscm.service.VerificationCodeService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationCodeServiceImpl implements VerificationCodeService {

  private final VerificationCodeRepository verificationCodeRepository;
  private final Random random = new Random();

  @Override
  @Transactional
  public String sendVerificationCode(String phone) {
    // 生成6位随机验证码
    String code = String.format("%06d", random.nextInt(1000000));

    // 创建验证码记录
    VerificationCode verificationCode = new VerificationCode();
    verificationCode.setPhone(phone);
    verificationCode.setCode(code);
    verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(5));
    verificationCode.setUsed(false);

    verificationCodeRepository.save(verificationCode);

    // 这里应该调用短信服务发送验证码
    // 为了演示，我们只在日志中输出
    log.info("发送验证码到手机号 {}: {}", phone, code);

    // 开发环境返回验证码，生产环境应该返回null
    return code;
  }

  @Override
  @Transactional
  public boolean verifyCode(String phone, String code) {
    Optional<VerificationCode> codeOpt =
        verificationCodeRepository.findValidCode(phone, code, LocalDateTime.now());

    if (codeOpt.isPresent()) {
      // 标记为已使用
      verificationCodeRepository.markAsUsed(phone, code);
      return true;
    }

    return false;
  }

  /** 定时清理过期验证码（每小时执行一次） */
  @Scheduled(fixedRate = 3600000)
  @Transactional
  public void cleanExpiredCodes() {
    verificationCodeRepository.deleteExpiredCodes(LocalDateTime.now());
  }
}
