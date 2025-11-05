package com.fisa.bank.loan.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fisa.bank.loan.application.model.MonthlyRepayment;

/** 대출 상환 계산기 인터페이스 */
public interface LoanCalculator {

  /**
   * 월별 상환액 계산
   *
   * @param principal 대출 원금
   * @param remainPrincipal 남은 원금
   * @param annualInterestRate 연이율 (예: 0.05 = 5%)
   * @param totalTermInMonths 총 상환 기간(개월)
   * @param currentTerm 현재 상환 회차
   * @param nextRepaymentDate 다음 상환 날짜
   * @param loanEndDate 대출 종료 날짜
   * @return 월별 상환액
   */
  MonthlyRepayment calculate(
      BigDecimal principal,
      BigDecimal remainPrincipal,
      BigDecimal annualInterestRate,
      Integer totalTermInMonths,
      Integer currentTerm,
      LocalDateTime nextRepaymentDate,
      LocalDateTime loanEndDate);
}
