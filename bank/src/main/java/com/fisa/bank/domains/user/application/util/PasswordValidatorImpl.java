package com.fisa.bank.domains.user.application.util;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class PasswordValidatorImpl implements PasswordValidator {

  private static final String REGEX = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$";

  @Override
  public boolean validate(String origin) {
    return Pattern.matches(REGEX, origin);
  }
}
