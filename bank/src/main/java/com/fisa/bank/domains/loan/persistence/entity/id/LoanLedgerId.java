package com.fisa.bank.domains.loan.persistence.entity.id;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.fisa.bank.domains.common.persistence.id.BaseId;

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
