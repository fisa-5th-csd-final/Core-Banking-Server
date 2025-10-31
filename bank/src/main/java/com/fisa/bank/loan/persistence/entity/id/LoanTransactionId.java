package com.fisa.bank.loan.persistence.entity.id;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.fisa.bank.common.persistence.id.BaseId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoanTransactionId extends BaseId<Long> {
  private LoanTransactionId(Long value) {
    super(value);
  }

  public static LoanTransactionId of(Long value) {
    return new LoanTransactionId(value);
  }
  ;
}
