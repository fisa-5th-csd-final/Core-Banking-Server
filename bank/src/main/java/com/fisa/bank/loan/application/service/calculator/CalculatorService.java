package com.fisa.bank.loan.application.service.calculator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.fisa.bank.loan.application.model.MonthlyRepayment;

@Service
public class CalculatorService {
  private LoanCalculator loanCalculator;

  public CalculatorService() {}

  public void setLoanCalculator(LoanCalculator loanCalculator) {
    this.loanCalculator = loanCalculator;
  }

  public MonthlyRepayment calculate(
      BigDecimal principal,
      BigDecimal remainPrincipal,
      BigDecimal annualInterestRate,
      Integer totalTermInMonths,
      Integer currentTerm,
      LocalDateTime nextRepaymentDate,
      LocalDateTime loanEndDate) {

    return loanCalculator.calculate(
        principal,
        remainPrincipal,
        annualInterestRate,
        totalTermInMonths,
        currentTerm,
        nextRepaymentDate,
        loanEndDate);
  }
}
