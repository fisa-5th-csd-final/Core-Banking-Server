package com.fisa.bank.domains.loan.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// 우대 금리 매핑 테이블 - 신용 등급, 고객 등급에 따라 고정된 값 반환
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PreferInterest {

  @EmbeddedId private PreferInterestCompositeKey id;

  // 우대금리
  @Column(nullable = false)
  private BigDecimal preferInterest;

  // 복합키와 우대 금리 초기화 생성자
  public PreferInterest(PreferInterestCompositeKey id, BigDecimal preferInterest) {
    this.id = id;
    this.preferInterest = preferInterest;
  }
}
