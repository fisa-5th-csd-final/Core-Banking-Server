package com.fisa.bank.loan.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fisa.bank.loan.application.model.MonthlyRepayment;
import com.fisa.bank.loan.application.service.calculator.EqualInstallmentCalculator;

public class EqualInstallmentCalculatorTest {
  private final EqualInstallmentCalculator calculatorService = new EqualInstallmentCalculator();

  @Test
  @DisplayName("원리금균등상환 계산기 - 월별 이자 및 만기 원금 상환 출력")
  void printMonthlyRepaymentWithDates() {
    // 대출 조건
    BigDecimal principal = BigDecimal.valueOf(1_000_000); // 원금
    BigDecimal completedInterest = BigDecimal.valueOf(0.05); // 연이율 5%
    int term = 12; // 12개월

    LocalDateTime nextRepaymentDate = LocalDateTime.of(2025, 12, 3, 17, 9, 30); // 첫 상환일
    LocalDateTime loanEndDate = LocalDateTime.of(2026, 11, 3, 17, 9, 30); // 마지막 상환일

    BigDecimal remainPrincipal = principal;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    System.out.printf(
        "%-4s %-17s %8s %12s %12s %12s\n", "회차", "상환일", "남은 원금", "이자", "원금 상환", "월 상환액");
    for (int month = 1; month <= term; month++) {
      //            int remainingTerm = term - (month - 1);

      // 월별 상환액 계산
      MonthlyRepayment repayment =
          calculatorService.calculate(
              principal,
              remainPrincipal,
              completedInterest,
              term,
              month,
              nextRepaymentDate,
              loanEndDate);

      System.out.printf(
          "%2d  %-19s  %12s  %12s  %12s  %12s\n",
          month,
          nextRepaymentDate.format(formatter),
          remainPrincipal,
          repayment.getInterestPayment(),
          repayment.getPrincipalPayment(),
          repayment.getMonthlyPayment());

      // 남은 원금 갱신
      remainPrincipal = remainPrincipal.subtract(repayment.getPrincipalPayment());

      // 다음 상환일 1개월 후로 갱신
      nextRepaymentDate = nextRepaymentDate.plusMonths(1);
    }
  }
}
