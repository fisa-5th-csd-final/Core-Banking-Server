package com.fisa.bank.domains.loan.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanAutoDepositUpdateRequest {
<<<<<<< HEAD
  @NotNull(message = "자동 예치 여부를 선택하세요.")
=======
>>>>>>> 4231628 ([REFACTOR] spotless 적용)
  private Boolean autoDepositEnabled;
}
