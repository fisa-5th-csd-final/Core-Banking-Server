package com.fisa.bank.loan.application.exception;

import com.fisa.bank.common.application.exception.BusinessException;

import java.math.BigDecimal;

public class InsufficientRepaymentException extends BusinessException {
    private static final String errorCode = "L005";
    private static final String message = "납입 금액이 상환액보다 적습니다. (납입 금액: %s 상환액: %s)";

    public InsufficientRepaymentException(BigDecimal amount, BigDecimal repay) {
        super(errorCode, String.format(message, amount, repay));
    }
}
