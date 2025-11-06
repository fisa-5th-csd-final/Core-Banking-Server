package com.fisa.bank.loan.application.exception;

import com.fisa.bank.common.application.exception.BusinessException;

public class InsufficientBalanceAmountException extends BusinessException {

  private static final String error = "L006";
  private static final String message = "상환을 위한 계좌 잔액이 충분하지 않습니다.";

  public InsufficientBalanceAmountException() {
    super(error, message);
  }
}
