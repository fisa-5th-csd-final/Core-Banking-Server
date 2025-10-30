package com.fisa.bank.account.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountDetailResponse(
        Long accountId,
        String accountNumber,
        String ownerName,
        String bankCode,
        BigDecimal balance,
        LocalDateTime createdAt
) {}
