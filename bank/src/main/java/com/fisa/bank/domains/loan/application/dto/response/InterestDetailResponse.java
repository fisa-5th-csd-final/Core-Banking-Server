package com.fisa.bank.domains.loan.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class InterestDetailResponse {
  private final LocalDateTime repaymentDate;
  private final BigDecimal interest;
}
