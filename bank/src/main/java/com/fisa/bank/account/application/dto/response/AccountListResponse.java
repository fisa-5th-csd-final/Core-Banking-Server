package com.fisa.bank.account.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountListResponse(
        Long accountId,
        String accountNumber,
        BigDecimal balance,
        LocalDateTime createdAt
) {}
