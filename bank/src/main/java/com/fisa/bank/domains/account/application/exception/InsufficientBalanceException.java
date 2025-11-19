package com.fisa.bank.domains.account.application.exception;

import com.fisa.bank.domains.common.application.exception.BusinessException;

/**
 * InsufficientBalanceException
 *
 * <p>출금 요청 금액이 계좌 잔액보다 많을 때 발생하는 예외
 */
public class InsufficientBalanceException extends BusinessException {
  private static final String ERROR_CODE = "A002";
  private static final String MESSAGE = "출금 가능한 잔액이 부족합니다.";

  public InsufficientBalanceException() {
    super(ERROR_CODE, MESSAGE);
  }
}
