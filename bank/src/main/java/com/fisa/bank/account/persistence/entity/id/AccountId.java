package com.fisa.bank.account.persistence.entity.id;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.fisa.bank.common.persistence.id.BaseId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccountId extends BaseId<Long> {
  // 생성자
  private AccountId(Long value) {
    super(value);
  }

  // 팩토리 메서드
  public static AccountId of(Long value) {
    return new AccountId(value);
  }
}
