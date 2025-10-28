package com.fisa.bank.loan.persistence.entity;


import com.fisa.bank.common.persistence.entity.BaseEntity;
import com.fisa.bank.loan.persistence.repository.InterestRateRepository;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/*
    금리 테이블
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InterestRate extends BaseEntity {
    // 금리 id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interestRateId;

    // 대출 상품 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loanProductId", nullable = false)
    LoanProduct loanProduct;

    // 기준 금리
    @Column(nullable = false)
    private java.math.BigDecimal baseInterest;

    // 가산 금리
    @Column(nullable = false)
    private java.math.BigDecimal addInterest;

    // 우대 금리 상한
    @Column(nullable = false)
    private java.math.BigDecimal limitPreferInterest;

}
