package com.fisa.bank.domains.interest.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fisa.bank.domains.common.persistence.entity.BaseEntity;
import com.fisa.bank.domains.interest.persistence.id.InterestRateId;
import com.fisa.bank.domains.interest.persistence.id.InterestRateIdJavaType;
import com.fisa.bank.domains.loan.persistence.entity.LoanProduct;

/*
   금리 테이블
*/
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
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
  private BigDecimal baseInterest;

  // 가산 금리
  @Column(nullable = false)
  private BigDecimal addInterest;

  // 우대 금리 상한
  @Column(nullable = false)
  private BigDecimal limitPreferInterest;
}
