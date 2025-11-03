package com.fisa.bank.loan.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fisa.bank.loan.persistence.entity.id.LoanLedgerId;
import com.fisa.bank.loan.persistence.entity.id.LoanLedgerIdJavaType;
import com.fisa.bank.loan.persistence.enums.InterestType;
import com.fisa.bank.loan.persistence.enums.RepaymentStatus;
import com.fisa.bank.loan.persistence.enums.RepaymentType;
import com.fisa.bank.user.persistence.entity.User;

/*
   대출 원장 테이블
*/
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class LoanLedger {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JavaType(LoanLedgerIdJavaType.class)
  @JdbcTypeCode(SqlTypes.BIGINT)
  private LoanLedgerId loanLedgerId;

  // LoanLedger 1 : N LoanTransaction
  @OneToMany(mappedBy = "loanLedger", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<LoanTransaction> loanTransactionList;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "loanProductId", nullable = false)
  private LoanProduct loanProduct;

  // LoanLedger N : 1 User
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  // 최종 금리
  @Column(nullable = false)
  private BigDecimal completedInterest;

  // 원금
  @Column(nullable = false)
  private BigDecimal principal;

  // 남은 원금
  @Column(nullable = false)
  private BigDecimal remainPrincipal;

  // 상환 타입 - 원리금, 원금, 만기
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RepaymentType repaymentType;

  // 상환 상태 - 정상, 연체
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RepaymentStatus repaymentStatus;

  // 금리 유형 - 고정, 변동
  @Enumerated(EnumType.STRING)
  @Column(name = "interest_type", nullable = false)
  private InterestType interestType;

  // 중도 상환 수수료율 - 금리 유형과 대출 유형에 따라 표 참고해서 정하기
  @Column(nullable = false)
  private BigDecimal earlyRepayInterestRate;

  // 다음 상환, 마지막 거래 일시, 상환 마감 기한
  @Column(nullable = false)
  private LocalDateTime nextRepaymentDate;

  private LocalDateTime lastRepaymentDate;

  @Column(nullable = false)
  private LocalDateTime loanEndDate;

  // 연체 일수
  @Column(nullable = false)
  private int overdueCount;

  @Column(nullable = false)
  private int term;
}
