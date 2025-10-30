package com.fisa.bank.account.application.dto.response;

import com.fisa.bank.account.persistence.entity.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountDetailResponse(
        Long accountId,
        String accountNumber,
        String ownerName,
        String bankCode,
        BigDecimal balance,
        LocalDateTime createdAt
) {
    public static AccountDetailResponse of(Account account) {
        return new AccountDetailResponse(
                account.getAccountId().getValue(),
                account.getAccountNumber(),
                account.getUser().getName(),
                account.getBankCode(),
                account.getBalance(),
                account.getCreatedAt()
        );
    }
}
