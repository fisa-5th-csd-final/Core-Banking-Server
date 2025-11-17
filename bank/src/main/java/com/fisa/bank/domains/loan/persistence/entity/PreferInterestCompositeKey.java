package com.fisa.bank.domains.loan.persistence.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import com.fisa.bank.domains.user.persistence.entity.CreditRating;
import com.fisa.bank.domains.user.persistence.entity.CustomerLevel;

/*
   우대금리 매핑 테이블의 복합키
*/

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // 복합키의 일치 여부를 확인하기 위한 어노테이션. 만약 없다면, 객체의 값이 아니라 주소를 비교하게 돼서 일치 확인이 안 됨.
@Embeddable // Entity의 포함될 수 있는 타입임을 명시하는 어노테이션
@Builder
public class PreferInterestCompositeKey implements Serializable {

  // 신용 등급
  @Enumerated(EnumType.STRING)
  private CreditRating creditRating;

  // 고객 등급
  @Enumerated(EnumType.STRING)
  private CustomerLevel customerLevel;
}
