package com.fisa.bank.loan.persistence.entity;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OrderBy;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fisa.bank.interest.persistence.entity.InterestRate;
import com.fisa.bank.loan.persistence.entity.id.LoanProductId;
import com.fisa.bank.loan.persistence.entity.id.LoanProductIdJavaType;
import com.fisa.bank.loan.persistence.enums.LoanType;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoanProduct {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JavaType(LoanProductIdJavaType.class)
  @JdbcTypeCode(SqlTypes.BIGINT)
  private LoanProductId loanProductId;

  // LoanProduct 1 : N LoanLedger
  @OneToMany(mappedBy = "loanProduct")
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
