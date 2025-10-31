package com.fisa.bank.user.application.exception;

import com.fisa.bank.common.application.exception.BusinessException;

public class InvalidAuthInfoException extends BusinessException {

  private static final String errorCode = "U1000";
  private static final String message = "이미 존재하는 ID 입니다. : %s";

  public InvalidAuthInfoException(String loginId) {
    super(errorCode, String.format(message, loginId));
  }
}
