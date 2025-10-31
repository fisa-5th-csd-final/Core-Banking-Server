package com.fisa.bank.loan.application.dto.request;

import com.fisa.bank.loan.persistence.enums.InterestType;
import com.fisa.bank.loan.persistence.enums.RepaymentType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class LoanApplyForRequest {
    // 받아야 하는 데이터
    // 유저 아이디

    // 원금, 금리 유형
    @NotNull
    private BigDecimal principal;

    @NotNull
    private InterestType interestType;

    // 상환 방법(원리금, 원금, 만기)
    @NotNull
    private RepaymentType repaymentType;

}
