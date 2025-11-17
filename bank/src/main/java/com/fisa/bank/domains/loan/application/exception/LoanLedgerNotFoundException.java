package com.fisa.bank.domains.loan.application.exception;

import com.fisa.bank.domains.common.application.exception.BusinessException;

public class LoanLedgerNotFoundException extends BusinessException {

  private static final String errorCode = "L004";
  private static final String message = "해당 대출을 찾을 수 없습니다. (ID: %s)";

  public LoanLedgerNotFoundException(Long loanLedgerId) {
    super(errorCode, String.format(message, loanLedgerId));
  }
}
