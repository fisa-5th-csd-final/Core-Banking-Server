package com.fisa.bank.domains.user.application.util;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.fisa.bank.domains.user.application.exception.InvalidPasswordFormatException;

@Component
@RequiredArgsConstructor
public class PasswordUtil {

  private final PasswordValidator validator;
  private final PasswordEncoder passwordEncoder;

  public String encrypt(String password) {

    if (!validator.validate(password)) throw new InvalidPasswordFormatException();

    return passwordEncoder.encode(password);
  }
}
