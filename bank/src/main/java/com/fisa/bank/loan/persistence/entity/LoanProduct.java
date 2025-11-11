package com.fisa.bank.loan.persistence.entity;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OrderBy;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import com.fisa.bank.common.persistence.entity.BaseEntity;
import com.fisa.bank.interest.persistence.entity.InterestRate;
import com.fisa.bank.loan.persistence.entity.id.LoanProductId;
import com.fisa.bank.loan.persistence.entity.id.LoanProductIdJavaType;
import com.fisa.bank.loan.persistence.enums.LoanType;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FilterDef(
    name = "deletedFilter", // 정의할 필터 이름
    parameters = @ParamDef(name = "isDeleted", type = Boolean.class) // 필터에 사용될 파라미터
    )
// 실제 적용되는 핕터
@Filter(
    name = "deletedFilter", // 적용할 필터 이름
    condition = "is_deleted = :isDeleted" // 필터 조건 - sql 실행 시 해당 조건에 따라 실행
    )
public class LoanProduct extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JavaType(LoanProductIdJavaType.class)
  @JdbcTypeCode(SqlTypes.BIGINT)
  private LoanProductId loanProductId;

  // LoanProduct 1 : N LoanLedger
  @OneToMany(mappedBy = "loanProduct", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<LoanLedger> loanLedgerList = new ArrayList<>();

  // LoanProduct 1 : N com.fisa.bank.interest.persistence.entity.InterestRate
  @OneToMany(mappedBy = "loanProduct", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  @OrderBy("createdAt DESC") // 최신 순으로 정렬
  private List<InterestRate> interestRateList = new ArrayList<>();

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private LoanType type;
}
