package com.bscm.service.impl;

import com.bscm.entity.User;
import com.bscm.repository.UserRepository;
import com.bscm.service.UserService;
import com.bscm.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final VerificationCodeService verificationCodeService;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public User register(String phone, String password, String verificationCode) {
    // 验证验证码
    if (!verificationCodeService.verifyCode(phone, verificationCode)) {
      throw new RuntimeException("验证码错误或已过期");
    }

    // 检查手机号是否已注册
    if (userRepository.existsByPhone(phone)) {
      throw new RuntimeException("该手机号已被注册");
    }

    // 创建新用户
    User user = new User();
    user.setPhone(phone);
    user.setPassword(passwordEncoder.encode(password));

    return userRepository.save(user);
  }

  @Override
  public User login(String phone, String password) {
    User user =
        userRepository.findByPhone(phone).orElseThrow(() -> new RuntimeException("手机号或密码错误"));

    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new RuntimeException("手机号或密码错误");
    }

    return user;
  }

  @Override
  public User findByPhone(String phone) {
    return userRepository.findByPhone(phone).orElseThrow(() -> new RuntimeException("用户不存在"));
  }

  @Override
  @Transactional
  public User createUser(String phone, String password) {
    // 检查手机号是否已注册
    if (userRepository.existsByPhone(phone)) {
      throw new RuntimeException("该手机号已被注册");
    }

    // 创建新用户
    User user = new User();
    user.setPhone(phone);
    user.setPassword(passwordEncoder.encode(password));

    return userRepository.save(user);
  }
}
