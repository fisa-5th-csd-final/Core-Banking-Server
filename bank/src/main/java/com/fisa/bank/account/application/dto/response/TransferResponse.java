package com.fisa.bank.account.application.dto.response;

import java.math.BigDecimal;

import com.fisa.bank.account.persistence.entity.Account;

public record TransferResponse(
    String fromAccountNumber,
    String toAccountNumber,
    BigDecimal amount,
    BigDecimal fromBalanceAfter,
    BigDecimal toBalanceAfter,
    String message) {

  // 같은 은행끼리 거래 시 팩토리 메서드
  public static TransferResponse of(Account fromAccount, Account toAccount, BigDecimal amount) {
    return new TransferResponse(
        fromAccount.getAccountNumber(),
        toAccount.getAccountNumber(),
        amount,
        fromAccount.getBalance(),
        toAccount.getBalance(),
        "이체가 성공적으로 완료되었습니다.");
  }

  // 타행 송금용 팩토리 메서드
  public static TransferResponse ofExternal(
      Account fromAccount, String toAccountNumber, BigDecimal amount) {
    return new TransferResponse(
        fromAccount.getAccountNumber(),
        toAccountNumber,
        amount,
        fromAccount.getBalance(),
        null, // 타행이므로 잔액 알 수 없음
        "타행 이체가 성공적으로 완료되었습니다.");
  }
}
