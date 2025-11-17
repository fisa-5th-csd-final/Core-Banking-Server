package com.fisa.bank.domains.loan.application.exception;

import com.fisa.bank.domains.common.application.exception.BusinessException;

public class UnknownCalculatorException extends BusinessException {

  private static final String errorCode = "L008";
  private static final String message = "지원하지 않는 상환 타입입니다.";

  public UnknownCalculatorException() {
    super(errorCode, message);
  }
}
