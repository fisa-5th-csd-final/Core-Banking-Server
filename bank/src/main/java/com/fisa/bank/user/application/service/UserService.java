package com.fisa.bank.user.application.service;

import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisa.bank.user.application.dto.UserCreateRequest;
import com.fisa.bank.user.application.dto.UserInfoResponse;
import com.fisa.bank.user.application.exception.InvalidAuthInfoException;
import com.fisa.bank.user.application.exception.UserNotFoundException;
import com.fisa.bank.user.application.util.PasswordUtil;
import com.fisa.bank.user.persistence.entity.User;
import com.fisa.bank.user.persistence.entity.UserAuth;
import com.fisa.bank.user.persistence.entity.id.UserId;
import com.fisa.bank.user.persistence.repository.UserAuthRepository;
import com.fisa.bank.user.persistence.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserAuthRepository authRepository;
  private final PasswordUtil passwordUtil;

  @Transactional
  public boolean create(UserCreateRequest request) {
    if (authRepository.existsById(request.loginId()))
      throw new InvalidAuthInfoException(request.loginId());

    String encryptedPassword = passwordUtil.encrypt(request.password());

    UserAuth userAuth = UserAuth.create(request.loginId(), encryptedPassword);

    User user =
        User.create(
            request.name(),
            request.address(),
            request.birthday(),
            BigInteger.valueOf(request.salary()),
            request.job(),
            userAuth);

    authRepository.save(userAuth);
    userRepository.save(user);

    return true;
  }

  public UserInfoResponse getUserInfo(UserId userId) {
    User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

    return UserInfoResponse.of(user);
  }
}
