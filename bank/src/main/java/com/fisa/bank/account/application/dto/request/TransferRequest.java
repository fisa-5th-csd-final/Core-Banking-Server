package com.fisa.bank.account.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(
        Long fromAccountId,
        Long toAccountId,
        @NotNull(message = "송금 금액은 null값이 허용되지 않습니다")
        @Positive(message = "송금 금액은 0보다 커야 합니다")
        BigDecimal amount
) {}