package com.fisa.bank.loan.application.exception;

import com.fisa.bank.common.application.exception.BusinessException;

public class LoanLedgerAccessDeniedException extends BusinessException {
  private static final String errorCode = "L007";
  private static final String message = "내 대출만 확인할 수 있습니다.";

  public LoanLedgerAccessDeniedException() {
    super(errorCode, String.format(message));
  }
}
