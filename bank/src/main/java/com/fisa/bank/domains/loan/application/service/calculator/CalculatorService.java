package com.fisa.bank.domains.loan.application.service.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fisa.bank.domains.loan.application.dto.response.InterestDetailResponse;
import com.fisa.bank.domains.loan.application.exception.UnknownCalculatorException;
import com.fisa.bank.domains.loan.application.model.MonthlyRepayment;
import com.fisa.bank.domains.loan.persistence.entity.LoanLedger;
import com.fisa.bank.domains.loan.persistence.enums.RepaymentType;

@Service
public class CalculatorService {
  private final Map<RepaymentType, LoanCalculator> calculators;

  public CalculatorService() {
    // 각 계산기 인스턴스를 미리 생성하여 맵에 저장
    calculators = new EnumMap<>(RepaymentType.class);
    calculators.put(RepaymentType.EQUAL_INSTALLMENT, new EqualInstallmentCalculator());
    calculators.put(RepaymentType.EQUAL_PRINCIPAL, new EqualPrincipalCalculator());
    calculators.put(RepaymentType.BULLET, new BulletCalculator());
  }

  public List<MonthlyRepayment> calculate(LoanLedger loanLedger) {
    LoanCalculator calculator = calculators.get(loanLedger.getRepaymentType());
    if (calculator == null) {
      throw new UnknownCalculatorException();
    }
    List<MonthlyRepayment> repayments = new ArrayList<>();

    // 연체월까지 포함해서 계산하기 위해 일시적으로 바뀌는 값들
    BigDecimal tempRemainPrincipal = loanLedger.getRemainPrincipal();
    LocalDateTime lastRepaymentDate = loanLedger.getLastRepaymentDate();
    LocalDateTime tempNextRepaymentDate = loanLedger.getNextRepaymentDate();
    if (tempNextRepaymentDate == null) {
      return repayments; // 상환 일정이 없으면 빈 리스트 반환
    }
    // 마지막 상환일이 없을 때(첫 회차 미납 상태)는 다음 상환일을 기준으로 한 달 전을 가정한다
    if (lastRepaymentDate == null) {
      lastRepaymentDate = tempNextRepaymentDate.minusMonths(1);
    }

    int monthsOverdue =
        (int)
            ChronoUnit.MONTHS.between(
                lastRepaymentDate.toLocalDate(), tempNextRepaymentDate.toLocalDate());
    monthsOverdue = Math.max(monthsOverdue, 1); // 최소 1개월

    for (int i = 0; i < monthsOverdue; i++) {
      MonthlyRepayment monthlyRepayment =
          calculator.calculate(
              loanLedger.getPrincipal(),
              tempRemainPrincipal,
              loanLedger
                  .getCompletedInterest()
                  .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP),
              loanLedger.getTerm() * 12, // 연 -> 개월로 변경
              1, // currentTerm은 실제로 사용되지 않음
              tempNextRepaymentDate,
              loanLedger.getLoanEndDate());

      repayments.add(monthlyRepayment);

      // 임시 변수 업데이트 (DB는 건드리지 않음)
      tempRemainPrincipal = monthlyRepayment.getRemainPrincipal();
      tempNextRepaymentDate = tempNextRepaymentDate.plusMonths(1);
    }

    return repayments;
  }

  /**
   * [추가된 메서드] LoanLedger를 기반으로 만기일까지의 전체 상환 스케줄을 계산합니다. 이 메서드가 RepaymentType에 맞는 calculate 로직을
   * 반복적으로 활용합니다.
   */
  private List<MonthlyRepayment> calculateFullSchedule(LoanLedger loanLedger) {

    LoanCalculator calculator = calculators.get(loanLedger.getRepaymentType());
    if (calculator == null) {
      throw new UnknownCalculatorException();
    }

    List<MonthlyRepayment> fullSchedule = new ArrayList<>();

    // 시뮬레이션을 위한 가상 변수 설정
    // 고정값
    LocalDateTime createdAt = loanLedger.getCreatedAt();
    LocalDateTime loanEndDate = loanLedger.getLoanEndDate();
    int termInMonth =
        (int)
            ChronoUnit.MONTHS.between(
                createdAt.toLocalDate().withDayOfMonth(1),
                loanEndDate.toLocalDate().withDayOfMonth(1));
    BigDecimal principal = loanLedger.getPrincipal();
    BigDecimal annualInterestRate =
        loanLedger.getCompletedInterest().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

    // 반복 갱신할 값
    BigDecimal remainPrincipal = loanLedger.getRemainPrincipal();
    LocalDateTime createdAtForLoop = createdAt.plusMonths(1);

    for (int i = 0; i < termInMonth; i++) {
      MonthlyRepayment repayment =
          calculator.calculate(
              principal,
              remainPrincipal,
              annualInterestRate,
              termInMonth,
              i,
              createdAtForLoop,
              loanEndDate);
      fullSchedule.add(repayment);

      // 남은 원금 갱신
      remainPrincipal = remainPrincipal.subtract(repayment.getPrincipalPayment());

      // 다음 상환일 1개월 후로 갱신
      createdAtForLoop = createdAtForLoop.plusMonths(1);
    }

    return fullSchedule;
  }

  public List<InterestDetailResponse> calculateRemainingInterests(LoanLedger loanLedger) {
    List<MonthlyRepayment> fullSchedule = calculateFullSchedule(loanLedger);
    LocalDateTime nextRepaymentDate = loanLedger.getNextRepaymentDate();

    return fullSchedule.stream()
        .filter(detail -> !detail.getRepaymentDate().isBefore(nextRepaymentDate))
        .map(
            detail ->
                InterestDetailResponse.builder()
                    .interest(detail.getInterestPayment())
                    .repaymentDate(detail.getRepaymentDate())
                    .build())
        .collect(Collectors.toList());
  }
}
