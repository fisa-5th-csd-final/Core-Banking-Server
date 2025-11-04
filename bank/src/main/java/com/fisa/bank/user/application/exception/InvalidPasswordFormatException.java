package com.fisa.bank.user.application.exception;

import com.fisa.bank.common.application.exception.BusinessException;

public class InvalidPasswordFormatException extends BusinessException {

  private static final String errorCode = "U101";
  private static final String message = "비밀번호 규칙에 맞지 않습니다.";

  public InvalidPasswordFormatException() {
    super(errorCode, message);
  }
}
