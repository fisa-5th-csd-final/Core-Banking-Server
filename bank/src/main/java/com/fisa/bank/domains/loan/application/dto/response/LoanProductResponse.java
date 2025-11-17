package com.fisa.bank.domains.loan.application.dto.response;

import lombok.Getter;

import com.fisa.bank.domains.interest.application.dto.response.InterestRateResponse;
import com.fisa.bank.domains.loan.persistence.entity.LoanProduct;
import com.fisa.bank.domains.loan.persistence.enums.LoanType;

@Getter
public class LoanProductResponse {

  private final Long id;
  private final String name;
  private final LoanType type;
  private final InterestRateResponse interestRateResponse;

  public static LoanProductResponse from(
      LoanProduct entity, InterestRateResponse interestRateResponse) {
    return new LoanProductResponse(
        entity.getLoanProductId().getValue(),
        entity.getName(),
        entity.getType(),
        interestRateResponse // 가장 최신 금리만 저장
        );
  }

  public LoanProductResponse(
      Long id, String name, LoanType type, InterestRateResponse interestRateResponse) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.interestRateResponse = interestRateResponse;
  }
}
