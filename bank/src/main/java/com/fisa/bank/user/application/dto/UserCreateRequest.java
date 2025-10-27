package com.fisa.bank.user.application.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fisa.bank.common.presentation.util.EpochTimeToLocalDateTimeSerializer;
import java.time.LocalDateTime;

public record UserCreateRequest(

        @JsonDeserialize(using = EpochTimeToLocalDateTimeSerializer.class)
        LocalDateTime birthday,

        String name,
        String loginId,
        String password,
        String address,
        String job,
        long salary
) {
}
