package com.fisa.bank.domains.loan.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
public class PrepaymentInfoResponse {
  private final BigDecimal earlyRepayment;
  private final List<InterestDetailResponse> interestDetailResponses;
}
