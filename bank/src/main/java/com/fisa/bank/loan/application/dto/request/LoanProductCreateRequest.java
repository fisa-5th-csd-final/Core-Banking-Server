package com.fisa.bank.loan.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

import com.fisa.bank.loan.persistence.enums.LoanType;

@Getter
public class LoanProductCreateRequest {
  @NotBlank(message = "대출 상품 이름을 입력하세요.")
  private String name;

  @NotNull(message = "대출 유형을 입력하세요.")
  private LoanType type;

  @NotNull(message = "대출 가산 금리를 입력하세요.")
  private BigDecimal addInterest;

  @NotNull(message = "대출 우대금리 상한을 입력하세요.")
  private BigDecimal limitPreferInterest;
}
