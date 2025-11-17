package com.fisa.bank.domains.account.application.exception;

import com.fisa.bank.domains.common.application.exception.BusinessException;

public class AccountOwnerMismatchException extends BusinessException {
  private static final String ERROR_CODE = "A003";
  private static final String MESSAGE = "계좌 소유자가 일치하지 않습니다";

  public AccountOwnerMismatchException() {
    super(ERROR_CODE, MESSAGE);
  }
}
