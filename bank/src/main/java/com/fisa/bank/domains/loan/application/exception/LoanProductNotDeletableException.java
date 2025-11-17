package com.fisa.bank.domains.loan.application.exception;

import com.fisa.bank.domains.common.application.exception.BusinessException;

public class LoanProductNotDeletableException extends BusinessException {
  private static final String ERROR_CODE = "L009";
  private static final String MESSAGE = "해당 상품에 연결된 대출 원장이 있어 삭제할 수 없습니다.";

  public LoanProductNotDeletableException() {
    super(ERROR_CODE, MESSAGE);
  }
}
