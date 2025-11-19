package com.fisa.bank.domains.account.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fisa.bank.domains.account.persistence.entity.Account;

public record AccountDetailResponse(
    Long accountId,
    String accountNumber,
    String ownerName,
    String bankCode,
    BigDecimal balance,
    LocalDateTime createdAt,
    boolean isInCome) {
  public static AccountDetailResponse from(Account account) {
    return new AccountDetailResponse(
        account.getAccountId().getValue(),
        account.getAccountNumber(),
        account.getUser().getName(),
        account.getBankCode(),
        account.getBalance(),
        account.getCreatedAt(),
            account.isIncome());
  }
}
