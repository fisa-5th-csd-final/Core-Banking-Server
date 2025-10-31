package com.fisa.bank.loan.persistence.entity.id;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.fisa.bank.common.persistence.id.BaseId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoanProductId extends BaseId<Long> {
  private LoanProductId(Long value) {super(value);}

  public static LoanProductId of(Long value) {return new LoanProductId(value);}
  ;
}
