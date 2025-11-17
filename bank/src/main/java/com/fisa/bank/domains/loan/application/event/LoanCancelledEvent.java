package com.fisa.bank.domains.loan.application.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

import com.fisa.bank.domains.account.persistence.entity.Account;

@Getter
@AllArgsConstructor
public class LoanCancelledEvent {
  private final Account account;
  private final BigDecimal amount;
  private final String loanName;
}
