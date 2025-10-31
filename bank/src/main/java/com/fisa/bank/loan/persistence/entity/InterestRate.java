package com.fisa.bank.loan.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fisa.bank.common.persistence.entity.BaseEntity;
import com.fisa.bank.loan.persistence.entity.id.InterestRateId;
import com.fisa.bank.loan.persistence.entity.id.InterestRateIdJavaType;

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
  @JavaType(InterestRateIdJavaType.class)
  @JdbcTypeCode(SqlTypes.BIGINT)
  private InterestRateId interestRateId;

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
