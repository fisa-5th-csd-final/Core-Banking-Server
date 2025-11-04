package com.fisa.bank.loan.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

import com.fisa.bank.loan.persistence.enums.LoanType;

@Getter
@Builder
public class LoanProductCreateResponse {
  private final Long loanProductId;

  private final String name;

  private final LoanType type;

  private final BigDecimal addInterest;

  private final BigDecimal limitPreferInterest;
}
