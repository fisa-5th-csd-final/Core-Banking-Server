package com.fisa.bank.loan.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.fisa.bank.loan.application.model.MonthlyRepayment;

/*
   원금 균등 상환 - 월별 상환액 계산기
   P - 대출 원금
   r - 월 이자율(금리)
   N - 총 상환 횟수(개월)
*/
public class EqualPrincipalCalculator implements LoanCalculator {

  private static final EqualPrincipalCalculator INSTANCE = new EqualPrincipalCalculator();

  private EqualPrincipalCalculator() {}

  public static EqualPrincipalCalculator getInstance() {
    return INSTANCE;
  }

  @Override
  public MonthlyRepayment calculate(
      BigDecimal principal,
      BigDecimal remainPrincipal,
      BigDecimal annualInterestRate,
      Integer totalTermInMonths,
      Integer currentTerm,
      LocalDateTime nextRepaymentDate,
      LocalDateTime loanEndDate) {

    // 월 이자율 계산 (연 이자율 / 12)
    BigDecimal monthlyRate =
        annualInterestRate.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_DOWN);

    // 매월 동일한 원금 상환액 = 총 원금 / 총 상환 기간
    BigDecimal monthlyPrincipalPayment =
        principal.divide(BigDecimal.valueOf(totalTermInMonths), 0, RoundingMode.DOWN);

    // 해당 월의 이자 = 남은 원금 * 월 이자율
    BigDecimal interestPayment =
        remainPrincipal.multiply(monthlyRate).setScale(0, RoundingMode.DOWN);

    BigDecimal principalPayment;
    BigDecimal monthlyPayment;

    // 다음 상환 날짜와 마지막 상환 날짜가 같다면 마지막 상환일이므로, 갚아야 하는 원금을 남은 원금으로 치환.
    if (nextRepaymentDate.toLocalDate().equals(loanEndDate.toLocalDate())) {
      principalPayment = remainPrincipal.setScale(0, RoundingMode.UP);
      monthlyPayment = principalPayment.add(interestPayment);
    } else {
      principalPayment = monthlyPrincipalPayment;
      monthlyPayment = principalPayment.add(interestPayment);
    }

    return new MonthlyRepayment(
        currentTerm,
        principalPayment,
        interestPayment,
        monthlyPayment,
        remainPrincipal.subtract(principalPayment));
  }

  // 기존 EqualInstallmentCalculator 형식 사용
  @Deprecated
  public static MonthlyRepayment calculateEqualRepayment(
      BigDecimal principal,
      BigDecimal remainPrincipal,
      BigDecimal annualInterestRate,
      Integer totalTermInMonths,
      Integer currentTerm,
      LocalDateTime nextRepaymentDate,
      LocalDateTime loanEndDate) {
    return INSTANCE.calculate(
        principal,
        remainPrincipal,
        annualInterestRate,
        totalTermInMonths,
        currentTerm,
        nextRepaymentDate,
        loanEndDate);
  }
}
