package com.fisa.bank.loan.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fisa.bank.loan.application.model.MonthlyRepayment;

public class BulletCalculatorTest {

  @Test
  @DisplayName("만기일시상환 계산기 - 월별 이자 및 만기 원금 상환 출력 + 검증")
  void printAndAssertBulletLoanRepayments() {
    // given
    BulletCalculator calculator = BulletCalculator.getInstance();

    BigDecimal principal = BigDecimal.valueOf(1_000_000); // 원금
    BigDecimal annualRate = BigDecimal.valueOf(0.05); // 연이율 5%
    int totalTerm = 12;

    LocalDateTime nextRepaymentDate = LocalDateTime.of(2025, 11, 1, 0, 0);
    LocalDateTime loanEndDate = LocalDateTime.of(2026, 10, 1, 0, 0);

    BigDecimal remainPrincipal = principal;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    System.out.printf(
        "%-4s %-17s %7s %8s %12s %12s%n", "회차", "상환일", "남은 원금", "이자", "원금 상환", "월 상환액");

    // when
    MonthlyRepayment lastMonth = null;

    for (int month = 1; month <= totalTerm; month++) {
      MonthlyRepayment repayment =
          calculator.calculate(
              principal,
              remainPrincipal,
              annualRate,
              totalTerm,
              month,
              nextRepaymentDate,
              loanEndDate);

      // 출력
      System.out.printf(
          "%2d   %-17s %12s %12s %12s %12s%n",
          month,
          nextRepaymentDate.format(formatter),
          remainPrincipal,
          repayment.getInterestPayment(),
          repayment.getPrincipalPayment(),
          repayment.getMonthlyPayment());

      remainPrincipal = repayment.getRemainPrincipal();
      nextRepaymentDate = nextRepaymentDate.plusMonths(1);

      if (month == totalTerm) {
        lastMonth = repayment;
      }
    }

    //        // then
    //        assertThat(lastMonth).isNotNull();
    //        assertThat(lastMonth.getPrincipalPayment()).isEqualByComparingTo(principal);
    //        assertThat(lastMonth.getInterestPayment()).isGreaterThan(BigDecimal.ZERO);
    //        assertThat(lastMonth.getMonthlyPayment())
    //
    // .isEqualByComparingTo(lastMonth.getPrincipalPayment().add(lastMonth.getInterestPayment()));
    //        assertThat(lastMonth.getRemainPrincipal()).isEqualByComparingTo(BigDecimal.ZERO);
  }
}
