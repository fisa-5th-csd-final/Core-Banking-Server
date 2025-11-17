package com.fisa.bank.domains.loan.application.exception;

import com.fisa.bank.domains.common.application.exception.BusinessException;

public class LoanProductNotFoundException extends BusinessException {

  private static final String errorCode = "L001";
  private static final String message = "요청하신 대출 상품을 찾을 수 없습니다. (ID: %s)";

  public LoanProductNotFoundException(Long loanProductId) {
    super(errorCode, String.format(message, loanProductId));
  }
}
