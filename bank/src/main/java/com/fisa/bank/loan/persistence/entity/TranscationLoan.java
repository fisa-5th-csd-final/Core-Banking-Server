package com.fisa.bank.loan.persistence.entity;

import com.fisa.bank.loan.persistence.enums.TranscationType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/*
    대출 이력성 테이블
 */
@Entity
public class TranscationLoan {

    // 거래 id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long trxLId;

    // LoanLedger 1 : N TranscationLoan
    @ManyToOne
    @JoinColumn(name = "loanLedgerId", nullable = false)
    private LoanLedger loanLedger;

    // 거래 일시
    @Column(nullable = false)
    private LocalDateTime date;

    // 거래 유형
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TranscationType transcationType;

    // 거래 금액
    @Column(nullable = false)
    private long amount;

    // 이자 납입액
    @Column(nullable = false)
    private long repaymentInterestAmount;

    // 원금 납입액
    @Column(nullable = false)
    private long repaymentPrincipalAmount;

    // 거래 후 남은 상환액(원금)
    @Column(nullable = false)
    private long remainPrincipal;
}
