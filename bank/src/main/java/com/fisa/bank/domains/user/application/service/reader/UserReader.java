package com.fisa.bank.domains.user.application.service.reader;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.fisa.bank.domains.user.application.exception.UserNotFoundException;
import com.fisa.bank.domains.user.persistence.entity.User;
import com.fisa.bank.domains.user.persistence.entity.id.UserId;
import com.fisa.bank.domains.user.persistence.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserReader {
  private final UserRepository userRepository;

  public User getUserById(Long userId) {
    return userRepository.findById(UserId.of(userId)).orElseThrow(UserNotFoundException::new);
  }
}
