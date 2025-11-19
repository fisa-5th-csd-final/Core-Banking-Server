package com.fisa.bank.domains.loan.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

import com.fisa.bank.domains.loan.persistence.enums.InterestType;
import com.fisa.bank.domains.loan.persistence.enums.RepaymentType;

@Getter
public class LoanApplyForRequest {
  // 받아야 하는 데이터
  // 유저 아이디

  // 원금, 금리 유형
  @NotNull(message = "원금을 입력해주세요.")
  private BigDecimal principal;

  @NotNull(message = "금리 유형을 입력해주세요.")
  private InterestType interestType;

  // 상환 방법(원리금, 원금, 만기)
  @NotNull(message = "상환 방법을 입력해주세요.")
  private RepaymentType repaymentType;

  @NotNull(message = "상환 기간을 입력해주세요.")
  @Min(value = 1, message = "상환 기간은 최소 1년 이상이어야 합니다.")
  private Integer term;

  @NotBlank(message = "계좌 번호를 입력해주세요")
  private String accountNumber;

  @NotNull(message = "자동 예치 여부를 입력해주세요.")
  private Boolean autoDepositEnabled;
}
