package com.fisa.bank.user.persistence.entity.id;

import lombok.*;

import com.fisa.bank.common.persistence.id.BaseId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserId extends BaseId<Long> {

  private UserId(Long value) {
    super(value);
  }

  public static UserId of(Long value) {
    return new UserId(value);
  }
}
