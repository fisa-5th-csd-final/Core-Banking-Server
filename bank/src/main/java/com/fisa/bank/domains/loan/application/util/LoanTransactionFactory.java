package com.fisa.bank.domains.loan.application.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fisa.bank.domains.loan.application.model.EarlyRepayment;
import com.fisa.bank.domains.loan.application.model.MonthlyRepayment;
import com.fisa.bank.domains.loan.persistence.entity.LoanLedger;
import com.fisa.bank.domains.loan.persistence.entity.LoanTransaction;
import com.fisa.bank.domains.loan.persistence.enums.TransactionType;

public class LoanTransactionFactory {

  /** private 공용 팩토리 메소드 */
  private static LoanTransaction create(
      LoanLedger loanLedger,
      LocalDateTime date,
      TransactionType transactionType,
      BigDecimal amount,
      BigDecimal repayInterest,
      BigDecimal repayPrincipal,
      BigDecimal remainPrincipal) {
    return LoanTransaction.builder()
        .loanLedger(loanLedger)
        .amount(amount)
        .date(date)
        .remainPrincipal(remainPrincipal)
        .repaymentInterestAmount(repayInterest)
        .repaymentPrincipalAmount(repayPrincipal)
        .transactionType(transactionType)
        .build();
  }

  /**
   * 중도 상환 팩토리 메소드
   *
   * @param loanLedger
   * @param earlyRepayment
   * @return
   */
  public static LoanTransaction createEarlyRepay(
      LoanLedger loanLedger, EarlyRepayment earlyRepayment, LocalDateTime transactionTime) {
    return create(
        loanLedger,
        transactionTime,
        TransactionType.REPAYMENT,
        earlyRepayment.getMustPaidAmount(),
        earlyRepayment.getEarlyPaidCost(),
        earlyRepayment.getRemainPrincipal(),
        BigDecimal.ZERO);
  }

  /**
   * 정기 상환 팩토리 메소드
   *
   * @param loanLedger
   * @param amount
   * @param monthlyRepayment
   * @return
   */
  public static LoanTransaction createRepay(
      LoanLedger loanLedger,
      BigDecimal amount,
      MonthlyRepayment monthlyRepayment,
      LocalDateTime transactionTime) {
    return create(
        loanLedger,
        transactionTime,
        TransactionType.REPAYMENT,
        amount,
        monthlyRepayment.getInterestPayment(),
        monthlyRepayment.getPrincipalPayment(),
        monthlyRepayment.getRemainPrincipal());
  }

  /**
   * 대출 가입 팩토리 메소드
   *
   * @param loanLedger
   * @param remainPrincipal
   * @return
   */
  public static LoanTransaction createLoan(
      LoanLedger loanLedger, BigDecimal remainPrincipal, LocalDateTime transactionTime) {
    return create(
        loanLedger,
        transactionTime,
        TransactionType.LOAN,
        remainPrincipal,
        null,
        null,
        remainPrincipal);
  }
}
