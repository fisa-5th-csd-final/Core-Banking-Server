package com.fisa.bank.domains.loan.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LoanRepaymentResponse {

  private Long loanLedgerId;
  private LocalDateTime nextRepaymentDate;
  private Boolean autoDepositEnabled;
}
