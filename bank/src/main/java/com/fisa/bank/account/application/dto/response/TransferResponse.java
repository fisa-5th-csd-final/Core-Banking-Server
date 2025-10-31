package com.fisa.bank.account.application.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

import com.fisa.bank.account.persistence.entity.Account;

@Builder
public record TransferResponse(
    Long fromAccountId,
    Long toAccountId,
    BigDecimal amount,
    BigDecimal fromBalanceAfter,
    BigDecimal toBalanceAfter,
    String message) {
  public static TransferResponse of(Account from, Account to, BigDecimal amount) {
    return TransferResponse.builder()
        .fromAccountId(from.getAccountId().getValue())
        .toAccountId(to.getAccountId().getValue())
        .amount(amount)
        .fromBalanceAfter(from.getBalance())
        .toBalanceAfter(to.getBalance())
        .message("이체가 성공적으로 완료되었습니다.")
        .build();
  }
}
