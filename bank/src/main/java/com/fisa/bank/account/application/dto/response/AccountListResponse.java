package com.fisa.bank.account.application.dto.response;

import com.fisa.bank.account.persistence.entity.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountListResponse(
        Long accountId,
        String accountNumber,
        BigDecimal balance,
        LocalDateTime createdAt
) {
    public static AccountListResponse of(Account account) {
        return new AccountListResponse(
                account.getAccountId().getValue(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCreatedAt()
        );
    }
}
