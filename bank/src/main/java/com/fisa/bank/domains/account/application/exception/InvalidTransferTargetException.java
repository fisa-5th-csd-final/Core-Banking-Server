package com.fisa.bank.domains.account.application.exception;

import com.fisa.bank.domains.common.application.exception.BusinessException;

/**
 * AccountNotFoundException
 *
 * <p>계좌가 존재하지 않을 때 발생하는 비즈니스 예외
 */
public class InvalidTransferTargetException extends BusinessException {
  private static final String ERROR_CODE = "A006";
  private static final String MESSAGE = "동일 계좌 간 송금은 허용되지 않습니다.";

  public InvalidTransferTargetException() {
    super(ERROR_CODE, MESSAGE);
  }
}
