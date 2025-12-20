package com.bscm.service;

public interface VerificationCodeService {

  /** 发送验证码 */
  String sendVerificationCode(String phone);

  /** 验证验证码 */
  boolean verifyCode(String phone, String code);
}
