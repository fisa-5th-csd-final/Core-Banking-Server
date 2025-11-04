package com.fisa.bank.account.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fisa.bank.account.persistence.entity.AccountTransaction;

/** 단일 거래내역 응답 DTO */
public record AccountTransactionResponse(
    String trxId, // 거래 ID
    LocalDateTime date, // 거래 일시
    String type, // 거래 유형 (DEPOSIT, WITHDRAWAL 등)
    BigDecimal amount, // 거래 금액
    BigDecimal balanceAfter // 거래 후 잔액
    ) {

  public static AccountTransactionResponse from(AccountTransaction transaction) {
    return new AccountTransactionResponse(
        transaction.getTrxAId().getValue().toString(),
        transaction.getDate(),
        transaction.getType().name(),
        transaction.getAmount(),
        transaction.getBalanceAfter());
  }
}
