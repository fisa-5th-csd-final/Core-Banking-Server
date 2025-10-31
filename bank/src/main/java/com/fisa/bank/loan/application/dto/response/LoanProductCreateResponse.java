package com.fisa.bank.loan.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import com.fisa.bank.loan.persistence.entity.id.LoanProductId;
import com.fisa.bank.loan.persistence.enums.LoanType;

@Getter
@Builder
public class LoanProductCreateResponse {
  private final LoanProductId loanProductId;

  private final String name;

  private final LoanType type;
}
