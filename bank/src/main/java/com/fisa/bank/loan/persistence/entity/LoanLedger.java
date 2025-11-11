package com.fisa.bank.loan.persistence.entity;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import com.fisa.bank.account.persistence.entity.Account;
import com.fisa.bank.common.persistence.entity.BaseEntity;
import com.fisa.bank.loan.application.model.UpdateLoanLedgerParam;
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
@FilterDef(
    name = "ldeletedFilter", // 정의할 필터 이름
    parameters = @ParamDef(name = "isDeleted", type = Boolean.class) // 필터에 사용될 파라미터
    )
// 실제 적용되는 핕터
@Filter(
    name = "ldeletedFilter", // 적용할 필터 이름
    condition = "is_deleted = :isDeleted" // 필터 조건 - sql 실행 시 해당 조건에 따라 실행
    )
public class LoanLedger extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JavaType(LoanLedgerIdJavaType.class)
  @JdbcTypeCode(SqlTypes.BIGINT)
  private LoanLedgerId loanLedgerId;

  // LoanLedger 1 : N LoanTransaction
  @OneToMany(mappedBy = "loanLedger", cascade = CascadeType.REMOVE, orphanRemoval = true)
  @Builder.Default
  private List<LoanTransaction> loanTransactionList = new ArrayList<>();

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
  private LocalDateTime nextRepaymentDate;
  private LocalDateTime lastRepaymentDate;

  @Column(nullable = false)
  private LocalDateTime loanEndDate;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id")
  private Account account;

  // 연체 일수
  @Column(nullable = false)
  private int overdueCount;

  @Column(nullable = false)
  private int term;

  public void addLoanTransactionList(LoanTransaction loanTransaction) {
    loanTransactionList.add(loanTransaction);
  }

  public void updateLoanLedger(UpdateLoanLedgerParam updateLoanLedgerParam) {
    this.remainPrincipal = updateLoanLedgerParam.getRemainPrincipal();
    this.nextRepaymentDate = updateLoanLedgerParam.getNextRepaymentDate();
    this.lastRepaymentDate = updateLoanLedgerParam.getLastRepaymentDate();
    this.repaymentStatus = updateLoanLedgerParam.getStatus();
  }

  public static LoanLedger createLoanLedger(
      LoanProduct loanProduct,
      User user,
      BigDecimal completedInterest,
      BigDecimal principal,
      BigDecimal remainPrincipal,
      RepaymentType repaymentType,
      LocalDateTime nextRepaymentDate,
      LocalDateTime loanEndDate,
      InterestType interestType,
      BigDecimal earlyRepayInterestRate,
      int term,
      Account account) {
    return LoanLedger.builder()
        .loanProduct(loanProduct)
        .user(user)
        .completedInterest(completedInterest)
        .principal(principal)
        .remainPrincipal(remainPrincipal)
        .repaymentType(repaymentType)
        .repaymentStatus(RepaymentStatus.NORMAL)
        .nextRepaymentDate(nextRepaymentDate)
        .loanEndDate(loanEndDate)
        .overdueCount(0)
        .interestType(interestType)
        .earlyRepayInterestRate(earlyRepayInterestRate)
        .term(term)
        .account(account)
        .build();
  }
}
