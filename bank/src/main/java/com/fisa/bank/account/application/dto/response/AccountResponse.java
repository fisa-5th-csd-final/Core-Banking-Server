package com.fisa.bank.account.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import com.fisa.bank.account.persistence.entity.Account;

@Getter
@Builder
@AllArgsConstructor
public class AccountResponse {
  private String accountNumber;
  private String bankCode;
  private String message;

  public static AccountResponse of(Account entity, String message) {
    return AccountResponse.builder()
        .accountNumber(entity.getAccountNumber())
        .bankCode(entity.getBankCode())
        .message(message)
        .build();
  }
}
