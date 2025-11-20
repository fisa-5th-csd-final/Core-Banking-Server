package com.fisa.bank.domains.loan.application.exception;

import com.fisa.bank.domains.common.application.exception.BusinessException;

public class LoanApplyDeniedException extends BusinessException {

  private static final String ERROR_CODE = "L010";
  private static final String MESSAGE = "급여 계좌는 대출 상품에 가입할 수 없습니다.";

  public LoanApplyDeniedException() {
    super(ERROR_CODE, MESSAGE);
  }

  public LoanApplyDeniedException(String message) {
    super(ERROR_CODE, message);
  }
}
