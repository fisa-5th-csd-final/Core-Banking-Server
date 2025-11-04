package com.fisa.bank.account.application.dto.response;

import com.fisa.bank.account.persistence.entity.Account;

public record AccountResponse(String accountNumber, String bankCode, String message) {

  public static AccountResponse from(Account entity, String message) {
    return new AccountResponse(entity.getAccountNumber(), entity.getBankCode(), message);
  }
}
