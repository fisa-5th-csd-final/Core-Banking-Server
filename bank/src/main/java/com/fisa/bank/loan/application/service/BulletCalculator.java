package com.fisa.bank.loan.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

import com.fisa.bank.loan.application.model.MonthlyRepayment;

public class BulletCalculator implements LoanCalculator {

  private static final BulletCalculator INSTANCE = new BulletCalculator();

  private BulletCalculator() {}

  public static BulletCalculator getInstance() {
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
    LocalDateTime prevRepaymentDate = nextRepaymentDate.minusMonths(1);

    LocalDate prevDate = nextRepaymentDate.toLocalDate().minusMonths(1);
    LocalDate nextDate = nextRepaymentDate.toLocalDate();

    int month = nextRepaymentDate.getMonthValue();
    int year = nextRepaymentDate.getYear();
    int daysInMonth = YearMonth.of(year, month).lengthOfMonth();

    BigDecimal interestPayment =
        remainPrincipal
            .multiply(annualInterestRate)
            .multiply(BigDecimal.valueOf(daysInMonth))
            .divide(
                BigDecimal.valueOf(nextRepaymentDate.toLocalDate().lengthOfYear()),
                2,
                RoundingMode.HALF_DOWN);

    BigDecimal principalPayment = BigDecimal.ZERO;

    if (nextRepaymentDate.toLocalDate().equals(loanEndDate.toLocalDate())) {
      principalPayment = remainPrincipal;
    }
    BigDecimal monthlyPayment = interestPayment.add(principalPayment);

    return new MonthlyRepayment(
        currentTerm,
        principalPayment.setScale(0, RoundingMode.DOWN),
        interestPayment.setScale(0, RoundingMode.DOWN),
        monthlyPayment.setScale(0, RoundingMode.DOWN),
        remainPrincipal.subtract(principalPayment));
  }
}
