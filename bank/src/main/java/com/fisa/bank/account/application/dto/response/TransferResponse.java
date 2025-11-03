package com.fisa.bank.account.application.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

import com.fisa.bank.account.persistence.entity.Account;

@Builder
public record TransferResponse(
        String fromAccountNumber,
        String toAccountNumber,
        BigDecimal amount,
        BigDecimal fromBalanceAfter,
        BigDecimal toBalanceAfter,
        String message) {

    // 같은 은행끼리 거래 시 팩토리 메서드
    public static TransferResponse of(Account fromAccount, Account toAccount, BigDecimal amount) {
        return TransferResponse.builder()
                .fromAccountNumber(fromAccount.getAccountNumber())
                .toAccountNumber(toAccount.getAccountNumber())
                .amount(amount)
                .fromBalanceAfter(fromAccount.getBalance())
                .toBalanceAfter(toAccount.getBalance())
                .message("이체가 성공적으로 완료되었습니다.")
                .build();
    }

    // 타행 송금용 팩토리 메서드
    public static TransferResponse ofExternal(Account fromAccount, Account toAccount, BigDecimal amount) {
        return TransferResponse.builder()
                .fromAccountNumber(fromAccount.getAccountNumber())
                .toAccountNumber(toAccount.getAccountNumber())  // 타행이므로 ID 없음
                .amount(amount)
                .fromBalanceAfter(fromAccount.getBalance())
                .toBalanceAfter(null)  // 타행이므로 잔액 알 수 없음
                .message("타행 이체가 성공적으로 완료되었습니다.")
                .build();
    }
}
