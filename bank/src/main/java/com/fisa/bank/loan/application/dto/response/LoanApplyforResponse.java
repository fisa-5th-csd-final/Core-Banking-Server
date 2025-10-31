package com.fisa.bank.loan.application.dto.response;

import com.fisa.bank.loan.persistence.enums.LoanType;
import com.fisa.bank.loan.persistence.enums.RepaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class LoanApplyforResponse {
//    private final Long loanProductId;

    private final String name;

//    private final LoanType type;
//
//    private final BigDecimal completedInterest;
//
//    private final BigDecimal principal;
//
//    private final BigDecimal remainPricipal;
//
//    private final LocalDateTime nextRepaymentDate;
//
//    private final LocalDateTime loanEndDate;
//
//    private final RepaymentStatus repaymentStatus;
//
//    private final BigDecimal earlyRepayInterestRate;
//
//    private final int overdueCount;
}
