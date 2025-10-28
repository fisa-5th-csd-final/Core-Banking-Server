package com.fisa.bank.loan.persistence.entity;

import com.fisa.bank.loan.persistence.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
    대출 이력성 테이블
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class TransactionLoan {

    // 거래 id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trxLId;

    // LoanLedger 1 : N TranscationLoan
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
    @Column(nullable = false)
    private BigDecimal repaymentInterestAmount;

    // 원금 납입액
    @Column(nullable = false)
    private BigDecimal repaymentPrincipalAmount;

    // 거래 후 남은 상환액(원금)
    @Column(nullable = false)
    private BigDecimal remainPrincipal;
}
