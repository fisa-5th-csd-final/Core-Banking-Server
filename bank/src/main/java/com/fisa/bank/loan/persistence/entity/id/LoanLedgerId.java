package com.fisa.bank.loan.persistence.entity.id;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.fisa.bank.common.persistence.id.BaseId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoanLedgerId extends BaseId<Long> {
  private LoanLedgerId(Long value) {
    super(value);
  }

  public static LoanLedgerId of(Long value) {
    return new LoanLedgerId(value);
  }
  ;
}
