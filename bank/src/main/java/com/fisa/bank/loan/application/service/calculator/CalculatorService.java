package com.fisa.bank.loan.application.service.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.fisa.bank.loan.application.model.MonthlyRepayment;
import com.fisa.bank.loan.persistence.entity.LoanLedger;

@Service
public class CalculatorService {
  private LoanCalculator loanCalculator;

  public CalculatorService() {}

  public void setLoanCalculator(LoanCalculator loanCalculator) {
    this.loanCalculator = loanCalculator;
  }

  public MonthlyRepayment calculate(LoanLedger loanLedger) {

    return loanCalculator.calculate(
        loanLedger.getPrincipal(),
        loanLedger.getRemainPrincipal(),
        loanLedger.getCompletedInterest().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP),
        loanLedger.getTerm() * 12, // 연 -> 개월로 변경
        1, // currentTerm은 실제로 사용되지 않음
        loanLedger.getNextRepaymentDate(),
        loanLedger.getLoanEndDate());
  }
}
