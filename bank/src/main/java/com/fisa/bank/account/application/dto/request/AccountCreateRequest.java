package com.fisa.bank.account.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountCreateRequest {
    private String accountNumber;
    private Long userId;
    private String bankCode;
}
