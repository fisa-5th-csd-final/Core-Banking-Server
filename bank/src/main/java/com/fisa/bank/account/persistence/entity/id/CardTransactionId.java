package com.fisa.bank.account.persistence.entity.id;

import com.fisa.bank.common.persistence.id.BaseId;

public class CardTransactionId extends BaseId<Long> {
  private CardTransactionId(Long value) {
    super(value);
  }

  public static CardTransactionId of(Long value) {
    return new CardTransactionId(value);
  }
}
