package com.fisa.bank.domains.account.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fisa.bank.domains.account.persistence.entity.Account;

public record AccountListResponse(
    Long accountId, String accountNumber, BigDecimal balance, LocalDateTime createdAt) {
  public static AccountListResponse from(Account account) {
    return new AccountListResponse(
        account.getAccountId().getValue(),
        account.getAccountNumber(),
        account.getBalance(),
        account.getCreatedAt());
  }
}
