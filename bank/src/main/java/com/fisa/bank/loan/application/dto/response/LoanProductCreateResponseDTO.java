package com.fisa.bank.loan.application.dto.response;

import com.fisa.bank.loan.persistence.entity.InterestRate;
import com.fisa.bank.loan.persistence.entity.LoanLedger;
import com.fisa.bank.loan.persistence.enums.LoanType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public class LoanProductCreateResponseDTO {
    private final long loanProductId;

    private final String name;

    private final LoanType type;

}
