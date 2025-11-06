package com.fisa.bank.loan.application.model;

import com.fisa.bank.loan.persistence.enums.RepaymentStatus;
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
  private RepaymentStatus status;

  public UpdateLoanLedgerParam(
      BigDecimal remainPrincipal,
      LocalDateTime nextRepaymentDate,
      LocalDateTime lastRepaymentDate,
      RepaymentStatus status) {
    this.remainPrincipal = remainPrincipal;
    this.nextRepaymentDate = nextRepaymentDate;
    this.lastRepaymentDate = lastRepaymentDate;
    this.status = status;
  }
}
