package com.fisa.bank.loan.application.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MonthlyRepayment {
  private int term;
  //    private int remainingTerm; // 남은 상환 기간(월)
  private BigDecimal principalPayment;
  private BigDecimal interestPayment;
  private BigDecimal monthlyPayment;
  private BigDecimal remainPrincipal;

  public MonthlyRepayment(
      int term,
      BigDecimal principalPayment,
      BigDecimal interestPayment,
      BigDecimal monthlyPayment,
      BigDecimal remainPrincipal) {
    this.term = term;
    //        this.remainingTerm = term;
    this.principalPayment = principalPayment;
    this.interestPayment = interestPayment;
    this.monthlyPayment = monthlyPayment;
    this.remainPrincipal = remainPrincipal;
  }

  public MonthlyRepayment() {}
}
