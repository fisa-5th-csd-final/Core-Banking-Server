package com.fisa.bank.loan.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fisa.bank.loan.persistence.entity.id.LoanTransactionId;
import com.fisa.bank.loan.persistence.entity.id.LoanTransactionIdJavaType;
import com.fisa.bank.loan.persistence.enums.TransactionType;

/*
   대출 이력성 테이블
*/
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Builder
public class LoanTransaction {

  // 거래 id
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JavaType(LoanTransactionIdJavaType.class)
  @JdbcTypeCode(SqlTypes.BIGINT)
  private LoanTransactionId trxLId;

  // LoanLedger 1 : N TransactionLoan
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "loanLedgerId", nullable = false)
  private LoanLedger loanLedger;

  // 거래 일시
  @Column(nullable = false)
  private LocalDateTime date;

  // 거래 유형
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionType transactionType;

  // 거래 금액
  @Column(nullable = false)
  private BigDecimal amount;

  // 이자 납입액
  private BigDecimal repaymentInterestAmount;

  // 원금 납입액
  private BigDecimal repaymentPrincipalAmount;

  // 거래 후 남은 상환액(원금)
  @Column(nullable = false)
  private BigDecimal remainPrincipal;

  public void setLoanLedger(LoanLedger loanLedger) {
      this.loanLedger = loanLedger;
  }
}
