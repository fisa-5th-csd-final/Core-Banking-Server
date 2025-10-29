package com.fisa.bank.loan.application.dto.request;

import com.fisa.bank.loan.persistence.enums.LoanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LoanProductCreateRequest {
    @NotBlank
    private String name;

    @NotNull
    private LoanType type;
}
