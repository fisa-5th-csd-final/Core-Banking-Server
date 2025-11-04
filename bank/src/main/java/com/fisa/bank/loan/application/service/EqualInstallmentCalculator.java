package com.fisa.bank.loan.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.fisa.bank.loan.application.model.MonthlyRepayment;

/*
   원리금 균등 상환 - 월별 상환액 계산기
   P - 대출 원금
   r - 월 이자율(금리)
   N - 총 상환 횟수(개월)
*/
public class EqualInstallmentCalculator {

  /**
   * 원리금 균등 상환 계산
   *
   * @param remainPrincipal 남은 원금
   * @param completedInterest 연이율 (예: 0.05 = 5%)
   * @param term 상환 기간(개월)
   * @return 월별 상환액
   */
  public static MonthlyRepayment calculateEqualInstallment(
      BigDecimal principal,
      BigDecimal remainPrincipal,
      BigDecimal completedInterest,
      Integer term,
      LocalDateTime nextRepaymentDate,
      LocalDateTime loanEndDate) {
    // 월 금리 계산
    BigDecimal monthlyRate =
        completedInterest.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_DOWN);

    // (r * P) / (1 - (1 + r)^-n)
    BigDecimal numerator = monthlyRate.multiply(principal); // r * P
    BigDecimal denominator =
        BigDecimal.ONE.subtract(
            BigDecimal.ONE
                .add(monthlyRate)
                .pow(-term, new java.math.MathContext(10, RoundingMode.HALF_DOWN)));

    BigDecimal monthlyPayment = numerator.divide(denominator, 0, RoundingMode.DOWN);
    BigDecimal interestPayment =
        remainPrincipal.multiply(monthlyRate).setScale(0, RoundingMode.DOWN);
    BigDecimal principalPayment = monthlyPayment.subtract(interestPayment);

    // 다음 상환 날짜와 마지막 상환 날짜가 같다면 마지막 상환일이므로, 갚아야 하는 원금을 남은 원금으로 치환.
    if (nextRepaymentDate.toLocalDate().equals(loanEndDate.toLocalDate())) {
      principalPayment = remainPrincipal.setScale(0, RoundingMode.UP);
      monthlyPayment = principalPayment.add(interestPayment);
    }
    return new MonthlyRepayment(term, principalPayment, interestPayment, monthlyPayment);
  }
}
