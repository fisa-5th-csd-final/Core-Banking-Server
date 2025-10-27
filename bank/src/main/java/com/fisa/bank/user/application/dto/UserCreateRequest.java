package com.fisa.bank.user.application.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fisa.bank.common.presentation.util.EpochTimeToLocalDateTimeSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

public record UserCreateRequest(

        @JsonDeserialize(using = EpochTimeToLocalDateTimeSerializer.class)
        @NotBlank @NotNull @PositiveOrZero LocalDateTime birthday,

        @NotBlank @NotNull String name,
        @NotBlank @NotNull String loginId,
        @NotBlank @NotNull String password,
        @NotBlank @NotNull String address,
        @NotBlank @NotNull String job,
        @PositiveOrZero long salary
) {
}
