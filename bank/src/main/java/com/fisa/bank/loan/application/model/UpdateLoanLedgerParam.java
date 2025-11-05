package com.fisa.bank.loan.application.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class UpdateLoanLedgerParam {
  private BigDecimal remainPrincipal;
  private LocalDateTime nextRepaymentDate;
  private LocalDateTime lastRepaymentDate;

  public UpdateLoanLedgerParam(
      BigDecimal remainPrincipal,
      LocalDateTime nextRepaymentDate,
      LocalDateTime lastRepaymentDate) {
    this.remainPrincipal = remainPrincipal;
    this.nextRepaymentDate = nextRepaymentDate;
    this.lastRepaymentDate = lastRepaymentDate;
  }
}
