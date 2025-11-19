package com.fisa.bank.domains.loan.application.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fisa.bank.domains.loan.persistence.enums.RepaymentStatus;

@Getter
@Builder
public class UpdateLoanLedgerParam {
  private BigDecimal remainPrincipal;
  private LocalDateTime nextRepaymentDate;
  private LocalDateTime lastRepaymentDate;
  private RepaymentStatus status;
  private Boolean autoDepositEnabled;

  public UpdateLoanLedgerParam(
      BigDecimal remainPrincipal,
      LocalDateTime nextRepaymentDate,
      LocalDateTime lastRepaymentDate,
      RepaymentStatus status,
      Boolean autoDepositEnabled) {
    this.remainPrincipal = remainPrincipal;
    this.nextRepaymentDate = nextRepaymentDate;
    this.lastRepaymentDate = lastRepaymentDate;
    this.status = status;
    this.autoDepositEnabled = autoDepositEnabled;
  }
}
