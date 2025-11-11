package com.fisa.bank.loan.application.dto.response;

import lombok.Getter;

import java.math.BigDecimal;

import com.fisa.bank.loan.persistence.entity.LoanLedger;

@Getter
public class LoanLedgerResponse {
  private final String name;
  private final BigDecimal remainPrincipal;
  private final BigDecimal completedInterest;
  private final Long loanLedgerId;

  public LoanLedgerResponse(
      String name, BigDecimal remainPrincipal, BigDecimal completedInterest, Long loanLedgerId) {
    this.name = name;
    this.remainPrincipal = remainPrincipal;
    this.completedInterest = completedInterest;
    this.loanLedgerId = loanLedgerId;
  }

  public static LoanLedgerResponse from(LoanLedger loanLedger) {
    return new LoanLedgerResponse(
        loanLedger.getLoanProduct().getName(), // 엔티티 구조에 맞게 수정
        loanLedger.getRemainPrincipal(),
        loanLedger.getCompletedInterest(),
        loanLedger.getLoanLedgerId().getValue());
  }
}
