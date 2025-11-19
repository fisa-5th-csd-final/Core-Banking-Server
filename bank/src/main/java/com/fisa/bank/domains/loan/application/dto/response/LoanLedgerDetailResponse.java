package com.fisa.bank.domains.loan.application.dto.response;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fisa.bank.domains.loan.application.model.MonthlyRepayment;
import com.fisa.bank.domains.loan.persistence.entity.LoanLedger;
import com.fisa.bank.domains.loan.persistence.enums.LoanType;
import com.fisa.bank.domains.loan.persistence.enums.RepaymentType;
import com.fisa.bank.domains.loan.persistence.enums.RepaymentStatus;

@Getter
public class LoanLedgerDetailResponse {
  // 대출 이름, 남은 원금, 원금, 월 상환액, 상환 계좌, 대출 유형, 상환 방식 응답

  private final String name;
  private final BigDecimal remainPrincipal;
  private final BigDecimal principal;
  private final BigDecimal monthlyRepayment;
  private final String accountNumber;
  private final LoanType loanType;
  private final RepaymentType repaymentType;
  private final Boolean autoDepositEnabled;
  private final LocalDateTime lastRepaymentDate;
  private final LocalDateTime createdAt;
  private final int term;
  private final RepaymentStatus repaymentStatus;

  public LoanLedgerDetailResponse(
      String name,
      BigDecimal principal,
      BigDecimal remainPrincipal,
      LoanType loanType,
      RepaymentType repaymentType,
      BigDecimal monthlyRepayment,
      String accountNumber,
      Boolean autoDepositEnabled,
      LocalDateTime lastRepaymentDate,
      LocalDateTime createdAt,
      int term,
      RepaymentStatus repaymentStatus) {
    this.name = name;
    this.principal = principal;
    this.remainPrincipal = remainPrincipal;
    this.loanType = loanType;
    this.repaymentType = repaymentType;
    this.monthlyRepayment = monthlyRepayment;
    this.accountNumber = accountNumber;
    this.autoDepositEnabled = autoDepositEnabled;
    this.lastRepaymentDate = lastRepaymentDate;
    this.createdAt = createdAt;
    this.term = term;
    this.repaymentStatus = repaymentStatus;
  }

  public static LoanLedgerDetailResponse from(
      LoanLedger loanLedger, MonthlyRepayment monthlyRepayment) {
    return new LoanLedgerDetailResponse(
        loanLedger.getLoanProduct().getName(), // 엔티티 구조에 맞게 수정
        loanLedger.getPrincipal(),
        loanLedger.getRemainPrincipal(),
        loanLedger.getLoanProduct().getType(),
        loanLedger.getRepaymentType(),
        monthlyRepayment.getMonthlyPayment(),
        loanLedger.getAccount().getAccountNumber(),
        loanLedger.isAutoDepositEnabled(),
        loanLedger.getLastRepaymentDate(),
        loanLedger.getCreatedAt(),
        loanLedger.getTerm(),
        loanLedger.getRepaymentStatus());
  }
}
