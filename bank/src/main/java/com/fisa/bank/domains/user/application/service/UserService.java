package com.fisa.bank.domains.user.application.service;

import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisa.bank.domains.user.application.dto.UserCreateRequest;
import com.fisa.bank.domains.user.application.dto.UserInfoResponse;
import com.fisa.bank.domains.user.application.event.UserCreatedEvent;
import com.fisa.bank.domains.user.application.exception.InvalidAuthInfoException;
import com.fisa.bank.domains.user.application.service.reader.UserReader;
import com.fisa.bank.domains.user.application.util.PasswordUtil;
import com.fisa.bank.domains.user.persistence.entity.User;
import com.fisa.bank.domains.user.persistence.entity.UserAuth;
import com.fisa.bank.domains.user.persistence.entity.id.UserId;
import com.fisa.bank.domains.user.persistence.repository.UserAuthRepository;
import com.fisa.bank.domains.user.persistence.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

  private final ApplicationEventPublisher eventPublisher;
  private final UserRepository userRepository;
  private final UserAuthRepository authRepository;
  private final UserReader userReader;
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
    User saved = userRepository.save(user);

    eventPublisher.publishEvent(new UserCreatedEvent(saved.getUserId().getValue()));

    return true;
  }

  public UserInfoResponse getUserInfo(UserId userId) {
    User user = userReader.getUserById(userId.getValue());

    return UserInfoResponse.of(user);
  }
}
