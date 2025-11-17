package com.fisa.bank.domains.interest.application.dto.response;

import lombok.Getter;

import java.math.BigDecimal;

import com.fisa.bank.domains.interest.persistence.entity.InterestRate;

@Getter
public class InterestRateResponse {
  private final BigDecimal baseInterest;
  private final BigDecimal addInterest;
  private final BigDecimal limitPreferInterest;

  public InterestRateResponse(
      BigDecimal baseInterest, BigDecimal addInterest, BigDecimal limitPreferInterest) {
    this.baseInterest = baseInterest;
    this.addInterest = addInterest;
    this.limitPreferInterest = limitPreferInterest;
  }

  public static InterestRateResponse from(InterestRate entity) {
    return new InterestRateResponse(
        entity.getBaseInterest(), entity.getAddInterest(), entity.getLimitPreferInterest());
  }
}
