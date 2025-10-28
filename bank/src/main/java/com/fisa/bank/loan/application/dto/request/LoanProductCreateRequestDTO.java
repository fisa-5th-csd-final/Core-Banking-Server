package com.fisa.bank.loan.application.dto.request;

import com.fisa.bank.loan.persistence.enums.LoanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoanProductCreateRequestDTO {
    @NotBlank
    private String name;

    @NotNull
    private LoanType type;
}
