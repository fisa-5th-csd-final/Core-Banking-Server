package com.fisa.bank.domains.user.persistence.entity.id;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.fisa.bank.domains.common.persistence.id.BaseId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserId extends BaseId<Long> {

  private UserId(Long value) {
    super(value);
  }

  public static UserId of(Long value) {
    return new UserId(value);
  }
}
