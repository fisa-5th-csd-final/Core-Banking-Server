package com.fisa.bank.account.application.dto.request;

import com.fisa.bank.account.persistence.enums.ConsumptionCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CardPaymentRequest(
        @Positive BigDecimal amount,
        @NotBlank String storeName,
        @NotNull ConsumptionCategory category
) {}
