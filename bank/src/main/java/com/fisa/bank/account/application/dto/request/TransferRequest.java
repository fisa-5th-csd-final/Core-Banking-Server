package com.fisa.bank.account.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(
    @NotNull(message = "보내는 분의 계좌 ID는 null일 수 없습니다.") Long fromAccountId,
    @NotNull(message = "받는 분의 계좌 ID는 null일 수 없습니다.") Long toAccountId,
    @NotNull(message = "송금 금액은 null값이 허용되지 않습니다") @Positive(message = "송금 금액은 0보다 커야 합니다")
        BigDecimal amount) {}
