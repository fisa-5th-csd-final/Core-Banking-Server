package com.fisa.bank.domains.loan.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class AutoDepositResponse {
  private String loanName;
  private BigDecimal accountBalance;
  private boolean autoDepositEnabled;
}
