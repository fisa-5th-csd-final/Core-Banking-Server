package com.fisa.bank.account.application.dto.response;

import com.fisa.bank.account.persistence.entity.Account;

public record AccountResponse(
    Long accountId, String accountNumber, String bankCode, String message) {

  public static AccountResponse from(Account entity, String message) {
    return new AccountResponse(
        entity.getAccountId().getValue(), entity.getAccountNumber(), entity.getBankCode(), message);
  }
}
