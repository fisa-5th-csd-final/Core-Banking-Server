package com.fisa.bank.domains.loan.application.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanAutoDepositUpdateRequest {
    private Boolean autoDepositEnabled;
}

