package com.bscm.service;

import com.bscm.entity.User;

public interface UserService {

  /** 用户注册 */
  User register(String phone, String password, String verificationCode);

  /** 用户登录 */
  User login(String phone, String password);

  /** 根据手机号查找用户 */
  User findByPhone(String phone);

  /** 创建用户（用于一键登录，不需要验证码） */
  User createUser(String phone, String password);
}
