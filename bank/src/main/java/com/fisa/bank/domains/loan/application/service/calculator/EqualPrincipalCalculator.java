package com.fisa.bank.domains.loan.application.service.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;

import com.fisa.bank.domains.loan.application.model.MonthlyRepayment;

public class EqualPrincipalCalculator implements LoanCalculator {

  private static final EqualPrincipalCalculator INSTANCE = new EqualPrincipalCalculator();

  public EqualPrincipalCalculator() {}

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

    int month = nextRepaymentDate.getMonthValue();
    int year = nextRepaymentDate.getYear();
    int daysInMonth = YearMonth.of(year, month).lengthOfMonth();

    BigDecimal interestPayment =
        remainPrincipal
            .multiply(annualInterestRate)
            .multiply(BigDecimal.valueOf(daysInMonth))
            .divide(
                BigDecimal.valueOf(nextRepaymentDate.toLocalDate().lengthOfYear()),
                0,
                RoundingMode.DOWN);

    // 매월 동일한 원금 상환액 = 총 원금 / 총 상환 기간
    BigDecimal principalPayment;

    // 첫 상환일에 원금을 가장 많이 내도록 조정
    BigDecimal monthlyPrincipal =
        principal.divide(BigDecimal.valueOf(totalTermInMonths), 0, RoundingMode.DOWN);
    boolean isFirstRepayment = remainPrincipal.compareTo(principal) == 0;

    if (isFirstRepayment) {
      principalPayment =
          remainPrincipal.subtract(
              monthlyPrincipal.multiply(BigDecimal.valueOf(totalTermInMonths - 1)));
    } else {
      // 2회차 이후는 기존 균등 원금 상환
      principalPayment = monthlyPrincipal;
    }

    BigDecimal monthlyPayment = principalPayment.add(interestPayment);

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
