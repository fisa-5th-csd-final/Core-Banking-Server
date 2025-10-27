package com.fisa.bank.account.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AccountResponse {
    private Long accountId;
    private String accountNumber;
    private Long userId;
    private String bankCode;
    private String message;
}
