package com.fisa.bank.loan.application.dto.request;

import com.fisa.bank.loan.persistence.enums.InterestType;
import com.fisa.bank.loan.persistence.enums.RepaymentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

}
