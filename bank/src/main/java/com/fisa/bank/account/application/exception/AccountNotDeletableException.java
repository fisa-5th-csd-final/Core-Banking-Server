package com.fisa.bank.account.application.exception;

import com.fisa.bank.common.application.exception.BusinessException;

public class AccountNotDeletableException extends BusinessException {
  private static final String ERROR_CODE = "A004";
  private static final String MESSAGE = "잔액이 0이 아닌 게좌는 삭제할 수 없습니다";

  public AccountNotDeletableException() {
    super(ERROR_CODE, MESSAGE);
  }
}
