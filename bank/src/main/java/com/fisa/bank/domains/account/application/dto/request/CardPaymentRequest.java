package com.fisa.bank.domains.account.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

import com.fisa.bank.domains.account.persistence.enums.ConsumptionCategory;

public record CardPaymentRequest(
    @Positive BigDecimal amount,
    @NotBlank String storeName,
    @NotNull ConsumptionCategory category) {}
