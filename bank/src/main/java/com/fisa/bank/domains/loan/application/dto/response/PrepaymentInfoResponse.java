package com.fisa.bank.domains.loan.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
public class PrepaymentInfoResponse {
  private final Long loanLedgerId;
  private BigDecimal balance;
  private final String loanProductName;
  private final BigDecimal earlyRepayment;
  private final BigDecimal mustPaidAmount;
  private final List<InterestDetailResponse> interestDetailResponses;
  private String accountNumber;
}
