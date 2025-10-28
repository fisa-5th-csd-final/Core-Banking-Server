package com.fisa.bank.loan.application.exception;

import com.fisa.bank.common.application.exception.BusinessException;

public class LoanProductException extends RuntimeException {

    public LoanProductException(String message) {
        super(message);
    }

}

