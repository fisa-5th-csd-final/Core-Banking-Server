package com.fisa.bank.loan.application.service.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fisa.bank.loan.application.exception.UnknownCalculatorException;
import com.fisa.bank.loan.application.model.MonthlyRepayment;
import com.fisa.bank.loan.persistence.entity.LoanLedger;
import com.fisa.bank.loan.persistence.enums.RepaymentType;

@Service
public class CalculatorService {
  private LoanCalculator loanCalculator;
  private final Map<RepaymentType, LoanCalculator> calculators;

  public CalculatorService() {
    // 각 계산기 인스턴스를 미리 생성하여 맵에 저장
    calculators = new EnumMap<>(RepaymentType.class);
    calculators.put(RepaymentType.EQUAL_INSTALLMENT, new EqualInstallmentCalculator());
    calculators.put(RepaymentType.EQUAL_PRINCIPAL, new EqualPrincipalCalculator());
    calculators.put(RepaymentType.BULLET, new BulletCalculator());
  }

  public MonthlyRepayment calculate(LoanLedger loanLedger) {
    LoanCalculator calculator = calculators.get(loanLedger.getRepaymentType());
    if (calculator == null) {
      throw new UnknownCalculatorException();
    }
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
