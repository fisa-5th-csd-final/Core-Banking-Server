package com.fisa.bank.account.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import com.fisa.bank.account.persistence.entity.Account;

@Getter
@Builder
@AllArgsConstructor
public class AccountResponse {
  private Long accountId;
  private String accountNumber;
  private Long userId;
  private String bankCode;
  private String message;

  public static AccountResponse of(Account entity, String message) {
    return AccountResponse.builder()
        .accountId(entity.getAccountId().getValue())
        .accountNumber(entity.getAccountNumber())
        .userId(entity.getUser().getUserId().getValue())
        .bankCode(entity.getBankCode())
        .message(message)
        .build();
  }
}
