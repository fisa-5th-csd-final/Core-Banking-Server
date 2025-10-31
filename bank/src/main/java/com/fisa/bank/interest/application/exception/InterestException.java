package com.fisa.bank.interest.application.exception;

import com.fisa.bank.common.application.exception.BusinessException;
import com.fisa.bank.loan.persistence.entity.id.LoanProductId;

public class InterestException extends BusinessException {

  private static final String errorCode = "I001";
  private static final String message = "요청하신 대출 상품이 금리 테이블에 없습니다. (ID: %s)";

  public InterestException(LoanProductId loanProductId) {
    super(errorCode, String.format(message, loanProductId));
  }
}
