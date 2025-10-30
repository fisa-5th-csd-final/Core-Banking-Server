package com.fisa.bank.account.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountBalanceResponse(
        Long accountId,
        String accountNumber,
        BigDecimal balance,
        String ownerName,
        LocalDateTime retrievedAt
) {}

