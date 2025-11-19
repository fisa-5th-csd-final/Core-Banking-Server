package com.fisa.bank.domains.interest.application.exception;

import com.fisa.bank.domains.common.application.exception.BusinessException;

public class InterestException extends BusinessException {

  private static final String errorCode = "I001";
  private static final String message = "요청하신 대출 상품이 금리 테이블에 존재하지 않습니다. (ID: %s)";

  public InterestException(Long loanProductId) {
    super(errorCode, String.format(message, loanProductId));
  }
}
