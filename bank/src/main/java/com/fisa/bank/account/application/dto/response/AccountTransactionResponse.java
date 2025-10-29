package com.fisa.bank.account.application.dto.response;

import com.fisa.bank.account.persistence.entity.AccountTransaction;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AccountTransactionResponse(
        Long transactionId,     // 거래 ID
        Long accountId,           // 계좌 ID
        String type,              // 거래 타입 (DEPOSIT, WITHDRAW)
        BigDecimal amount,        // 거래 금액
        BigDecimal balanceBefore, // 거래 전 잔액
        BigDecimal balanceAfter,  // 거래 후 잔액
        boolean isIncome,         // 입금 여부
        LocalDateTime transactionAt // 거래 일시
) {
    public static AccountTransactionResponse of(AccountTransaction entity) {
        return AccountTransactionResponse.builder()
                .transactionId(entity.getTrxAId().getValue())
                .accountId(entity.getAccount().getAccountId().getValue())
                .type(entity.getType().name())
                .amount(entity.getAmount())
                .balanceBefore(entity.getBalanceBefore())
                .balanceAfter(entity.getBalanceAfter())
                .isIncome(entity.getIsIncome())
                .transactionAt(entity.getDate())
                .build();
    }
}
