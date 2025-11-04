package com.fisa.bank.loan.application.exception;

import com.fisa.bank.common.application.exception.BusinessException;
import com.fisa.bank.loan.persistence.entity.id.LoanProductId;
import com.fisa.bank.user.persistence.entity.id.UserId;

public class DuplicateLoanException extends BusinessException {
  private static final String errorCode = "L003";
  private static final String message = "같은 대출 상품을 중복 가입할 수 없습니다. (고객 id: %s 상품 id: %s)";

  public DuplicateLoanException(UserId userId, LoanProductId loanProductId) {
    super(errorCode, String.format(message, userId.getValue(), loanProductId.getValue()));
  }
}
