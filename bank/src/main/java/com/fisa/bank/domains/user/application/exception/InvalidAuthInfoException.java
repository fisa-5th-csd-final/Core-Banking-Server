package com.fisa.bank.domains.user.application.exception;

import com.fisa.bank.domains.common.application.exception.BusinessException;

public class InvalidAuthInfoException extends BusinessException {

  private static final String errorCode = "U100";
  private static final String message = "이미 존재하는 ID 입니다. : %s";

  public InvalidAuthInfoException(String loginId) {
    super(errorCode, String.format(message, loginId));
  }
}
